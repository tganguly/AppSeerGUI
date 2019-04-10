TOOL=../AppSeer
TOOL_BIN=${TOOL}/bin
TOOL_SRC=${TOOL}/src/appseer
CONFIG=config2.cfg

while true; do
  ans=$(zenity --info --title 'Choose!' \
      --text 'Choose A or B or C' \
      --ok-label Configure \
      --extra-button Compile --extra-button Clean --extra-button Exit\
 --timeout 30)
  rc=$?
  echo "${rc}-${ans}"
case $rc in
0)
sh my_script2.sh;
echo configuring...;;
esac

case $ans in
Configure) echo configuring....;;
Compile) echo compiling....;;
Clean) echo cleaning....;
rm -rf $(TOOL_BIN)
rm -rf $(CONFIG);;
Exit) exit 1;;
esac



done
