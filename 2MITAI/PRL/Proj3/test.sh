#!/bin/bash
if [ $# -lt 1 ];then
    exit 1
fi;

#compile application for benchmarking
#mpic++ --prefix /usr/local/share/OpenMPI -o vid vid.cpp -D BENCHMARK

#compile application
mpic++ --prefix /usr/local/share/OpenMPI -o vid vid.cpp

#run application
mpirun --prefix /usr/local/share/OpenMPI -np 16 vid $1

#clean
rm -f vid
