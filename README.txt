Planets Services Project
========================

Homepage: http://gforge.planets-project.eu/gf/pserv/


Prerequisites
-------------

The Planets Services project is designed to be deployed against 
an instance of the Planets IF. To install the IF and its dependencies
please follow the instructions shown here:
  - http://gforge.planets-project.eu/svn/if_sp/trunk/INSTALL_IF.txt
  

Using the Planets Services project
----------------------------------
First, use SVN to check out the pserv project

% svn co http://gforge.planets-project.eu/svn/pserv/trunk/ pserv

And change into the pserv directory

% cd pserv

To build the pserv project, you need a tell it where Planets is installed. 
First, copy 'build.properties.template' to 'build.properties'. 

Now, edit 'build.properties' to point at your IF installation. 
This means setting the '' property so the same value as you used for 
'framework.test.dir' in the IF framework properties file.

Once this is done, you should be able to build and deploy the services using

% ant deploy:all

This does not mean that all services will work, as many of them require 
individual configuration.

Therefore, each service project may have its own installation notes. 
For example, the Xena services require OpenOffice to be installed, 
and so this project provides some installation notes on this 
in PA/xena/README.txt.


---------------------------------------------------------------
Developer Guildlines
====================

Outline of build system.

Source layout

Conventions (e.g. appName like 'pa-xena').

Build system.  EJB only at present.

How to create a new service project.

