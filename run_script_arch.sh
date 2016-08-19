#!/bin/bash
cd /home/rajz/Documents/TNEA
echo "start	" `date` >> barch_log
/home/rajz/install/jdk1.8.0_91/bin/java Barch 
echo "end		" `date` >> barch_log

