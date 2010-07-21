********************************************************************************
*                       PLANETS Software Suite Installer                       *
*                                                                              *
*                          Installation Instructions                           *
*                                                                              *
*  Author: Andrew Lindley, Reza Rawassizadeh 																   *
*  Date: 2/12/2008                                                            *
********************************************************************************

How to build a PLANETS Software Suite Installer 
-----------------------------------------------

(0) Install JDK 1.5 and Install Ant version 1.6.5

(1) Optional: within the checkout directory, modify the config.properties file so 
that the SVN urls point to the correct locations. Per default, the URLs point to the
HEAD revision of the trunk. Modification is only necessary in case you want to build
from a different revision, tag or branch.

(2) Open a shell and run "ant svn:checkout" from the checkout dir. This will download
the planets-server and planets-suite projects from the repository into a directory called
"planets-src" (will be created automatically)

(3) Run "ant build:installer" to create the installer.

(4) When the build process has succeeded you will find the executable jar called
PlanetsInstaller.jar within the "dist" directory.

Enjoy :-)

How to extend the installer
---------------------------------
The structure of the installer is mainly defined within 3 files, which are 
located in the "/installer/resources" directory:
 
1) install.xml:
defines the structure of the Installer GUI and installation packs, etc.

2) planets-buildfile.xml:
the 'umbrella' build script which performs token replacement and triggers the
execution of the server and suite build steps (defined in two separate build
scripts - build.server.xml and build.suite.xml, respectively)

3) userInputSpec.xml
the actual GUI declaration file


Technologies used:
---------------------------------
The Planets Suite Installer is created using IzPack 3.11 and SVNAnt
http://izpack.org/

For documentation please see
http://izpack.org/documentation/

SVNAnt enables SVN integration within ant projects and supports most of the major Subversion commands.
i.e. enables to run SVN commands (e.g. checkout, update, revert, etc.) within the build.xml's targets

For documentation please see
http://subclipse.tigris.org/svnant.html

