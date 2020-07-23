#!/bin/bash

#create numbers file
dd if=/dev/random bs=1 count=$1 status=none of=numbers

#compile application for benchmarking
#mpic++ -o ots ots.cpp -L/usr/local/lib -lpapi -D BENCHMARK

#compile application
mpic++ -o ots ots.cpp

#run
mpirun -np $1 ots

#clean
rm -f ots numbers