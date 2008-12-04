echo off
set TARGETDIR=src-dump-axis1
set AXIS_LIB=axis-1_4\lib
set LOG_LIB=.\..\..\net.bioclipse.logger\log4j\log4j-1.2.15.jar
set AXISCP=.;%AXIS_LIB%\axis.jar
set AXISCP=%AXISCP%;%AXIS_LIB%\commons-discovery-0.2.jar
set AXISCP=%AXISCP%;%AXIS_LIB%\jaxrpc.jar
set AXISCP=%AXISCP%;%AXIS_LIB%\saaj.jar
set AXISCP=%AXISCP%;%AXIS_LIB%\wsdl4j-1.5.1.jar
set AXISCP=%AXISCP%;%AXIS_LIB%\commons-logging-1.0.4.jar
set AXISCP=%AXISCP%;%AXIS_LIB%\log4j-1.2.8.jar
set AXISCP=%AXISCP%;%AXIS_LIB%\commons-logging-1.0.4.jar
echo on
echo %AXISCP%
java -cp %AXISCP% org.apache.axis.wsdl.WSDL2Java -o %TARGETDIR% --wrapArrays --timeout -1 %*