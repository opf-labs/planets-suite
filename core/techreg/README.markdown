Planets Suite TechReg
=====================
This is a really simple technical registry for the Planets Suite. It uses the 
DROID signature file (embedded or freshly-downloaded if possible) to provide a way of 
mapping between different type of format identifier URIs, base on: 

* File extensions.
* MIME media types.
* PRONOM UIDs.


Working with the PRONOM service
-------------------------------
To save re-building the stubs all the time, the PRONOM stubs have been checked 
in under the source tree. If you wish to rebuild the stubs, you can use:

    mvn -Pbuild-stubs generate-sources

Note, however, that this will not automatically download the latest WSDL. There 
were some minor issues with the WSDL which meant they had to be cached and 
patched manually (see comments tagged as 'ANJ' in the WSDL files). To make this
possible, a catalogue file has been used to replace the remote references with 
local copies.
