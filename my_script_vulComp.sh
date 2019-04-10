#!/bin/bash
  
response = $(zenity --list --checklist --title="Vulnerable Component Extraction"\
    --text="Select your preferences"\
    --column="Decompilation"\
    --column="Analysis"\
    TRUE A\
    TRUE B\
    --separator =':'
)
echo $response

IFS=":" ; for word in $response ; do 
   case $word in
      A) echo Item A ;;
      B) echo Item B ;;
   esac
done

