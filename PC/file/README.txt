File Identify Service Service
-----------------------------

This is a file identification service based around the Cygwin File utility.  The following must
be true to run this service:

1) It currently only works on windows boxes. It is planned to extend the service to run on
   unix / linux boxes that have a native file command.
   
2) The windows box must have Cygwin installed (or at least the cygwin file.exe) available.

The properties file /resources/eu/planets_project/services/file/FileIdentify.properties
has an entry cygwin.file.location that should be pointed to the location of the file executable.