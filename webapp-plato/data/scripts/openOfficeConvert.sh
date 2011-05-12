#!/bin/bash 

port_to_use=$1
inputFile=$2
outputFile=$3

OOFFICE=`ls /usr/bin/openoffice.org2.4 /usr/bin/ooffice /usr/lib/openoffice/program/soffice | head -n 1` 
OOOPYTHON=`ls /opt/openoffice.org*/program/python /usr/bin/python | head -n 1` 

if [ ! -x "$OOFFICE" ] 
then 
  echo "Could not auto-detect OpenOffice.org binary" 
  exit 
fi 

if [ ! -x "$OOOPYTHON" ] 
then 
  echo "Could not auto-detect OpenOffice.org Python" 
  exit 
fi 

echo "Detected OpenOffice.org binary: $OOFFICE" 
echo "Detected OpenOffice.org python: $OOOPYTHON" 

echo "input: $inputFile output: $outputFile port: $port_to_use"

$OOOPYTHON OpenOfficeDocumentConverter.py $inputFile $outputFile $port_to_use

pid_top=$!
echo "The thing we want to monitor has pid: $pid_top"

