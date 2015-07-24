@echo off
@title Kao
set CLASSPATH=.;Libs\dist\*
java -server -Dnet.sf.odinms.wzpath=Libs\wz server.Start
pause
