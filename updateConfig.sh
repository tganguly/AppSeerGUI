#!/bin/bash
set -x
result="$(zenity --forms --title="Title"\
    --text="Text"\
    --add---file-selection ="Enter the location for OUTPUT_DIR:"\
    --add-entry="Enter the location for JADX_DIR:"\
    --add-entry="Enter the location for JADX_OUT:"\
    --add-entry="Enter the location for APKTOOL_DIR:"\
    --add-entry="Enter the location for ADB_DIR:"\
    --add-entry="Enter the location for SDK_ROOT:"\
    --add-entry="Enter the location for ANDROID_ROOT:"\
)"





    OUTPUT_DIR="$(echo "$result"| cut -d '|' -f 1)"
    JADX_DIR="$(echo "$result"| cut -d '|' -f 2)"
    JADX_OUT="$(echo "$result"| cut -d '|' -f 3)"
    APKTOOL_DIR="$(echo "$result"| cut -d '|' -f 4)"
    ADB_DIR="$(echo "$result"| cut -d '|' -f 5)"
    SDK_ROOT="$(echo "$result"| cut -d '|' -f 6)"
    ANDROID_ROOT="$(echo "$result"| cut -d '|' -f 7)"
    echo $OUTPUT_DIR
    echo $JADX_DIR
    echo $JADX_OUT
    echo $APKTOOL_DIR
    echo $ADB_DIR
    echo $SDK_ROOT
    echo $ANDROID_ROOT



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

        echo $command


