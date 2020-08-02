#!/bin/sh
#$ -pe smp 1 #Request X CPU cores
#$ -l h_rt=30:00:00 # Request 7h 20m 10s of walltime
#$ -l h_vmem=20G # Request 2GB RAM for each core requested
#$ -M diego.perez@qmul.ac.uk # Sends notifications email to this address
#$ -m bea # Emails are sent on begin, end and abortion
#$ -t 1-15 # 1-8 Index of the job array ${SGE_TASK_ID}
#$ -tc 15 # 20 Number of jobs allowed to run concurrently

module load java/9.0.1-oracle

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    exit -1
fi

cd /data/scratch/$USER/$1/ # Move to the scratch directory
#$ -cwd

#AIIDE
p0=(0  0  0  0  0  0  0  0  0  0  0  0  0  0  0)
p1=(20 20 20 20 20 20 20 20 20 20 20 20 20 20 20)
p2=(0  20 20 20 20 20 20 20 20 20 0  20 20 20 20)
p3=(0  0  0  0  0  0  0  0  0  0  0  0  0  0  0)
p4=(0  0  0  0  0  0  0  0  0  0  0  0  0  0  0)
p5=(0  0  0  0  0  0  1  1  1  1  0  0  0  0  1)
p6=(2  2  3  5  5  5  2  3  4  5  2  3  4  5  6)
p7=(3  4  4  2  3  4  6  6  6  6  1  1  1  1  1)
p8=(0  0  0  0  0  0  0  0  0  0  0  0  0  0  0)
p9=(1  1  1  1  1  1  1  1  1  1  1  1  1  1  1) 

#ALL vs RANDOM
#p0=(0  0  0  0  0)
#p1=(20 20 20 20 20)
#p2=(0  20 20 20 20)
#p3=(0  0  0  0  0)
#p4=(0  0  0  0  0)
#p5=(0  0  0  0  1)
#p6=(2  3  4  5  6)
#p7=(1  1  1  1  1)
#p8=(0  0  0  0  0)
#p9=(1  1  1  1  1)

#THREE:
#p0=(0)
#p1=(20)
#p2=(20)
#p3=(0)
#p4=(0)
#p5=(1)
#p6=(2)
#p7=(5)
#p8=(6)
#p9=(0)
#p10=(1)
#p11=(2)


#BF:
#p0=(0)
#p1=(4)
#p2=(5)
#p3=(0)
#p4=(0)
#p5=(1)
#p6=(2)
#p7=(6)
#p8=(0)
#p9=(1)
#p10=(1)
#p11=(2)



nb=${SGE_TASK_ID}-1

#java -jar Tribes.jar ${p0[$nb]} ${p1[$nb]} ${p2[$nb]} ${p3[$nb]} ${p4[$nb]} ${p5[$nb]} ${p6[$nb]} ${p7[$nb]} ${p8[$nb]} ${p9[$nb]} ${p10[$nb]} ${p11[$nb]} >> tribes_${p0[$nb]}_${p1[$nb]}_${p2[$nb]}_${p3[$nb]}_${p4[$nb]}_${p5[$nb]}_${p6[$nb]}_${p7[$nb]}_${p8[$nb]}_${p9[$nb]}_${p10[$nb]}_${p11[$nb]}.txt 2>tribes_${p0[$nb]}_${p1[$nb]}_${p2[$nb]}_${p3[$nb]}_${p4[$nb]}_${p5[$nb]}_${p6[$nb]}_${p7[$nb]}_${p8[$nb]}_${p9[$nb]}_${p10[$nb]}_${p11[$nb]}.err
java -jar Tribes.jar ${p0[$nb]} ${p1[$nb]} ${p2[$nb]} ${p3[$nb]} ${p4[$nb]} ${p5[$nb]} ${p6[$nb]} ${p7[$nb]} ${p8[$nb]} ${p9[$nb]} >> tribes_${p0[$nb]}_${p1[$nb]}_${p2[$nb]}_${p3[$nb]}_${p4[$nb]}_${p5[$nb]}_${p6[$nb]}_${p7[$nb]}_${p8[$nb]}_${p9[$nb]}.txt 2>tribes_${p0[$nb]}_${p1[$nb]}_${p2[$nb]}_${p3[$nb]}_${p4[$nb]}_${p5[$nb]}_${p6[$nb]}_${p7[$nb]}_${p8[$nb]}_${p9[$nb]}.err
