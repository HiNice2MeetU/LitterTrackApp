#!/bin/sh

echo "Launching"
cd .out
java -cp ../sqlite-jdbc-3.34.0.jar:../Paho.jar:../gson-2.8.6.jar:../bcpkix-jdk15on-1.68.jar:../bcprov-jdk15on-1.68.jar:. Main &
