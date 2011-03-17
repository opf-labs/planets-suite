# set -x
LOG=$0.log

#
# DEFINITION OF THE PARAMETERS:
#
# $1 ... the working directory, this is where the top.log file will be written
#        and in case the COMMAND which will be executed can write its temp. data
# $2 ... the PID that shall be monitored. If $2 != 0 then we start to monitor
#        this PID right before we execute COMMAND. If $2 == 0 then we start
#        COMMAND, determine its PID and start to monitor it with top.
# $3 ... the command that shall be executed and monitored, e.g. ps2pdf
# $4 ... parameters the COMMAND has (e.g. input and output file)
#

DIRECTORY=$1
TIMEOUT=$2
COMMAND=$3
PARAMETERS=$4

#
# We calculate the timeout of the COMMAND
#

# We assume that the first parameter in PARAMETERS is the file that shall be processed (migrated)
# FILE_TO_PROCESS=`echo $PARAMETERS | awk '{print $1}'`

# we deterime the file size
# FileSizeInMB=`du -ms $FILE_TO_PROCESS | cut -f1`

# echo "$FileSizeInMB"

# we define a min. timeout of 60 seconds
# TIMEOUT=60
# TIMEOUT=max(FileSizeInMB*6, TIMEOUT) ... so we take the maximum of FileSizeInMB*6 and 60
# if [[ $((FileSizeInMB * 6)) > $TIMEOUT ]]
# then
#  	TIMEOUT=$((FileSizeInMB * 6))
# fi

echo "The process has $TIMEOUT seconds to finish."

echo "We will now execute command \"$COMMAND\" in directory \"$DIRECTORY\" with parameters $PARAMETERS"

cd $DIRECTORY
pwd

TOP_LOG=top.log

CMD_TO_EXECUTE="$COMMAND $PARAMETERS"

echo "We execute: $CMD_TO_EXECUTE"

# echo "monitored_pid=" $PID_TO_MONITOR >> $TOP_LOG
top -b -d0.1 -n100000  >> "$TOP_LOG" &
pid_top=$!

# sleep 1s to give top some time to start up
sleep 1s

echo "We are executing the command..."
${CMD_TO_EXECUTE} &
echo "DONE executing the command..."

pid_command=$!

count=0

# we wait for the command to end
empty="x";
finished=0
while (($count < $TIMEOUT )); do
  empty=`ps -p $pid_command | grep -v PID`
  echo "$empty"
    
  # if ps doesn't find the command anymore, it finished
  if [[ "$empty" == "" ]]
  then
    finished=1
    count=TIMEOUT
  fi

  ((count++))
  sleep 1
done

# if the command timed out, we kill it
if [[ $finished != 1 ]]
then
  echo "Command with PID $pid_command timed out. We kill have to terminate the process."
  kill -9 "$pid_command"
fi

echo "PID of top: $pid_top"
echo "PID of command: $pid_command";

# top should still be running, so we kill the process
empty=`ps -p $pid_top | grep -v PID`
if [[ "$empty" != "" ]]
then
  echo "We kill top now!"
  kill "$pid_top"
fi

# if the COMMAND did not finish successfully (it timed out and we had to terminate it) 
# we exit with code 1
if [[ $finished != 1 ]]
then
  exit 1
fi

echo -n "monitored_pid=" $pid_command >> $TOP_LOG

exit 0
