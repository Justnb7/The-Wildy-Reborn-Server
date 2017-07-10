@echo off
title Venenatis
color B
java -Xms512m -Xmx1024m -cp bin;lib/* com.venenatis.server.Server
pause