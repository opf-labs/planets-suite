The comparator service calls a local command-line tool. To set up your system:

1. Add the 'src/resources/COMPARATOR_HOME' folder to your path or move the comparator executable 
   to a folder on your path ("comparator" for Linux, "comparator.exe" for Windows)
2. Set an environment variable called COMPARATOR_HOME that points to the 'src/resources/COMPARATOR_HOME' 
   folder (with 'export COMPARATOR_HOME="/path/to/the/folder/above"' in a Bash where you start the IF 
   JBoss, or in the 'System' control panel on Windows)