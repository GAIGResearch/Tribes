#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters (1): ./run.sh <Config> "
    exit -1
fi

mkdir -p /data/scratch/$USER/$1 # Create a new directory for our job

cp -R $HOME/tribes/Tribes.jar /data/scratch/$USER/$1
cp -R $HOME/tribes/terrainProbs.json /data/scratch/$USER/$1
# mkdir -p /data/scratch/$USER/$1/gamelogs/ # Create a new directory for our job

qsub tribes.sh $1
