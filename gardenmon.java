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

                InterfaceKitPhidget ik;
                TemperatureSensorPhidget tempSensor;
                System.out.println(Phidget.getLibraryVersion());
                ik = new InterfaceKitPhidget();
                tempSensor = new TemperatureSensorPhidget();

                ik.addAttachListener(new AttachListener() {
                        public void attached(AttachEvent ae) {
                                System.out.println("22:  attachment of ik " + ae);
                        }
                });
                tempSensor.addAttachListener(new AttachListener() {
                        public void attached(AttachEvent ae) {
                                System.out.println("27:  attachment of tempSensor " + ae);
                        }
                });
                ik.addDetachListener(new DetachListener() {
                        public void detached(DetachEvent ae) {
                                System.out.println("32: detachment of ik " + ae);
                        }
                });
                tempSensor.addDetachListener(new DetachListener() {
                        public void detached(DetachEvent ae) {
                                System.out.println("37: detachment of tempSensor " + ae);
                        }
                });
                ik.addErrorListener(new ErrorListener() {
                        public void error(ErrorEvent ee) {
                                System.out.println("42: ik Error "+ee);
                        }
                });
                ik.addInputChangeListener(new InputChangeListener() {
                        public void inputChanged(InputChangeEvent oe) {
                                System.out.println("47: ik inputchangelistener "+oe);
                        }
                });
                ik.addOutputChangeListener(new OutputChangeListener() {
                        public void outputChanged(OutputChangeEvent oe) {
                                System.out.println("52: ik outputchangelistener "+oe);
                        }
                });
                tempSensor.addTemperatureChangeListener(new TemperatureChangeListener() {
                         public void temperatureChanged(TemperatureChangeEvent se) {
                        }
                });
                ik.open(330510);
                System.out.println("63: waiting for InterfaceKit attachment...");
                ik.waitForAttachment();
                System.out.println("65: Device Name: "+ik.getDeviceName());
                System.out.println("66: Device Serial: "+ik.getSerialNumber());
                System.out.println("67: Total Outputs: "+ik.getOutputCount());
                System.out.println("68: Total Inputs: "+ik.getInputCount());
                int oneminute=0;
                Runtime pb = Runtime.getRuntime();
                Thread.sleep(500);

                tempSensor.open(322809);
                tempSensor.waitForAttachment();
                System.out.println("76: Device Name: "+tempSensor.getDeviceName());
                System.out.println("76: Device Serial: "+tempSensor.getSerialNumber());
                Thread.sleep(500);

                boolean test2 = true;
                while(test2) {
                        HashMap<Integer,Double> data= getReadableValue(1,(Double.valueOf( (ik.getSensorRawValue(1) / 4.095))).intValue() );
                        double light = (double)Math.round(100D*(double)Math.exp(0.02357*(ik.getSensorRawValue(6) / 4.095)+-0.7853))/100D;
                        double irTemp = tempSensor.getTemperature(0);
                        double tempc = data.get(0).doubleValue();
                        double tempf = data.get(1);
                        double humidity = data.get(0);
                        double vpd = (double)Math.round(((0.6108*(double)Math.exp((17.27*tempc)/(tempc+237.3))*(100-humidity)/100))*100D)/100D;
                        double irTempBase = tempSensor.getAmbientTemperature();
                        double irSurfaceTemp = (double)Math.round(((irTemp - irTempBase)*.965 + irTempBase)*100D)/100D;
                        System.out.println("87: Current readings Temperature="+tempc+"C Humidity="+humidity+"% VPD="+vpd+"kPa IR Temperature="+irTemp+"C "+"IR Temperature at Base="+irTempBase+"C "+"IR Surface Temperature="+irSurfaceTemp+"C");
                        if(oneminute == 6) {
                                HashMap<Integer,Integer[]> inputOutput1 = getInputOutputData(ik);
                                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                Date date = new Date();
                                System.out.println(dateFormat.format(date));
                                Process job;
                                String execline = "/home/cary/workspace/Phidget/addvalue.pl ";
                                execline += " "+tempc+" "+humidity+" "+vpd+" "+light+" "+irSurfaceTemp;
                                Integer[] inputs = inputOutput1.get(0);
                                Integer[] outputs = inputOutput1.get(1);
                                for (int k =0; k<inputs.length;k++) {
                                }
                                execline += " "+tempc+" "+humidity+" "+vpd+" "+light;
                                synchronized(pb) {

                                        System.out.println("going to exec: "+execline);
                                        job = pb.exec(execline);
                                }
                                int exit = job.waitFor();
                                if(exit != 0) {
                                        System.out.println("117: failed to insert data to graph");
                                }
                                execline = "/home/cary/workspace/Phidget/addstatus1.pl ";

                                for (int k =0; k<inputs.length;k++) {
                                        execline += " "+inputs[k].toString()+" "+outputs[k].toString();
                                }
                                synchronized(pb) {
                                        System.out.println("going to exec: "+execline);
                                        job = pb.exec(execline);
                                }
                                exit = job.waitFor();
                                if(exit != 0) {
                                        System.out.println("133: failed to insert data to graph");
                                }
                                oneminute = 0;
                        }
                        oneminute++;
                        Thread.sleep(10000);
                }
                ik.close();
        }
        private static  HashMap<Integer,Double> getReadableValue(Integer Index,Integer Value) {
                HashMap<Integer,Double> data= new HashMap<Integer,Double>();
                if (Index.intValue() == 1) {
                        double x =  Math.round((((Value * .22222) -61.11)*100D))/100D;
                        double f = Math.round(((x * 1.8)+32D)*100D)/100D;
                        data.put(0,x);
                        data.put(1,f);
                } else if (Index.intValue() == 2) {
                        double x = (Math.round( (  ((Value * .1906) - 40.2) * 100D) ) / 100D);
                        data.put(0,x);
//
                } else if (Index.intValue() == 3) {
                        double x =  Math.round((((Value * .22222) -61.11)*100D))/100D;
                        double f = Math.round(((x * 1.8)+32D)*100D)/100D;
                        data.put(0,x);
                        data.put(1,f);
                } else if (Index.intValue() == 4) {
                        double x = (Math.round( (  ((Value * .1906) - 60.2) * 100D) ) / 100D);
                        data.put(0,x);

                }
                return data;
        }
        private static HashMap<Integer,Integer[]> getInputOutputData(InterfaceKitPhidget ik) throws Exception {
                System.out.println("169: ik Input Count "+ik.getInputCount());
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
                        System.out.println("180: ik Input "+x+" Value : "+inputs[x]+" Status : "+status);
                }
                HashMap<Integer,Integer[]> ret = new HashMap<Integer,Integer[]>();
                ret.put(0,inputs);
                System.out.println("184: ik Output Count "+ik.getOutputCount());
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
                        System.out.println("195: ik Output "+x+" Value : "+outputs[x]+" Status : "+status);
                }
                ret.put(1,outputs);
                return(ret);
        }

}
