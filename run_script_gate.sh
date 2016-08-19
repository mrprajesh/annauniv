#!/bin/bash
cd /home/rajz/Documents/TNEA
echo "start	" `date` >> ngate_log
/home/rajz/install/jdk1.8.0_91/bin/java TncaNG 
echo "end		" `date` >> ngate_log

