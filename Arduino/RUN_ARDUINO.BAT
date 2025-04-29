
clear
arduino-cli.exe compile --fqbn arduino:avr:uno ComplexArduinoCode

TIMEOUT 4
arduino-cli.exe upload -p COM8 --fqbn arduino:avr:uno ComplexArduinoCode

TIMEOUT 20