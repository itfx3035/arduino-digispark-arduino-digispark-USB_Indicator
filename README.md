# USB_Indicator
<b>USB_Indicator</b><br>
This is example of DigiUSB implementation for Digispark board.<br>
Additional LEDs are connected to P0 and P2 ports of Digispark via 2 kOm resistors. Please note that P1 port is connected to onboard red LED.<br>
Contains:
<br>- project for Arduino IDE to flash Digispark
<br>- project for NetBeans. It is just a sample how to build notification software for Digispark boards. <a href="https://javaee.github.io/javamail/">JavaMail</a> lib is used
<br>cron.txt - sample events config file. Please see NetBeans project for details.<br>
You also need to download both micronucleus driver and Windows DigiUSB programs from here: https://digistump.com/wiki/digispark/tutorials/digiusb <br>
Please save Windows DigiUSB programs to digiusb_bin subfolder near the compiled jar file and also install drivers.<br>
Run this: send.exe 1001-0101-0011-1001-0101-0011-0005-1115-0000-<br>
If your device is soldered correctly, than you will see something like this: https://youtu.be/g-Rxqfe3G_U
