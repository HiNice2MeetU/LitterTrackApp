#!/bin/sh

DIR=$GEDIT_CURRENT_DOCUMENT_DIR

mkdir src
echo "1/3 Cleanup"
rm .out/*.class
cd src
javac *.java -cp ../sqlite-jdbc-3.34.0.jar:../Paho.jar:../gson-2.8.6.jar:../bcprov-jdk15on-1.68.jar:../bcpkix-jdk15on-1.68.jar:. -d ../.out
echo "2/3 Compiled" 
cd ../.out
echo "3/3 Running"
java -cp ../sqlite-jdbc-3.34.0.jar:../Paho.jar:../gson-2.8.6.jar:../bcprov-jdk15on-1.68.jar:../bcpkix-jdk15on-1.68.jar:. Main
