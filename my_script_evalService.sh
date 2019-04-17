ans=$(zenity --list \
--title="Evaluate Options" \
--text "Which vulnerable service(s) you want to evaluate?" \
--radiolist \
--column "Pick" \
--column "Answer" \
FALSE "First 'n' services" \
FALSE "Particular Service")

echo $ans
case $ans in
First*)
# text input prompt
first_n="$(zenity --entry --text "How many services you want to evaluate?" --entry-text 1 )"
if zenity --question --text="Do you want to check for false negative checks?"
then
    echo "evaluateServices.sh -f -n first_n apk_name"
else
    echo "evaluateServices.sh -n first_n apk_name"

fi
    echo $first_n;;

Particular*)
# text input prompt
pos_n="$(zenity --entry --text "Which service you want to evaluate?" --entry-text "1" )"
if zenity --question --text="Do you want to check for false negative checks?"
then
echo "evaluateServices.sh -f -o pos_n apk_name"
else
echo "evaluateServices.sh -o pos_n apk_name"

fi
    echo $pos_n;;
esac
