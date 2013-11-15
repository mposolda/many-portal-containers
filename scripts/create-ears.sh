#!/bin/bash

if [ -z "$2" ]; then
  echo "Invalid usage!";
  echo "You need to add two arguments specifying range (from => to) with range of EARS with sample-portal to create. For example 'scripts/create-ear.sh 2 10' will create sample-portal2.ear, sample-portal3.ear, ..., sample-portal10.ear";
  exit
fi


for I in $(seq $1 $2); do
  echo "STARTED TO CREATE EAR sample-portal$I.ear";
  ./scripts/create-ear.sh $I
  echo "FINISHED CREATING EAR sample-portal$I.ear";
done;
