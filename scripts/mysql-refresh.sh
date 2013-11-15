#!/bin/bash

if [ -z "$1" ]; then
  echo "Invalid usage!";
  echo "You need to add one argument specifying the number for mysql databases to drop and create. For example 'scripts/mysql-refresh.sh 10'";  
  exit
fi

# Add command for portal database on top
echo "drop database portal1;";
echo "create database portal1;";

# Add commands for sampleportal databases
for I in $(seq 1 $1); do
  echo "drop database sampleportal$I;";
  echo "create database sampleportal$I;";
done;
