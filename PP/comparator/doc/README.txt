The comparator service calls a local command-line tool. To set up your system:

1. Add the content of the latest ZIP file in 'pserv/PP/src/resources/' to your path or move 
   the content to a folder on your path.
2. Set an environment variable called COMPARATOR_HOME that points to the unzipped 
   folder (with 'export COMPARATOR_HOME="/path/to/the/unzipped/folder"' in a Bash 
   where you start the IF JBoss, or in the 'System' control panel on Windows)
3. To check if everything is set up correctly, run the tests in pserv/PP/comparator/test/java
   (if you use Eclipse, it should be available as one of the source folders)