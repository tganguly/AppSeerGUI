#!/bin/bash

result="$(zenity --forms --title="Title"\
    --text="Text"\
    --add-entry="File Name"\
    --add-entry="Directory")"


    name="$(echo "$result"| cut -d '|' -f 1)"
    directory="$(echo "$result"| cut -d '|' -f 2)"
    echo $name
    echo $directory

    if [ "$directory" ]; then
        command="$directory"
    fi

    if [ "$name" ]; then
      command="$command$name"
    fi

    echo $command
