#!/bin/bash
A="Skip APK Decompilation"
B="Skip Analysis"  
(sleep 1 && wmctrl -F -a "I am on top" -b add,above) &
response=$(zenity --height=250 --list --checklist \
   --title='Selection' --column=Boxes --column=Selections \
   TRUE "$A"  TRUE "$B" TRUE C --separator=':')
echo $response

# text input prompt
apk="$(zenity --entry --text "APK_Path" --entry-text "Apk_name" )"
echo "$apk"



if [ -z "$response" ] ; then
   echo "No selection"
   exit 1
fi

IFS=":" ; for word in $response ; do
   case $word in
      "$A") echo Item A ;;
      "$B") echo Item B ;;
      C) echo Item C ;;
   esac
done

