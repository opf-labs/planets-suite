#!/bin/bash 
# Try to autodetect OOFFICE and OOOPYTHON. 

SED=/bin/sed
function ltrim() { echo "$1" | $SED -e "s/^ *//"; }
function rtrim() { echo "$1" | $SED -e "s/ *$//"; }

port_to_use=$1

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

# Reference: http://wiki.services.openoffice.org/wiki/Using_Python_on_Linux 
# If you use the OpenOffice.org that comes with Fedora or Ubuntu, uncomment the following line: 
# export PYTHONPATH="/usr/lib/openoffice.org/program" 

# If you want to simulate for testing that there is no X server, uncomment the next line. 
#unset DISPLAY 
# Kill any running OpenOffice.org processes. 


# killall -u `whoami` -q soffice 

# Download the converter script if necessary. 
# test -f DocumentConverter.py || wget http://www.artofsolving.com/files/DocumentConverter.py 

# Start OpenOffice.org in listening mode on TCP port 8100. 
$OOFFICE "-accept=socket,host=localhost,port=$port_to_use;urp;StarOffice.ServiceManager" -norestore -nofirststartwizard -nologo -headless & 

TIMEOUT=5

count=0
empty="x";
while (($count < $TIMEOUT )); do
  pid_soffice=`ps -eo pid,args | grep soffice.bin | grep -v grep | cut -c1-6`

  # soffice is now started
  if [[ "$pid_soffice" != "" ]]
  then
    count=TIMEOUT
  fi

  ((count++))
  sleep 1
done

pid_soffice="$(ltrim "$pid_soffice")"
pid_soffice="$(rtrim "$pid_soffice")"

echo "Process ID of soffice.bin: $pid_soffice"

# soffice coudn't be started
if [[ "$pid_soffice" == "" ]]
then
  exit 1
fi

ret=`netstat -tulp | grep ":$port_to_use" | grep "$pid_soffice/soffice.bin"`

echo "return=$ret"

# empty=`ps -p $pid_soffice | grep -v PID`
# soffice couldn't be started
if [[ "$ret" == "" ]]
then
  exit 1
fi

# Wait a few seconds to be sure it has started. 
# sleep 5s 

# soffice.bin is doing all the work

pid_soffice=`ps -eo pid,args | grep soffice.bin | grep -v grep | cut -c1-6`
echo "OpenOffice started with PID: $pid_soffice"


# Convert as many documents as you want serially (but not concurrently).
 # Substitute whichever documents you wish. 

# $OOOPYTHON DocumentConverter.py $input_file $output_file $port_to_use
# $OOOPYTHON DocumentConverter.py sample.ppt sample.pdf 

# Close OpenOffice.org. 

# killall -u `whoami` soffice

exit 0
