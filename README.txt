Planets Services Project
========================

Homepage: http://gforge.planets-project.eu/gf/pserv/

Service Development Guidelines
------------------------------
Go to this URL to get full details on developing Planets Services.

http://www.planets-project.eu/private/pages/wiki/index.php/Service_Developers_Guidelines

Prerequisites
-------------

The Planets Services can be built without installing any further 
software, and services can be tested without an application server.
However, an application server is required in order to deploy
these services properly.

The Planets Services project is designed to be deployed against 
an instance of the Planets IF. To install the IF and its dependencies
please follow the instructions shown here:
  - http://gforge.planets-project.eu/svn/if_sp/trunk/INSTALL_IF.txt

It should be possible to deploy the Planets Services against another 
J5EE application server, and you can try this by setting the 
server.deploy.dir and server.deploy.lib.dir properties in 
build.properties.  However, other application servers are not 
supported by the IF team at present, due to the complexity and
degree of variation between platforms.

As a particular example, if you want to use Metro-on-Tomcat, please be 
aware that this requires a very different deployment model to 
the one we've been using up to now.  Instead of deploying the 
endpoints as EJBs, you would need to repackage them as a WAR 
so that the services can be deployed as servlets. 


Using the Planets Services project
----------------------------------
First, use SVN to check out the pserv project

% svn co http://gforge.planets-project.eu/svn/pserv/trunk/ pserv

And change into the pserv directory

% cd pserv

To build the pserv project, you need a tell it where Planets is installed. 
First, copy 'build.properties.template' to 'build.properties'. 

Now, edit 'build.properties' to point at your IF installation. 
This means setting the 'if_server.dir' property so the same value as you
used for 'framework.test.dir' in the IF framework properties file.  The
'if_server.conf' property should be set to the same  value as the the
'framework.config.dir' in the IF framework properties file.

The 'if_server.doms.config.dir' property points to a directory used to
place Planets Data Registry configuration files.

Once this is done, you should be able to build and deploy the services using

% ant deploy:all

This does not mean that all services will work, as many of them require 
individual configuration.  You can deploy only those service that do not 
have external dependancies using

% ant deploy:pure-java

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


----------------------------------------------------------------

Notes And To Dos
================

 * Should exclude API jar from EAR?  Seems not to matter for TechReg, even on hot redeploy.

Alternative build environments:

 * OpenEJB should work http://openejb.apache.org/
   But at the present time (3.0 - April 12th, 2008), the JAX-WS standalone testing fails during the Endpoint.create().
   Seems we should wait for a later version with xmlSchema 1.4 instead of 1.3.2.
   - http://open.iona.com/forums/thread.jspa?threadID=186&tstart=0
   - https://issues.apache.org/jira/browse/CXF-1388
   
   