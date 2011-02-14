@echo off
echo Connecting to Android X86 running on VMware via NAT.
adb connect 192.168.152.128
adb devices
pause
