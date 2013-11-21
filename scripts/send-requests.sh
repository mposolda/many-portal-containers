#!/bin/bash

if [ -z "$1" ]; then
  echo "Invalid usage!";
  echo "You need to add one argument specifying the number of requests to send. For example 'scripts/send-requests.sh 10' will send 10 requests to http://localhost:8080/sampleportal1/classic/";
  exit
fi

START=$(date +%s)
for I in $(seq 1 $1); do  
  curl --request GET 'http://localhost:8080/sampleportal1/classic/' > /dev/null
  TIME=$(date +%s)
  echo "Request $I to http://localhost:8080/sampleportal1/classic/ returned. Time from the start: $(($TIME-$START)) seconds"
done;
echo "Total execution time for send $1 requests: $(($TIME-$START)) seconds"

