#!/bin/bash # This script asks the user for a time, waits the specified amount # of time, and shows an alert dialog.
data= `(zenity --forms --title="Create user" --text="Appseer Configuration" \
   --add-entry="First Name" \
   --add-entry="Last Name" \
   --add-entry="Username" \
   --add-password="Password" \
   --add-password="Confirm Password" \
   --add-calendar="Expires")`
case $? in
     1) echo "you cancelled"; exit 1 ;;
    -1) echo "some error occurred"; exit -1 ;;
     0) IFS="|" read -r name title author price qtyA qtyS <<< "$data" ;;
esac
echo Data Stored
echo $data

