#!/usr/bin/perl -w
my $debug = 1;
use POSIX;
use strict;
use Getopt::Long;
use Time::Local;
use lib qw(/usr/lib/arm-linux-gnueabihf/perl5/5.28);
use RRDs;
use IO::File;
our $rrd_files_dir = "/home/phidget/workspace/";
my $tempc = $ARGV[0];
my $humidity = $ARGV[1];
my $vpd = $ARGV[2];
my $light = $ARGV[3];
my $irSurfaceTemp = $ARGV[4];
my $ofh = select STDOUT;
$| = 1;
select $ofh;
my $numArgs = $#ARGV + 1;

# Remove commnets below to enable debug log.
#my $fh = new IO::File;
#$fh->open(">> /tmp/addvalue.log");
#foreach my $argnum (0 .. $#ARGV) {
#print $fh $argnum."_".$ARGV[$argnum]." ";
#}
#print $fh "\n";
#$fh->close();

my %datefix;
$datefix{"Jan"} = 0;
$datefix{"Feb"} = 1;
$datefix{"Mar"} = 2;
$datefix{"Apr"} = 3;
$datefix{"May"} = 4;
$datefix{"Jun"} = 5;
$datefix{"Jul"} = 6;
$datefix{"Aug"} = 7;
$datefix{"Sep"} = 8;
$datefix{"Oct"} = 9;
$datefix{"Nov"} = 10;
$datefix{"Dec"} = 11;

my $timestr = localtime;
my $d1 = &timegm(localtime);
my $d2 = &timelocal(localtime);
my $dt = ($d1 - $d2);
$timestr =~ m/\w+\s+(\w+)\s+(\d+)\s+(\d+)\:(\d+)\:\d+\s+(\d+)/;
my $epoch = &timelocal(00,$4,$3,$2,$datefix{$1},$5);
my $rrdfile = $rrd_files_dir."temphumidity.rrd";
my $ret = &writerrd($rrdfile,$epoch,$tempc,$humidity,$vpd,$light,$irSurfaceTemp);
if ($ret ) {
	exit(1);
}

sub writerrd {
	my ($file,$ftime,$tempc,$humidity,$vpd,$light,$irSurfaceTemp) = @_;
	
# Remove commnets below to enable debug log.
#	my $fh = new IO::File;
#	$fh->open(">> /tmp/addvalue2.log");
#	print $fh "Going to add ".$ftime." ".$tempc." ".$humidity." ".$vpd." ".$light." ".$irSurfaceTemp."\n";
#	$fh->close();

	my $start_date = $ftime;
	my @args;
	if (! -w $file) {
		$start_date -= 10;
		@args=("$file",
		'--start', $start_date,
		'-s 60',
                'DS:tempc:GAUGE:180:-100:100',
                'DS:humidity:GAUGE:180:0:100',
                'DS:vpd:GAUGE:180:0:6',
                'DS:light:GAUGE:180:0:70000',
		'DS:irSurfaceTemp:GAUGE:180:-100:100',
		'RRA:AVERAGE:0.5:1:1440',
		'RRA:AVERAGE:0.5:5:2016',
		'RRA:AVERAGE:0.5:30:1488',
		'RRA:AVERAGE:0.5:1440:365',
		'RRA:MAX:0.5:1:1440',
		'RRA:MAX:0.5:5:2016',
		'RRA:MAX:0.5:30:1488',
		'RRA:MAX:0.5:1440:365'
		);

                my $cmd = "rrdtool create ".join(' ',@args);
		system($cmd);
	}
	RRDs::update($file,"$ftime:$tempc:$humidity:$vpd:$light:$irSurfaceTemp");
	my $result=RRDs::error;
	if ($result) {
		my $error = __LINE__.": rrd::update result: $result\n";
		print $error;
		return(1);
	}
}
exit(0);
