#!/bin/bash

if [ -z "$2" ]; then
  echo "Invalid usage!";
  echo "You need to add two arguments. First argument is name of the file in current directory, which contains XML template. Second is specifying number of copies of this XML template. Example of usage: 'scripts/create-xml-from-template datasources-xml 2' will create file datasources-xml-2 containing two copies of the template";
  exit
fi

SOURCE_FILE=$1
TARGET_FILE=$1-$2
echo "Source file: $SOURCE_FILE, Target file: $TARGET_FILE"
# Clear the file
echo "" > $TARGET_FILE

# Append datasources to it
for I in $(seq 1 $2); do
  cat $SOURCE_FILE | sed s/portal1/portal$I/g >> $TARGET_FILE
  # 2 lines break
  echo "" >> $TARGET_FILE
  echo "" >> $TARGET_FILE
done;
