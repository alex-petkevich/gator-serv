#!/bin/bash

chown gator.gator ./target/gator-1.0-beta.jar
chmod u+x ./target/gator-1.0-beta.jar
systemctl stop gator
sleep 3
cp ./target/gator-1.0-beta.jar /mnt/vol1/www/gator/gator.jar
systemctl start gator
