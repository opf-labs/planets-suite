#!/bin/bash 

workingDirectory=$1
port_to_use=$4
inputFile=$2
outputFile=$3

SED=/bin/sed
function ltrim() { echo "$1" | $SED -e "s/^ *//"; }
function rtrim() { echo "$1" | $SED -e "s/ *$//"; }

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

COMMAND="$OOOPYTHON OpenOfficeDocumentConverter.py"
COMMAND_PARAMS="$inputFile $outputFile $port_to_use"

# find the PID of soffice.bin
pid_soffice=`ps -eo pid,args | grep soffice.bin | grep -v grep | cut -c1-6`
pid_soffice="$(ltrim "$pid_soffice")"
pid_soffice="$(rtrim "$pid_soffice")"

./monitorcall.sh $workingDirectory $pid_soffice "$COMMAND" "$COMMAND_PARAMS"

# $OOOPYTHON OpenOfficeDocumentConverter.py $inputFile $outputFile $port_to_use

pid_top=$!
echo "The thing we want to monitor has pid: $pid_top"

