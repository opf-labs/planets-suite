<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<!DOCTYPE helpset PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN" "http://java.sun.com/products/javahelp/helpset_1_0.dtd">
<!--
COPYRIGHT STATEMENT:
   © The National Archives 2005-2006.  All rights reserved.
   See Licence.txt for full licence details.

DESCRIPTION:
	The helpset file for JavaHelp to be used for uk
UPDATE RECORD:
Date		Version		Who			Comment
06-Apr-2005   	V1.R0.M0    	S.Malik (Tessella)    	Created
09-May-2005     V1.R0.M1        S.Malik (Tessella)      Define custom toolbar icons
-->
<helpset version="1.0">
    <title>Title</title>
    <maps>
        <homeID>top</homeID>
        <mapref location="jhelpmap.jhm"/>
    </maps>

    <view>
        <name>TOC</name>
        <label>Table Of Contents</label>
        <type>javax.help.TOCView</type>
        <data>jhelptoc.xml</data>
    </view>

    <presentation default="true">

        <toolbar>
            <helpaction image="backIcon">javax.help.BackAction</helpaction>
            <helpaction image="forwardIcon">javax.help.ForwardAction</helpaction>
            <helpaction image="printIcon">javax.help.PrintAction</helpaction>
            <helpaction image="printSetupIcon">javax.help.PrintSetupAction</helpaction>
        </toolbar>
    </presentation>


</helpset>