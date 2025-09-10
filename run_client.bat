@echo off
setlocal
mvn -q -Dexec.mainClass=com.example.client.ClientMain exec:java
endlocal
