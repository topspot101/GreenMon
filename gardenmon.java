import java.util.HashMap;
import com.phidgets.*;
import com.phidgets.event.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.phidgets.TemperatureSensorPhidget;
import com.phidgets.PhidgetException;
public class TemperatureExample {
        private TemperatureSensorPhidget tempSensor;
	public static final void main(String args[]) throws Exception {
		//  Define Phidget interface kit "ik".
		InterfaceKitPhidget ik;
		//  Define IR temperature sensor "tempSensor"
                TemperatureSensorPhidget tempSensor;
		//  Print Phidget library version to console.
		System.out.println(Phidget.getLibraryVersion());
		//  Create a Phidget object called ik.
		ik = new InterfaceKitPhidget();
		//  Create a Phidget object called tempSensor
                tempSensor = new TemperatureSensorPhidget();
		//  Attach Events run when a Phidget is connected to the ik.
		ik.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae) {
				System.out.println("26:  attachment of ik " + ae);
			}
		});
		//  tempSensor attach event listener.
                tempSensor.addAttachListener(new AttachListener() {
                        public void attached(AttachEvent ae) {
                                System.out.println("32:  attachment of tempSensor " + ae);
                        }
                });
		//  Detach Events run when a Phidget is disconnected from the ik.
		ik.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("38: detachment of ik " + ae);
			}
		});
		// tempSensor detach event listener
                tempSensor.addDetachListener(new DetachListener() {
                        public void detached(DetachEvent ae) {
                                System.out.println("44: detachment of tempSensor " + ae);
                        }
                });
		// Error Listener to Catch any errors that may occur on the ik.
	  	ik.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println("50: ik Error "+ee);
			}
		});
		//  Data Event code runs when data from ik changes. 
		ik.addInputChangeListener(new InputChangeListener() {
			public void inputChanged(InputChangeEvent oe) {
				System.out.println("56: ik inputchangelistener "+oe);
			}
		});
		// Output event listener for ik.
		ik.addOutputChangeListener(new OutputChangeListener() {
			public void outputChanged(OutputChangeEvent oe) {
				System.out.println("62: ik outputchangelistener "+oe);
			}
		});
		// tempSensor change event listener.
                tempSensor.addTemperatureChangeListener(new TemperatureChangeListener() {
                         public void temperatureChanged(TemperatureChangeEvent se) {
                        }
                });
		// Open the ik object.
		ik.open(330510);
		// Log to console 
		System.out.println("73: waiting for InterfaceKit attachment...");
		// Wait for ik to be connected.
		ik.waitForAttachment();
		// Log connected ik info to console.
		System.out.println("77: Device Name: "+ik.getDeviceName());
		System.out.println("78: Device Serial: "+ik.getSerialNumber());
		System.out.println("79: Total Outputs: "+ik.getOutputCount());
		System.out.println("80: Total Inputs: "+ik.getInputCount());
                int oneminute=0;
                Runtime pb = Runtime.getRuntime();
		Thread.sleep(500);
		//  Open the tempSensor object.
                tempSensor.open(322809);
		//  Wait for tempSensor to be connected.
                tempSensor.waitForAttachment();
		// Log connected tempSensor infor to console.
                System.out.println("92: Device Name: "+tempSensor.getDeviceName());
                System.out.println("93: Device Serial: "+tempSensor.getSerialNumber());
                Thread.sleep(500);

                boolean test2 = true;
		// Keep program running
                while(test2) {
			// Define "data", get temperature sensor's value from input 0 of ik and devide by 4.095.
                       	HashMap<Integer,Double> data= getReadableValue(1,(Double.valueOf( (ik.getSensorRawValue(0) / 4.095))).intValue() );
			// Define "tempc", degrees Celsius.
                        double tempc = data.get(0).doubleValue();
			// Define "tempf", degrees Fahrenheit.
                        double tempf = data.get(1);
			//  Redefine "data", get humidity sensor's value from input 1 of ik and defide by 4.095
			data= getReadableValue(2,(Double.valueOf( (ik.getSensorRawValue(1) / 4.095))).intValue() );
			//  Define 'humidity', get data from redefined data objcect.
			double humidity = data.get(0);
			//  Define "light", get value from ik input 2 devide by 4.095 and apply sccuracy adjustments.
			double light = (double)Math.round(100D*(double)Math.exp(0.02357*(ik.getSensorRawValue(2) / 4.095)+-0.7853))/100D;
			// Define "vpd", use tempc and humidity to calculate vapor-pressure deficit.
                        double vpd = (double)Math.round(((0.6108*(double)Math.exp((17.27*tempc)/(tempc+237.3))*(100-humidity)/100))*100D)/100D;
			// Define "irTemp", and get IR sensor's data. 
                        double irTemp = tempSensor.getTemperature(0);
			//  Define "irTempBase", and get sensor's base tempature for accuracy adjustments.
			double irTempBase = tempSensor.getAmbientTemperature();
			// irSurfaceTemp = (Sensor Temperature - Ambient Temperature) * Emissivity + Ambient Temperature.
			//  Define "irSurfaceTemp", get sensor's data and do accuracy adjustment.
                        double irSurfaceTemp = (double)Math.round(((irTemp - irTempBase)*.965 + irTempBase)*100D)/100D;
			//  Print current readings to console.
                        System.out.println("118: Current readings Temperature="+tempc+"C, Humidity="+humidity+"%, VPD="+vpd+"kPa, Light="+light+"Lux, IR Surface Temperature="+irSurfaceTemp+"C");
			// Run 6 times a minute.
			if(oneminute == 6) {
				//  Read input and output data from ik.
				HashMap<Integer,Integer[]> inputOutput = getInputOutputData(ik);
				// Set date format.
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				//  Define "date".
				Date date = new Date();
				// Print date to console.
				System.out.println(dateFormat.format(date));
				Process job;
				//  Run the perl script addvalue.pl.
				String execline = "/home/phidget/workspace/addvalue.pl ";
				// Values to send to addvalue.pl
				execline += " "+tempc+" "+humidity+" "+vpd+" "+light+" "+irSurfaceTemp;
				synchronized(pb) {
					System.out.println("135: going to exec: "+execline);
					job = pb.exec(execline);
				}
				int exit = job.waitFor();
				if(exit != 0) { // Failed.
					System.out.println("140: failed to insert data to graph");
				}
				//  Add data for Kit 1.
				execline = "/home/phidget/workspace/addstatus1.pl ";
				// Get inputs state from the ik.
				Integer[] inputs = inputOutput.get(0);
				// Get outputs state from the ik.
				Integer[] outputs = inputOutput.get(1);
				// 
				for (int k =0; k<inputs.length;k++) {
					execline += " "+inputs[k].toString()+" "+outputs[k].toString();
				}
				synchronized(pb) {
					System.out.println("153: going to exec: "+execline);
					job = pb.exec(execline);
				}
				exit = job.waitFor();
				if(exit != 0) { // Failed.
					System.out.println("158: failed to insert data to graph");
				}
				oneminute = 0;
                        }
			oneminute++;
			Thread.sleep(10000);
		}
		// Close connection to ik object.
		ik.close();
	}
        private static  HashMap<Integer,Double> getReadableValue(Integer Index,Integer Value) {
                HashMap<Integer,Double> data= new HashMap<Integer,Double>();
                if (Index.intValue() == 1) {
			// Calculate for Celsius(x).
                        double x =  Math.round((((Value * .22222) -61.11)*100D))/100D;
			// Calculate for Fahrenheit(f).
                        double f = Math.round(((x * 1.8)+32D)*100D)/100D;
                        //return x+"C "+f+"F";
                        data.put(0,x);
                        data.put(1,f);
			// Calculate light LUX.
                } else if (Index.intValue() == 2) {
                        double x = (Math.round( (  ((Value * .1906) - 40.2) * 100D) ) / 100D);
                        data.put(0,x);
                } else if (Index.intValue() == 3) {
			// Calculate for Celsius(x).
                        double x =  Math.round((((Value * .22222) -61.11)*100D))/100D;
			// Calculate for Fahrenheit(f)
                        double f = Math.round(((x * 1.8)+32D)*100D)/100D;
                        data.put(0,x);
                        data.put(1,f);
			// Calculate light LUX.
                } else if (Index.intValue() == 4) {
                        double x = (Math.round( (  ((Value * .1906) - 60.2) * 100D) ) / 100D);
                        data.put(0,x);

                }
                return data;
        }
        private static HashMap<Integer,Integer[]> getInputOutputData(InterfaceKitPhidget ik) throws Exception {
                System.out.println("198: ik Input Count "+ik.getInputCount());
                Integer[] inputs = new Integer[ik.getInputCount()];
                int graphValue = 5;
                for (int x = 0; x<ik.getInputCount(); x++) {
                        boolean status = ik.getInputState(x);
                        if (status) {
                                inputs[x] = graphValue;
                        } else {
                                inputs[x] = 0;
                        }
                        graphValue += 10;
                        System.out.println("209: ik Input "+x+" Value : "+inputs[x]+" Status : "+status);
                }
                HashMap<Integer,Integer[]> ret = new HashMap<Integer,Integer[]>();
                ret.put(0,inputs);
                System.out.println("213: ik Output Count "+ik.getOutputCount());
                Integer[] outputs = new Integer[ik.getOutputCount()];
                graphValue = 10;
                for (int x = 0; x<ik.getOutputCount(); x++) {
                        boolean status = ik.getOutputState(x);
                        if (status) {
                                outputs[x] = graphValue;
                        } else {
                                outputs[x] = 0;
                        }
                        graphValue += 10;
                        System.out.println("224: ik Output "+x+" Value : "+outputs[x]+" Status : "+status);
                }
                ret.put(1,outputs);
                return(ret);
        }

}

