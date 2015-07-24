@echo off
@title Dump
set CLASSPATH=.;dist\*
java -Xms256m -Xmx512m -server -Dwzpath=wz tools.wztosql.DumpMobSkills
pause