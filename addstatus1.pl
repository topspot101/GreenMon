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
my $input00 = $ARGV[0];
my $output00 = $ARGV[1];
my  $input01 = $ARGV[2];
my $output01 = $ARGV[3];
my  $input02 = $ARGV[4];
my $output02 = $ARGV[5];
my  $input03 = $ARGV[6];
my $output03 = $ARGV[7];
my  $input04 = $ARGV[8];
my $output04 = $ARGV[9];
my  $input05 = $ARGV[10];
my $output05 = $ARGV[11];
my  $input06 = $ARGV[12];
my $output06 = $ARGV[13];
my  $input07 = $ARGV[14];
my $output07 = $ARGV[15];
my  $input08 = $ARGV[16];
my $output08 = $ARGV[17];
my $ofh = select STDOUT;
$| = 1;
select $ofh;
my $numArgs = $#ARGV + 1;

# Remove commnets below to enable debug log.
#my $fh = new IO::File;
#$fh->open(">> /tmp/addstatus.log");
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

my $rrdfile = $rrd_files_dir."kit1.rrd";
my $ret = &writerrd($rrdfile,$epoch,$input00,$output00,$input01,$output01,$input02,$output02,$input03,$output03,$input04,$output04,$input05,$output05,$input06,$output06,$input07,$output07);
if ($ret ) {
	exit(1);
}

sub writerrd {
	my ($file,$ftime,$input00,$output00,$input01,$output01,$input02,$output02,$input03,$output03,$input04,$output04,$input05,$output05,$input06,$output06,$input07,$output07) = @_;

# Remove commnets below to enable debug log.	
#	my $fh = new IO::File;
#	$fh->open(">> /tmp/addstatus2.log");
#	print $fh "Going to add ".$ftime." ".$input00." ".$output00." ".$input01." ".$output01." ".$input02." ".$output02." ".$input03." ".$output03." ".$input04." ".$output04." ".$input05." ".$output05." ".$input06." ".$output06." ".$input07." ".$output07."\n";
#	$fh->close();

	my $start_date = $ftime;
	my @args;
	if (! -w $file) {
		$start_date -= 10;
		@args=("$file",
		'--start', $start_date,
		'-s 60',
		'DS:input00:GAUGE:180:0:170',
		'DS:output00:GAUGE:180:0:170',
		'DS:input01:GAUGE:180:0:170',
		'DS:output01:GAUGE:180:0:170',
		'DS:input02:GAUGE:180:0:170',
		'DS:output02:GAUGE:180:0:170',
		'DS:input03:GAUGE:180:0:170',
		'DS:output03:GAUGE:180:0:170',
		'DS:input04:GAUGE:180:0:170',
		'DS:output04:GAUGE:180:0:170',
		'DS:input05:GAUGE:180:0:170',
		'DS:output05:GAUGE:180:0:170',
		'DS:input06:GAUGE:180:0:170',
		'DS:output06:GAUGE:180:0:170',
		'DS:input07:GAUGE:180:0:170',
		'DS:output07:GAUGE:180:0:170',
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
	RRDs::update($file,"$ftime:$input00:$output00:$input01:$output01:$input02:$output02:$input03:$output03:$input04:$output04:$input05:$output05:$input06:$output06:$input07:$output07");
	my $result=RRDs::error;
	if ($result) {
		my $error = __LINE__.": rrd::update result: $result\n";
		print $error;
		return(1);
	}
}
exit(0)
;
