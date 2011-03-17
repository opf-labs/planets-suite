Planets Suite Project
========================

Homepage: http://sourceforge.net/projects/planets-suite/

Service Development Guidelines
------------------------------
Go to this URL to get full details on developing Planets Services.

http://www.planets-project.eu/docs/reports/Planets_IF6-D3_Service_Developers_Guidelines.pdf

Prerequisites
-------------

The Planets Services can be built without installing any further 
software, and services can be tested without an application server.
However, an application server is required in order to deploy
these services properly.

The Planets Suite project is designed to be deployed against 
an instance of the Planets Server. To install the server
please follow the instructions shown here:
  - http:/planets-server.svn.sourceforge.net/viewvc/planets-server/trunk/INSTALL_PLANETS-SERVER.txt

It should be possible to deploy the Planets Services against another 
J5EE application server.  However, other application servers are not 
supported by the IF team at present, due to the complexity and
degree of variation between platforms.

As a particular example, if you want to use Metro-on-Tomcat, please be 
aware that this requires a very different deployment model to 
the one we've been using up to now.  Instead of deploying the 
endpoints as EJBs, you would need to repackage them as a WAR 
so that the services can be deployed as servlets. 

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
   
   