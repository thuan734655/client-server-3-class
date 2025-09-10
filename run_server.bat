@echo off
setlocal
mvn -q -Dexec.mainClass=com.example.server.net.ServerMain exec:java
endlocal
