REM @echo off
set JAVA_HOME="\\achlys\shared\BRC_CRIS\apps\java\jdk1.7.0_25"




%JAVA_HOME%\bin\java  -Djava.library.path="T:\apps\jtds-1.3.1-dist\x86\SSO" -Dgate.home=\\achlys\shared\BRC_CRIS\GATE\gate-7.1-build4485 -cp %JAVA_HOME%\lib\*; -jar "C:\Users\rjackson1\Documents\NetBeansProjects\TextHunter\dist\TextHunter.jar" 

pause