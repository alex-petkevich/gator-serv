#!/bin/bash

chown gator.gator ${WORKSPACE}/target/gator-1.0-beta.jar
chmod u+x ${WORKSPACE}/target/gator-1.0-beta.jar
systemctl gator stop
sleep 3
cp ${WORKSPACE}/target/gator-1.0-beta.jar /mnt/vol1/www/gator/gator.jar
systemctl gator start
