#!/bin/bash

######## Given an apk file and the name of the app, retrieve vulnerable
######## services according to the onStartCommand -> startForeground pattern.
######## Options: 
########	-d do not decompile apk, just analyze it
########	-a do not analyze apk, just decompile it
######## Results will be in $OUTPUT_DIR app_name.txt

#Import configuration variables from config.cfg
CONFIG="config.cfg"
if [ ! -f $CONFIG ];
	then
	echo "Error: config.cfg not found. Run: $ make config"
	exit
fi
source config.cfg

#Script constants
NO_DECOMPILE="-d"
NO_ANALYZE="-a"
SCRIPT_NAME="./vulnerableComponents.sh"
COMPLETE_LOG="complete_log.txt"
DO_DECOMPILE=1
DO_ANALYZE=1
UNRECOGNIZED_OPTION="Unrecognized option: "
OPTIONS="\t-d: do not decompile\n\t-a: do not analyze"

#jadx & apktool constants
APK_BIN="apktool"
JADX_BIN="jadx"
JADX_LOG="jadx_log.txt"
JADX_SOURCES="sources"
JADX_RESOURCES="resources"

#Java binary constants
CLASSPATH="AppSeer/bin/"
MAIN_CLASS="appseer.AppSeerDemo"

#Usage: ./vulnerableComponent.sh apk_file.apk app_name
if [ $# -lt 2 ] || [ $# -gt 5 ];
	then
	echo "Usage: $SCRIPT_NAME [-da] apk_file.apk app_name"
	exit
fi

if [ $# -eq 3 ];
	then
	case $1 in
		$NO_DECOMPILE)
			DO_DECOMPILE=0
			;;
		$NO_ANALYZE)
			DO_ANALYZE=0
			;;
		*)
			echo -e $UNRECOGNIZED_OPTION$1
			echo -e $OPTIONS
			DO_DECOMPILE=0
			DO_ANALYZE=0
		;;
	esac
elif [ $# -eq 4 ];
	then
	case $1 in
		$NO_DECOMPILE)
			DO_DECOMPILE=0
			;;
		$NO_ANALYZE)
			DO_ANALYZE=0
			;;
		*)
			echo -e $UNRECOGNIZED_OPTION$1
			echo -e $OPTIONS
			DO_DECOMPILE=0
			DO_ANALYZE=0
			;;
	esac

	case $2 in
		$NO_DECOMPILE)
			DO_DECOMPILE=0
			;;
		$NO_ANALYZE)
			DO_ANALYZE=0
			;;
		*)
			echo -e $UNRECOGNIZED_OPTION$2
			echo -e $OPTIONS
			DO_DECOMPILE=0
			DO_ANALYZE=0
			;;
	esac
fi

second_last=$(( $#-1 ))
app_name=${!#}
apk_source=${!second_last}

#Normalize paths with an ending /
last=$(echo -n $JADX_OUT | tail -c 1) 
if [ "$last" = "/" ];
	then
	apk_sink=$JADX_OUT$app_name
else
	apk_sink=$JADX_OUT/$app_name
fi

last=$(echo -n $OUTPUT_DIR | tail -c 1) 
if [ "$last" = "/" ];
	then
	OUTPUT_DIR="$OUTPUT_DIR$app_name"
else
	OUTPUT_DIR="$OUTPUT_DIR/$app_name"
fi

last=$(echo -n $JADX_DIR | tail -c 1) 
if [ "$last" = "/" ];
	then
	JADX_DIR="$JADX_DIR$JADX_BIN"
else
	JADX_DIR="$JADX_DIR/$JADX_BIN"
fi

last=$(echo -n $APKTOOL_DIR | tail -c 1) 
if [ "$last" = "/" ];
	then
	APKTOOL_DIR="$APKTOOL_DIR$APK_BIN"
else
	APKTOOL_DIR="$APKTOOL_DIR/$APK_BIN"
fi

######## 1 - Decompile apk file to obtain the AndroidManifest.xml file

if [ $DO_DECOMPILE -eq 1 ];
	then

	if [ ! -f $apk_source ];
		then
		echo "Error: file $apk_source not found. Exiting."
		exit
	fi

	mkdir $apk_sink
	decompile="$JADX_DIR -d $apk_sink/ $apk_source"

	echo -e "\n1 - Executing: $decompile"
	$decompile >> $apk_sink/$JADX_LOG

	manifest="$apk_sink/$JADX_RESOURCES/AndroidManifest.xml"

	if [ ! -f $manifest ];
		then
		echo "Cannot find file $manifest for app $app_name. Exiting."
		exit
	fi

	#Found AndroidManifest.xml file

fi

######## 2 - Fetch and analyze AndroidManifest.xml file through Java

if [ $DO_ANALYZE -eq 1 ];

	then

	if [ ! -d $OUTPUT_DIR ];
		then
		mkdir $OUTPUT_DIR
	fi

	javacmd="java -cp $CLASSPATH $MAIN_CLASS $app_name $SDK_ROOT $ANDROID_ROOT $OUTPUT_DIR $JADX_OUT"
	echo -e "\n2 - Executing: $javacmd\n"
	$javacmd | tee "$OUTPUT_DIR/$COMPLETE_LOG"

fi

echo -e ""
exit