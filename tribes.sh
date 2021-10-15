#!/bin/sh
#$ -pe smp 1 #Request X CPU cores
#$ -l h_rt=90:00:00 # Request 7h 20m 10s of walltime
#$ -l h_vmem=20G # Request 2GB RAM for each core requested
#$ -M diego.perez@qmul.ac.uk # Sends notifications email to this address
#$ -m bea # Emails are sent on begin, end and abortion
#$ -t 1-8 # 1-8 Index of the job array ${SGE_TASK_ID}
#$ -tc 8 # 20 Number of jobs allowed to run concurrently

module load java/9.0.1-oracle

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    exit -1
fi

nb=${SGE_TASK_ID}-1

#p0=('mcts/t1.json' 'mcts/t2.json' 'mcts/t3.json' 'mcts/t4.json' 'mcts/t5.json')

p0=('t1.json' 't2.json' 't3.json' 't4.json' 't5.json' 't6.json' 't7.json' 't8.json')

cp $HOME/tribes-mapelites-files/$2/*.json /data/scratch/$USER/$1/



#filename=t$2.json
#filename=t${nb}.json

cd /data/scratch/$USER/$1/ # Move to the scratch directory
java -jar Tribes.jar ${p0[$nb]} > tribes_${nb}.txt 2> tribes_${nb}.err

