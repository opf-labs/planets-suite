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

(1) within the directory '/installer'
create a config.properties file from the provided config.properties.template

(3) modify all required properties in the config.properties file
Currently the IF and TB have integrated. Please note that it's in the user's responsibility to check when building 
the installer that all specified revisions of individual code bases (e.g. IF, TB, Plato) are compliant and well functioning

(4) open a shell and run "ant" from the '/installer' dir.
This executes the default target "checkoutSVNAndBuild:installer" which downloads the source and builds the installer

(5) When the build process has succeeded you will find the executable jar called
PlanetsInstaller.jar within the '/dist' directory.

Enjoy :-)

How to extend the installer
---------------------------------
A) Adding additional components (e.g. Plato)
B) Extending the GUI

A1 - Adding additional components (e.g. Plato)
-) add additional properties to the config.properties.template file for specifying the project's gforge SVN location and make sure the 
  "planets_suite" user is added to your gforge-project
-) open the 'installer/build.xml' and 
  *) add targets for checking out from the SVN. (Don't forget to add an entry to checkoutSVN:all)
  *) add commands to the target "build:installer" if required (e.g. for setting a properties file

continue with step B1

(B1) Extending the Installer GUI
The structure of the installer is mainly defined within 3 files: 
1) install.xml 
2) planets-buildfile.xml which 1),2) are both located under the 'installer/src' 
3) userInputSpec.xml which is located in 'installer/src/extra'
Within 1) the structure of the Installer GUI is defined, packs are created and filled, etc.
within 2) the actual commands (e.g. the IF build.xml file)  are contained which are then executed on the client's machine


Technologies used:
---------------------------------

The Planets Suite Installer is based on IzPack and SVNAnt
http://izpack.org/

IzPack is one of the best available, open source, cross platform and highly customizable solution for packaging and distributing applications.
- it allows to define deployment dependencies between different packs
- build integration: allows to pack all source code and to run ant targets during the client's installation process
- creates a single installer which only requires a Java virtual machine to run
 - user interface, etc. defined within one xml file

For documentation please see
http://izpack.org/documentation/3.11.0/

SVNAnt enables SVN integration within ant projects and supports most of the major Subversion commands.
i.e. enables to run SVN commands (e.g. checkout, update, revert, etc.) within the build.xml's targets

For documentation please see
http://subclipse.tigris.org/svnant.html

Please note
---------------------------------
-) It's within the developers responsibility to ensure that the specified versions of all sub project's code which are used 
   to build the installer are compliant. Predefined versions of the installer, including compliant SP sources will be exposed 
   within the tags directory e.g. Planets Y2 review
