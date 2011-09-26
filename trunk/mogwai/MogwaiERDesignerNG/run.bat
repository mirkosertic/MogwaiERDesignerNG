@echo off

::IDs are hex values
set ID64Bit=40
set ID32Bit=20

::reg.exe does not exist on Windows 2000, force 32bit then
if not exist %windir%\system32\reg.exe goto 32bit

reg.exe query "HKLM\Hardware\Description\System\CentralProcessor\0" /v "Platform ID" | find "0x%ID64Bit%" > nul
if %ERRORLEVEL% == 0 (
	goto 64bit
) else (
	goto 32bit
)

:32bit
java -Djava.library.path=.\java3d\win32 -cp .\lib\mogwai-smartstart-1.1.jar de.mogwai.smartstart.SmartStart de.erdesignerng.visual.ERDesigner lib
goto end

:64bit
java -Djava.library.path=.\java3d\win64 -cp .\lib\mogwai-smartstart-1.1.jar de.mogwai.smartstart.SmartStart de.erdesignerng.visual.ERDesigner lib
goto end

:end