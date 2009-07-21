@echo off
rem ==$Workfile: publish.cmd $==========================================
rem execute eu.planets-project.service.migration.generic.GenericMigratePublish_1_0
rem in lib/GenericMigrate-1-0.jar. 
rem Version    : $Id$
rem Application: PLANETS PA/4 migration services
rem Platform   : Win32
rem --------------------------------------------------------------------
rem Copyright   : Swiss Federal Archives, Berne, Switzerland 
rem               (pending PLANETS copyright agreements)
rem Created     : July 09, 2009, Hartwig Thomas, Enter AG, Zurich
rem Sponsor     : Swiss Federal Archives, Berne, Switzerland
rem ====================================================================
if "%~1"=="-?" goto help
if "%~1"=="/?" goto help
if "%~1"=="-h" goto help
if "%~1"=="/h" goto help
chcp 1252

rem --------------------------------------------------------------------
rem execution directory from which cmd is called
rem --------------------------------------------------------------------
set execdir=%~dp0
rem detach the trailing backslash
set execdir=%execdir:~0,-1%

rem --------------------------------------------------------------------
rem local variables
rem --------------------------------------------------------------------
set executable=java.exe
set java=

rem --------------------------------------------------------------------
rem JAVA_OPTS
rem --------------------------------------------------------------------
set options=-Djava.endorsed.dirs="%execdir%\..\lib\endorsed" -Djava.util.logging.config.file="%execdir%\..\etc\logging.properties" %JAVA_OPTS%

:regcheck
rem --------------------------------------------------------------------
rem check registry HKLM\SOFTWARE\JavaSoft\Java Runtime Environment\xxx
rem for JavaHome\bin\%executable%
rem --------------------------------------------------------------------
@echo checking registry
set key=HKLM\SOFTWARE\JavaSoft\Java Runtime Environment
for /f "skip=4 delims=	 tokens=3" %%f in ('reg.exe query "%key%" /v CurrentVersion') do set cv=%%~f
if "%cv%"=="" goto javacheck
set key=%key%\%cv%
for /f "skip=4 delims=	 tokens=3" %%f in ('reg.exe query "%key%" /v JavaHome') do set jh=%%~f
if "%jh%"=="" goto javacheck
set java=%jh%\bin\%executable%
if exist "%java%" goto execute
@echo File "%java%" could not be found!
goto javacheck

:javacheck
rem --------------------------------------------------------------------
rem check environment variable JAVA_HOME for %executable%
rem --------------------------------------------------------------------
@echo checking JAVA_HOME
if "%JAVA_HOME%"=="" goto pathcheck
set java=%JAVA_HOME%\bin\%executable%
if exist "%java%" goto execute
@echo File "%java%" could not be found!
goto error

:pathcheck
rem --------------------------------------------------------------------
rem check environment variable PATH for %executable%
rem --------------------------------------------------------------------
@echo checking PATH
for %%f in (%executable%) do (set java=%%~$PATH:f)
if "%java%"=="" goto cdrivecheck
@echo JAVA Executable based on PATH: "%java%"
goto execute

:cdrivecheck
rem --------------------------------------------------------------------
rem check c-drive for %executable%
rem --------------------------------------------------------------------
rem skip this lengthy search ...
goto error
rem remove the line above, if you want to search the c-drive 
@echo checking C:\
rem search for %executable% on c:
@echo dir /b/s C:\%executable%>"%TEMP%\searchc.cmd"
for /f "delims=" %%f in ('"%TEMP%\searchc.cmd"') do set java=%%f
del "%TEMP%\searchc.cmd"
if "%java%"=="" goto error
@echo JAVA Executable found on C: "%java%"
goto execute

:execute
rem --------------------------------------------------------------------
rem execute lib/GenericMigrate-1-0.jar
rem --------------------------------------------------------------------
"%java%" %options% -jar "%execdir%\..\lib\GenericMigrate-1-0.jar" %*
goto exit

:error
rem --------------------------------------------------------------------
rem error message for missing %executable%
rem --------------------------------------------------------------------
@echo "No valid javaw.exe could be found.                                  "
@echo "Install the JAVA JRE or indicate correct path on the command line.  "

:help
rem --------------------------------------------------------------------
rem help for calling syntax
rem --------------------------------------------------------------------
rem we need the quotes for protecting the angular brackets
@echo "Calling syntax                                                      "
@echo "  publish.cmd [<host>]                                              "
@echo "executes                                                            "
@echo "  eu.planets-project.service.migration.generic.                     "
@echo "                                          GenericMigratePublish_1_0 "
@echo "in lib/GenericMigrate-1-0.jar                                       "
@echo "                                                                    "
@echo "Arguments:  (See user's manual for more detail).                    "
@echo "  <host>    hostname/IP optionally followed by : and the port       "
@echo "            (default: localhost:8080)                               "
@echo "                                                                    "
@echo "Javahome:                                                           "
@echo "            First the registry under HKLM\SOFTWARE\JavaSoft         "
@echo "            is searched for CurrentVersion and for JavaHome         "
@echo "            for locating the javaw.exe.                             "
@echo "                                                                    "
@echo "            Then if an environment variable JAVA_HOME exists,       "
@echo "            it is used for locating the javaw.exe.                  "
@echo "                                                                    "
@echo "            Otherwise the environment variable PATH is searched for "
@echo "            javaw.exe.                                              "
@echo "                                                                    "
@echo "            If this search fails too, the C-drive is searched for   "
@echo "            javaw.exe.                                              "
@echo "                                                                    "
@echo "Javaopts:                                                           "
@echo "            The environment variable %%JAVA_OPTS%% is used as a       "
@echo "            source for additional JAVA options.                     "
@echo "            E.g. "-DproxyHost=www-proxy.admin.ch -DproxyPort=8080"  "
@echo "                                                                    "

rem --------------------------------------------------------------------
rem exit
rem --------------------------------------------------------------------
:exit
