#!/bin/bash
set -x
ans=$(zenity --list \
--title="Evaluate Options" \
--text "Which vulnerable service(s) you want to evaluate?" \
--radiolist \
--column "Pick" \
--column "Answer" \
FALSE "First 'n' services" \
FALSE "Particular Service")

echo $ans
#apk_name=$(zenity --file-selection --directory --title="Choose the decompiled APK location" --filename=$HOME/Desktop/)
apk_name="$(zenity --entry --text "Apk_name to evaluate services" --entry-text "Apk_name" )"

case $ans in
First*)
# text input prompt
first_n="$(zenity --entry --text "How many services you want to evaluate?" --entry-text 1 )"
if zenity --question --text="Do you want to check for false negative checks?"
then
    ./evaluateServices.sh -f -n $first_n $apk_name
else
    ./evaluateServices.sh -n $first_n $apk_name

fi
    echo $first_n;;

Particular*)
# text input prompt
pos_n="$(zenity --entry --text "Which service you want to evaluate?" --entry-text "1" )"
if zenity --question --text="Do you want to check for false negative checks?"
then
	./evaluateServices.sh -f -o $pos_n $apk_name;
else
	./evaluateServices.sh -o $pos_n $apk_name;

fi
    echo $pos_n;;
esac
