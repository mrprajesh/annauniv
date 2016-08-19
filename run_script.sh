#!/bin/bash
cd /home/rajz/Documents/TNEA
echo "start " `date` >> log
/home/rajz/install/jdk1.8.0_91/bin/java TNEA && echo "end " `date` >> log

