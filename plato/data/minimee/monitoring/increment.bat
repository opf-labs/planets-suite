@echo off
:: Increments a three digit number
:: Works by comparing each digit
:: E5=100-thousend, E4=10-thousend, E3=thousend, E2=hundreds, E1=tens, E0=ones
if [%E5%]==[] set E5=0
if [%E4%]==[] set E4=0
if [%E3%]==[] set E3=0
if [%E2%]==[] set E2=0
if [%E1%]==[] set E1=0
if [%E0%]==[] set E0=0
:E0
if %E0%==9 goto E1
if %E0%==8 set E0=9
if %E0%==7 set E0=8
if %E0%==6 set E0=7
if %E0%==5 set E0=6
if %E0%==4 set E0=5
if %E0%==3 set E0=4
if %E0%==2 set E0=3
if %E0%==1 set E0=2
if %E0%==0 set E0=1
goto DONE
:E1
set E0=0
if %E1%==9 goto E2
if %E1%==8 set E1=9
if %E1%==7 set E1=8
if %E1%==6 set E1=7
if %E1%==5 set E1=6
if %E1%==4 set E1=5
if %E1%==3 set E1=4
if %E1%==2 set E1=3
if %E1%==1 set E1=2
if %E1%==0 set E1=1
goto DONE
:E2
set E1=0
if %E2%==9 goto E3
if %E2%==8 set E2=9
if %E2%==7 set E2=8
if %E2%==6 set E2=7
if %E2%==5 set E2=6
if %E2%==4 set E2=5
if %E2%==3 set E2=4
if %E2%==2 set E2=3
if %E2%==1 set E2=2
if %E2%==0 set E2=1
goto DONE
:E3
set E2=0
if %E3%==9 goto E4
if %E3%==8 set E3=9
if %E3%==7 set E3=8
if %E3%==6 set E3=7
if %E3%==5 set E3=6
if %E3%==4 set E3=5
if %E3%==3 set E3=4
if %E3%==2 set E3=3
if %E3%==1 set E3=2
if %E3%==0 set E3=1
goto DONE
:E4
set E3=0
if %E4%==9 goto E5
if %E4%==8 set E4=9
if %E4%==7 set E4=8
if %E4%==6 set E4=7
if %E4%==5 set E4=6
if %E4%==4 set E4=5
if %E4%==3 set E4=4
if %E4%==2 set E4=3
if %E4%==1 set E4=2
if %E4%==0 set E4=1
goto DONE
:E4
set E5=0
if %E5%==9 goto DONE
if %E5%==8 set E5=9
if %E5%==7 set E5=8
if %E5%==6 set E5=7
if %E5%==5 set E5=6
if %E5%==4 set E5=5
if %E5%==3 set E5=4
if %E5%==2 set E5=3
if %E5%==1 set E5=2
if %E5%==0 set E5=1
goto DONE
:DONE
