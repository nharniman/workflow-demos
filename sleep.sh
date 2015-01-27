#!/bin/bash

COUNTER=0
while [  $COUNTER -lt 100 ]; do
    echo The counter is $COUNTER
    let COUNTER=COUNTER+1 
    sleep 2s
done
