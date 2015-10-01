#!/bin/sh
pkill java
export CLASSPATH=.:dist/*
java  -Xmx3500m -Dfile.encoding=UTF-8 -server server.Start
