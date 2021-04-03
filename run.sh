#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters (1): ./run.sh <OutFolder> <JSONfolder>"
    exit -1
fi


mkdir -p /data/scratch/$USER/$1 # Create a new directory for our job

cp -R $HOME/tribes-mapelites-files/Tribes.jar /data/scratch/$USER/$1
cp -R $HOME/tribes-mapelites-files/terrainProbs.json /data/scratch/$USER/$1

#cp -R $HOME/tribes-pmcts/${filename} /data/scratch/$USER/$1
# mkdir -p /data/scratch/$USER/$1/gamelogs/ # Create a new directory for our job
qsub tribes.sh $1 $2
