#!/bin/biash

while true; do
set -x
  ans=$(zenity --info --title 'Analysis!' \
      --text 'Choose an option:' \
      --ok-label "Complete Analysis" \
      --extra-button "Custom Analysis" --extra-button Exit\
  --timeout 100 )
rc=$?
case $rc in
  0)
  echo "complete analysis...";
  # text input prompt
  apk_path=$(zenity --file-selection --title="Choose the APK_Path location " --filename=$HOME/Desktop/)
 # apk_path="$(zenity --entry --text "APK_Path" --entry-text "Apk_Path" )"
  echo "$apk"
  # text input prompt
  apk_name="$(zenity --entry --text "Apk_name" --entry-text "Apk_name" )"

  sh analyzeApk.sh $apk_path $apk_name;
;;
  1)
  case $ans in
    Custom*)
  	echo "custom analysis...";
    if zenity --question --text="Do you want to Find Vulnerabilities?"
    then
    sh my_script_vulComp.sh;
    fi
    if zenity --question --text="Do you want to Evaluate Services?"
    then
    sh my_script_evalService.sh;
    fi
;;

    Exit)
	echo exit...;
	exit 1;;
  esac
  ;;
esac
done
