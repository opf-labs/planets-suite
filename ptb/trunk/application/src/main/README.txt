Planets Testbed Project
========================

Homepage: http://gforge.planets-project.eu/svn/ptb/



Prerequisites
-------------

Before installing the Testbed you must have followed the instalation guidelines for the Planets Interoperability Framework (http://gforge.planets-project.eu/svn/if_sp/) and Planets Pserv (http://gforge.planets-project.eu/svn/ptb/). 



Installing the Planets Testbed project
----------------------------------
First, use SVN to check out the Testbed project

% svn co http://gforge.planets-project.eu/svn/ptb/trunk/ ptb

Within the ptb directory go to the 'main' directory:

% cd ptb/application/src/main/

To build the Testbed project, you need a tell it where Planets is installed. 
First, copy 'config.properties.template' to 'config.properties'. 

Now, edit 'config.properties' to point at your IF installation. 
This means setting the 'if_server.dir' property so the same value as you used for 
'framework.test.dir' in the IF framework properties file.

Once this is done, you should be able to build and deploy the Testbed using

% ant deploy:ear



Installing Workflow Execution Engine Templates for use with the Testbed
-----------------------------------------------------------------------
The Testbed uses the IF's Workflow Execution Engine (WEE).  In order to run experiment types you must first register the approprite template with the Testbed.  Do this as follows:

a) all WEE templates are located in if_sp/components/wee/src/main/resources/eu/planets_project/ifr/core/wee/impl/templates.  Find the required template in this directory. The one to use for Migration experiments is defined within the Testbed's backend.properties file and is called TestbedMigrationExperimentTemplate_v1_22122009.java

b) make sure you’ve deployed the WEE through the IF installation

c) log into the TB with admin rights and go to the admin panel – there you can upload a workflow template – chose the one from above



Cleaning the TB and the Database
--------------------------------
Some large changes to the IF or Pserv may require the TB or the TB database to be cleaned before running the deploy:ear command.  If deploy:ear fails to work try cleaning first:

% ant clean

If you would like to remove all TB experiment data use the following commeand (but beware that all data will be removed permanently)

% ant clean.db