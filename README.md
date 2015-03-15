# cynch

Cynch is a tiny Java application to distribute/install/update software. The distributed software does not have to be java.

Requirements:
=============
Client side:
----------
* JRE 1.2+ installed ('java -jar' support)
* download and launch a 30-KB jar-file containing update information for the target application 

Server Side:
------------
* Web server that can serve files 

Cynch behavior:
===============
* Extract contents of launch jar to application install location
* load configuration from jar
* using remote URL from configuration, pull down latest configuration
* download remote file manifest, compare to local manifest
* download any files not present in local manifest
* launch start script 

Other comments:
===============
Cynch provides a GUI to show download progress and status contacting the update server.

For planned enhancements, please visit the issues page. 

Automatically exported from code.google.com/p/cynch
