#!/bin/bash

######## Given an apk file and the name of the app, retrieve vulnerable
######## services according to the onStartCommand -> startForeground pattern and directly 
######## evaluate the obtained results through the ADB interface when a device is connected.


#Script constants
SCRIPT_NAME="./evaluateApk.sh"

#Usage: ./evaluateApk.sh apk_file.apk app_name
if [ $# -ne 2 ];
	then
	echo "Usage: $SCRIPT_NAME apk_file.apk app_name"
	exit -1
fi

./vulnerableComponents.sh $1 $2
./evaluateServices.sh $2