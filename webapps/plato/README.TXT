Plato 3.0
===========

* admin-utils

Plato provides some functions which only administrators can access and
run via the admin-utils page. This page is not linked and must be called
from http://localhost:8080/plato/project/admin-utils.seam

To carry out any of the actions on the admin-utils page the administrator
has to enter a predefined password.  
eu.planets_project.pp.plato.application.AdminAction.code stores this
password SHA-1 encoded.

Before deploying Plato you should change AdminAction.code to some password
you want the admin-util functions to be protected by.
