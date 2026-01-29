#!/usr/bin/env bash

# create bin directory if it doesn't exist
if [ ! -d "../bin" ]
then
    mkdir ../bin
fi

# delete output from previous run
if [ -e "./ACTUAL.TXT" ]
then
    rm ACTUAL.TXT
fi

# delete data file from previous run (tests expect a clean start)
if [ -e "../data/Jarvis.txt" ]
then
    rm ../data/Jarvis.txt
fi

# compile the code into the bin folder, terminates if error occurred
if ! javac -cp ../src/main/java -Xlint:none -d ../bin $(find ../src/main/java -name "*.java")
then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

# run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
java -classpath ../bin jarvis.Jarvis < input.txt > ACTUAL.TXT

# convert to UNIX format, mormalize line endings to avoid false failures
cp EXPECTED.TXT EXPECTED-UNIX.TXT
if command -v dos2unix >/dev/null 2>&1
then
    dos2unix ACTUAL.TXT EXPECTED-UNIX.TXT
else
    perl -pi -e 's/\r$//' ACTUAL.TXT EXPECTED-UNIX.TXT
fi

# compare the output to the expected output
diff ACTUAL.TXT EXPECTED-UNIX.TXT
if [ $? -eq 0 ]
then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi