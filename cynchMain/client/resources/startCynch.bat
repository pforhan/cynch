@echo off

set JCMD="C:\Program Files\JavaSoft\JRE\1.3\bin\javaw.exe"
if not exist %JCMD% set JCMD="C:\Program Files\JavaSoft\Java Web Start\1.3.0\bin\javaw.exe"
if not exist %JCMD% set JCMD="C:\windows\system32\javaw.exe"
if not exist %JCMD% set JCMD="javaw.exe"

cd %1

start "" %JCMD% -classpath cynch\ com.muddyhorse.cynch.Cynch %2 %3 %4 %5 %6 %7 %8 %9
rem java -classpath cynch\ com.muddyhorse.cynch.Cynch %2 %3 %4 %5 %6 %7 %8 %9

set JCMD=
pause