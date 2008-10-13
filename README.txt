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


----------------------------------------------------------------

Notes And To Dos (Andy J)
================

 * Allow sub-projects to declare pserv.ws.test.classes for the server version.
 * Add the standalone class path and use it.
 * Add server-config parameters and allow them to be invoked.
 * Allow sub-projects to define whether they are pure java etc, and pick this up automatically (no limited list, default to impure).
 
 
Alternative build environments:

 * OpenEJB should work http://openejb.apache.org/
   But at the present time (3.0 - April 12th, 2008), the JAX-WS standalone testing fails during the Endpoint.create().
   Seems we should wait for a later version with xmlSchema 1.4 instead of 1.3.2.
   - http://open.iona.com/forums/thread.jspa?threadID=186&tstart=0
   - https://issues.apache.org/jira/browse/CXF-1388
   
   