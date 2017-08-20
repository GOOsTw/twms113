#!/bin/bash

process=`ps -a |grep "java" |awk '{print $1}'`
sleeptime=20

if [[ $zero -ne $process ]]; then
    echo "Get java process $process running"
    echo "Closing..."
    `sudo kill -2 $process`
fi

if [[ $? != 0 ]]; then
    echo "Kill process successful."
fi
echo "Server will start in $sleeptime seconds..."
sleep $sleeptime

tmux has-session -t kumams
if [[ $? != 0 ]]; then
    echo "tmux session kumams not found"
    echo "Creating session kumams"
    tmux new-session -d -s kumams
fi

echo "Starting the server"
tmux select-window -t kumams:0
tmux send-keys "./linux.sh" "C-m"

if [[ $? == 0 ]]; then
    echo "Server started"
fi
