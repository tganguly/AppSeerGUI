TOOL=../AppSeer
TOOL_BIN=${TOOL}/bin
TOOL_SRC=${TOOL}/src/appseer
CONFIG=config.cfg



while true; do
  (sleep 1 && wmctrl -F -a "I am on top" -b add,above) &
  ans=$(zenity --info --title 'Choose!' \
      --text 'Choose an option:' \
      --ok-label Configure \
      --extra-button Compile --extra-button Clean --extra-button Exit\
  --timeout 30)
  rc=$?
  echo "${rc}-${ans}"
  
  case $rc in
  0)
  rm config.cfg;
  sh fc_test.sh;
  echo configuring...;;
  esac

  case $ans in
 
  Compile) echo compiling....;
  mkdir ${TOOL_BIN}
  javac -d ${TOOL_BIN} ${TOOL_SRC}/*.java;
  sh my_script_analysis.sh;;
  Clean) echo cleaning....;
         rm -rf ${TOOL_BIN}
         rm -rf ${CONFIG};;
  Exit) exit 1;;
  esac



done
