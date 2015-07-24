@echo off
@title Dump
set CLASSPATH=.;Libs\dist\*
java -Xms256m -Xmx512m -server -Dwzpath=Libs\wz\ tools.wztosql.MonsterDropCreator
pause