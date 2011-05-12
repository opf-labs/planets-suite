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

SED=/bin/sed
function ltrim() { echo "$1" | $SED -e "s/^ *//"; }
function rtrim() { echo "$1" | $SED -e "s/ *$//"; }

DIRECTORY=$1
COMMAND=$2
PARAMETERS=$3

echo "We will now execute command \"$COMMAND\" in directory \"$DIRECTORY\" with parameters $PARAMETERS"

# mkdir $DIRECTORY
cd $DIRECTORY
pwd

TOP_LOG=top.log

CMD_TO_EXECUTE="$COMMAND $PARAMETERS"

# find the PID of soffice.bin which we want to monitor
pid_soffice=`ps -eo pid,args | grep soffice.bin | grep -v grep | cut -c1-6`
pid_soffice="$(ltrim "$pid_soffice")"
pid_soffice="$(rtrim "$pid_soffice")"

echo "PID of soffice.bin: $pid_soffice"

# soffice.bin (pid_soffice) is the task/application we want to monitor
PID_TO_MONITOR=$pid_soffice

#
# we start 'top' for monitoring
# 
echo "monitored_pid=" $PID_TO_MONITOR >> $TOP_LOG
top -b -d0.1 -n100000 -p $PID_TO_MONITOR >> "$TOP_LOG" &
pid_top=$!
echo "PID of top: $pid_top"

echo "Starting to execute $CMD_TO_EXECUTE"
${CMD_TO_EXECUTE}
echo "DONE executing the command..."

pid_command=$!
echo "PID of command: $pid_command";

#
# We kill the task 'top', so it stops to write to log file
#
empty=`ps -p $pid_top | grep -v PID`
if [[ "$empty" != "" ]]
then
  echo "We kill top now!"
  kill "$pid_top"
fi





