#!/bin/bash
OUTPUT_DIR=$(zenity --file-selection --directory --title="Choose the location for OUTPUT_DIR:" --filename=$HOME/Desktop/)
echo $OUTPUT_DIR

JADX_DIR=$(zenity --file-selection --directory --title="Choose the location for JADX_DIR:" --filename=$HOME/Desktop/)
JADX_OUT=$(zenity --file-selection --directory --title="Choose Enter the location for JADX_OUT" --filename=$HOME/Desktop/)
APKTOOL_DIR=$(zenity --file-selection --directory --title="Choose the location for APKTOOL_DIR" --filename=$HOME/Desktop/)
ADB_DIR=$(zenity --file-selection --title="Choose  the location for ADB_DIR" --filename=$HOME/Desktop/)
SDK_ROOT=$(zenity --file-selection --directory --title="Choose the location for SDK_ROOT" --filename=$HOME/Desktop/)
ANDROID_ROOT=$(zenity --file-selection --directory --title="Choosethe location for ANDROID_ROOT" --filename=$HOME/Desktop/)
if [ "$OUTPUT_DIR" ]; then

        echo "OUTPUT_DIR=\"$OUTPUT_DIR\"" >> config2.cfg; \
else echo "OUPUT_DIR=\"\"" >> config2.cfg;

    fi

    if [ "$JADX_DIR" ]; then
        echo "JADX_DIR=\"$JADX_DIR\"" >> config2.cfg; \
      command="$OUTPUT_DIR$JADX_DIR"
    fi
    if [ "$JADX_OUT" ]; then

        echo "JADX_OUT=\"$JADX_OUT\"" >> config2.cfg; \
        else echo "JADX_OUT=\"\"" >> config2.cfg;

    fi
    if [ "$APKTOOL_DIR" ]; then

    echo "APKTOOL_DIR=\"$APKTOOL_DIR\"" >> config2.cfg; \
    else echo "APKTOOL_DIR=\"\"" >> config2.cfg;

    fi
    if [ "$ADB_DIR" ]; then

    echo "ADB_DIR=\"$ADB_DIR\"" >> config2.cfg; \
    else echo "ADB_DIR=\"\"" >> config2.cfg;

    fi
    if [ "$SDK_ROOT" ]; then

    echo "SDK_ROOT=\"$SDK_ROOT\"" >> config2.cfg; \
    else echo "SDK_ROOT=\"\"" >> config2.cfg;

    fi
    if [ "$ANDROID_ROOT" ]; then

    echo "ANDROID_ROOT=\"$ANDROID_ROOT\"" >> config2.cfg; \
    else echo "ANDROID_ROOT=\"\"" >> config2.cfg;

    fi


