@ECHO OFF

REM create bin directory if it doesn't exist
if not exist ..\bin mkdir ..\bin

REM delete output from previous run
if exist ACTUAL.TXT del ACTUAL.TXT

REM delete data file from previous run (each test expect a clean start)
if exist ..\data\Jarvis.txt del ..\data\Jarvis.txt

REM compile the code into the bin folder
if exist SOURCES.TXT del SOURCES.TXT
dir /s /b ..\src\main\java\*.java > SOURCES.TXT
javac  -cp ..\src\main\java -Xlint:none -d ..\bin @SOURCES.TXT
del SOURCES.TXT
IF ERRORLEVEL 1 (
    echo ********** BUILD FAILURE **********
    exit /b 1
)
REM no error here, errorlevel == 0

REM run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
java -classpath ..\bin jarvis.Jarvis < input.txt > ACTUAL.TXT

REM compare the output to the expected output
FC ACTUAL.TXT EXPECTED.TXT
