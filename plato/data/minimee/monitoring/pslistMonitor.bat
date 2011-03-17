

::
:: DEFINITION OF THE PARAMETERS:
:: 
:: WARNING: %2 must be a number of exacly 5 units
::
:: %1 ... the working directory, this is where the top.log file will be written
::        and in case the COMMAND which will be executed can write its temp. data
::
:: %2 ... the Cycle that habe to been executed before stop (must be of 6 units)
::       
:: %3 ... the command that shall be executed and monitored, e.g. ps2pdf
::
:: %4 ... parameters the COMMAND has (e.g. input and output file) (in brackets: example "-o outputfile")
::
:: %5 ... outputfile of the extracted informations
::
:: %6 ... path where to find the increment.bat script
::

:: USAGE EXAMPLE
:: pslistMonitor.bat . 000100 out.txt lame "-b 192 1.wav test.mp3"

:: PSlist command explanation
:: Pri: Priority
:: Thd: Number of Threads
:: Hnd: Number of Handles
:: VM: Virtual Memory
:: WS: Working Set
:: Priv: Private Virtual Memory
:: Priv Pk: Private Virtual Memory Peak
:: Faults: Page Faults
:: NonP: Non-Paged Pool
:: Page: Paged Pool
:: Cswtch: Context Switches

set DIRECTORY=%1
set CYCLE=%2
set OUTPUT_FILE=%3
set LOG=%4
set INCREMENT_PATH=%5

set COMMAND=%6
set PARAMETERS=%7

set tmp=%PARAMETERS:~1%
set PARAMETERS=%tmp:~0,-1%

::del %OUTPUT_FILE%

::convert timeout in the script variable cycles
set F5=%CYCLE:~0,1%
set F4=%CYCLE:~1,1%
set F3=%CYCLE:~2,1%
set F2=%CYCLE:~3,1%
set F1=%CYCLE:~4,1%
set F0=%CYCLE:~5,1%

set TIMEOUT=%CYCLE%0

echo "increment %E0% "

::set default path if INCREMENT_PATH is not set
if [%INCREMENT_PATH%]==[] set INCREMENT_PATH=d:/bin/increment.bat

:: Initialize the variables for the counter
:: E5=100-thousend, E4=10-thousend, E3=thousend, E2=hundreds, E1=tens, E0=ones
set E5=0
set E4=0
set E3=0
set E2=0
set E1=0
set E0=0


::::::::::::::::::::: BEGIN  ::::::::::::::::::::::::::

echo "The process has %TIMEOUT% milliseconds to finish."

:::::echo "We will now execute command \"%COMMAND%\" in directory \"%DIRECTORY%\" with parameters %PARAMETERS%"

cd %DIRECTORY%

set TOP_WIN_LOG=top_win.log

set CMD_TO_EXECUTE=%COMMAND% %PARAMETERS%


::MONITOR PROGRAM
set command_start=0

:START

::START
if [%command_start%]==[1] goto SKIP

echo "started program"
start /B "myprog" %CMD_TO_EXECUTE% 2> %LOG%

::read the information of the process and save them in the outputfile
cmd /C pslist -x -m %COMMAND% >> %OUTPUT_FILE%

sleep -m 10

set command_start=1
set PID=null
:SKIP


::::cmd /C pslist -m %COMMAND% > tmp.txt
::::set /P commandScreen=< tmp.txt


set PID=null
for /f "tokens=2" %%i in ('pslist -m %COMMAND%^|findstr /b "%COMMAND%"') do set "PID=%%i"

:: Here I will execute my monitoring file
cmd /C pslist -x -m %COMMAND% >> %OUTPUT_FILE%

:: CHECK if the Program with the given PID is runnig
echo %PID%
if [%PID%]==[null] goto END

CALL %INCREMENT_PATH%

::check that the cycle are how the counter
if NOT [%E5%]==[%F5%] goto FIN
if NOT [%E4%]==[%F4%] goto FIN
if not [%E3%]==[%F3%] goto FIN
if not [%E2%]==[%F2%] goto FIN
if not [%E1%]==[%F1%] goto FIN
if not [%E0%]==[%F0%] goto FIN

goto KILL
:FIN

:: sleep 10 mlliseconds 
sleep -m 10

goto START

:KILL
echo Program has to be killed...
taskkill /f /FI "PID eq %PID%"

:END
echo Program terminated successfully






