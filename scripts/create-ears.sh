#!/bin/bash

if [ -z "$2" ]; then
  echo "Invalid usage!";
  echo "You need to add two arguments specifying range (from => to) with range of EARS with sampleportal to create. For example 'scripts/create-ear.sh 2 10' will create sampleportal2.ear, sampleportal3.ear, ..., sampleportal10.ear";
  exit
fi


for I in $(seq $1 $2); do
  echo "STARTED TO CREATE EAR sampleportal$I.ear";
  ./scripts/create-ear.sh $I
  echo "FINISHED CREATING EAR sampleportal$I.ear";
done;
