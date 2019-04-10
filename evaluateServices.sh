#!/bin/bash

######## Given an app name, retrieve its vulnerable.txt or safe.txt text file organized as:

######1: packagename
######2: mainActivity
######2: Component.1
######4: Component.2
####### ....
####n+1: Component.n

######## And execute n times startForegroundService through the ADB interface for the ActivityManager
######## Options: 
########	-f perform false negative check
########	-n N only send the first N intents
########    -o M execute only the Mth intent (need to manually start the app on the device)

#Import configuration variables from config.cfg
CONFIG="config.cfg"
if [ ! -f $CONFIG ];
	then
	echo "Error: config.cfg not found. Run: $ make config"
	exit
fi
source config.cfg

#Script constants
ADB_LOG_CLEAR_CMD=" logcat -b crash -c"
ADB_START_ACTIVITY_CMD=" shell am start-activity "
ADB_START_SERVICE_CMD=" shell am start-foreground-service "
ADB_LOG_CMD=" logcat -b crash -d -m 1 "
ADB_TAP_CMD=" shell input tap 400 200"
ADB_ROOT="$ADB_DIR root"
INPUT_DIR=$OUTPUT_DIR
VULNERABLE_INPUT_FILE="vulnerable.txt"
SAFE_INPUT_FILE="safe.txt"
CRASH_LOG_FILE="crash_log.txt"
F="-f"
N="-n"
O="-o"
USAGE="Usage: ./evaluateServices.sh [-f [-n N | -o M]] appName"
UNRECOGNIZED_OPTION="Unrecognized option: "
OPTIONS="\t-f: do false negative check\n\t-n N: only send the first N malicious intents\n\t-o M: only send the Mth intent"
NO_MAIN_ACTIVITY="Warning: no main activity found. Start the application manually and use option -o"
#Send all intents by default
LIMIT_INTENTS=0
UNIQUE_INTENT=0
#Timeout between starting services
INTERVAL=14

#Usage: ./sendMaliciousIntents.sh [-f [-n N | -o M]] appName
if [ $# -eq 0 ] || [ $# -gt 4 ];
	then
	echo $USAGE
	exit -1
fi

input_file=$VULNERABLE_INPUT_FILE

#Normalize paths with an ending /
last=$(echo -n $INPUT_DIR | tail -c 1) 
if [ "$last" = "/" ];
	then
	INPUT_DIR="$OUTPUT_DIR"
else
	INPUT_DIR="$OUTPUT_DIR/"
fi

app_name=${!#}
crash_file_full="$INPUT_DIR$app_name/vuln_$CRASH_LOG_FILE"
fn_mode=0

if [ $# -eq 2 ];
	then
	if [ $1 == $F ];
		then
		input_file=$SAFE_INPUT_FILE
		crash_file_full="$INPUT_DIR$app_name/safe_$CRASH_LOG_FILE"
		fn_mode=1
	else
		echo $USAGE
		exit -1
	fi
elif [ $# -gt 2 ];
	then
	opt_start=$1
	opt_value=$2
	if [ $1 == $F ];
		then
		input_file=$SAFE_INPUT_FILE
		crash_file_full="$INPUT_DIR$app_name/safe_$CRASH_LOG_FILE"
		opt_start=$2
		opt_value=$3
		#fn_mode=1
	elif [ $1 != $N ] && [ $1 != $O ];
		then
		echo $USAGE
		exit -1	
	fi
	case $opt_start in
		$N)
			LIMIT_INTENTS=$opt_value
		;;
		$O)
			UNIQUE_INTENT=$opt_value
		;;
		*)
			echo -e $UNRECOGNIZED_OPTION$opt_start
			echo -e $OPTIONS
			exit -1
		;;
	esac
fi

file_full="$INPUT_DIR$app_name/$input_file"

if [ ! -f $file_full ];
	then
	echo "Error: cannot find $input_file file for application $app_name. Exiting."
	exit -1
fi

i=-1
pkg_name=""
main_activity=""

#Clean old logs
if [ -f $crash_file_full ];
	then
	rm $crash_file_full
fi

#(re)start ADB as root
$ADB_ROOT

while read -r line || [[ -n "$line" ]]; do

	do_break=0
	do_send=1
    
	if [ $i == -1 ];
		then
		pkg_name=$line
	elif [ $i == 0 ];
		then
		if [ $line == "null" ] && [ $UNIQUE_INTENT == 0 ];
			then
			echo -e $NO_MAIN_ACTIVITY
			break
		else
			main_activity=$line
		fi
	else
		if [ $UNIQUE_INTENT -gt 0 ];
			then
			if [ $i != $UNIQUE_INTENT ];
				then
				do_send=0
			else
				do_break=1
				do_send=1
			fi
		elif [ $i == $LIMIT_INTENTS ];
			then
			do_break=1
		fi

		if [ $do_send == 1 ];
			then
			service_name=$line
			#Start the activity and sleep for 2 seconds to be sure it has started
			if [ $UNIQUE_INTENT == 0 ];
				then
				$ADB_DIR$ADB_START_ACTIVITY_CMD$pkg_name/$main_activity < /dev/null
				sleep 6
			fi
			#Clear current log first
			$ADB_DIR$ADB_LOG_CLEAR_CMD
			echo "Sending intent $i and waiting..."
			$ADB_DIR$ADB_START_SERVICE_CMD$pkg_name/$line < /dev/null
			sleep $INTERVAL
			#Dismiss crash dialog (if any...)
			$ADB_DIR$ADB_TAP_CMD < /dev/null
			#Write to log
			echo $service_name >> $crash_file_full
			$ADB_DIR$ADB_LOG_CMD >> $crash_file_full
		fi

		if [ $do_break == 1 ];
			then
			break
		fi

	fi

	i=$(($i+1))

done < $file_full

#If we sent all the intents, evaluate results

if [ $i -gt 1 ];
	then
		if [ $# == 1 ] || [ $fn_mode == 1 ]; # && [ $UNIQUE_INTENT == 0 ] && [ $LIMIT_INTENTS == 0];
		then
		i=$(($i-1))
		crashes=$(grep -c "FATAL EXCEPTION" $crash_file_full)
		miss=$(($i-$crashes))

		if [ $fn_mode == 0 ];
			then
			first="True positive (vulnerable): "
			second="False positive: "
		else
			first="False negative: "
			second="True negative (safe): "
		fi

		echo -e "\nResults:"
		echo -e "\t$first$crashes/$i"
		echo -e "\t$second$miss/$i"
	fi
fi

exit 0
