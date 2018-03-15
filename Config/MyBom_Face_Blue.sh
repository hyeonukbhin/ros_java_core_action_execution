#!/bin/bash


trap 'onDestroySh' EXIT
onDestroySh()
{
    echo "onDestroySh"
    pkill -9 -ef nidRobot
    pkill -9 -ef node
    exit 0
}


echo "PID: " $$

cd /home/kist/ctit/robot
node app &

cd /home/kist/ctit/nidRobot-linux-x64/
./nidRobot 

echo "done"
