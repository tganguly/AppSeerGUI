#!/bin/bash
set -x
A="Skip APK Decompilation"
B="Skip Analysis"  
response=$(zenity --height=250 --list --checklist \
   --title='Selection' --column=Boxes --column=Selections \
   TRUE "$A"  TRUE "$B" TRUE C --separator=':')
echo $response
response=$(zenity --list \
--title="Choose vulnerability Options" \
--text "Choose correct option" \
--radiolist \
--column "Pick" \
--column "Answer" \
FALSE "$A" \
FALSE "$B")

echo $ans

# text input prompt
apk_path=$(zenity --file-selection --title="Choose the APK_Path location " --filename=$HOME/Desktop/)
#apk_path="$(zenity --entry --text "APK_Path" --entry-text "Apk_Path" )"
#echo "$apk"
# text input prompt
apk_name="$(zenity --entry --text "Apk_name" --entry-text "Apk_name" )"


if [ -z "$response" ] ; then
   echo "No selection"
   exit 1
fi

#IFS=":" ; for word in $response ; do
   case $response in
      "$A")
	#-d
	sh vulnerableComponents.sh -d apk_path apk_name; 
        echo Item A ;;
      "$B")
	#-a
	sh vulnerableComponents.sh -a apk_path apk_name; 
	echo Item B ;;
     # C) echo Item C ;;
   esac
done

