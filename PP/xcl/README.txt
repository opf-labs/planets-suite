The services in this component require the XCL tools (extractor, comparator, XCLExplorer).

For a version of the tools compatible with the current PSERV head, check out the latest 
'pserv-*' tag from http://gforge.planets-project.eu/svn/xcltools/tags/

Source code and building instructions are available from:

- http://gforge.planets-project.eu/gf/project/xcltools/
- http://planetarium.hki.uni-koeln.de/public/XCL/

A system variable called 'XCLTOOLS_HOME' should point to the the main 
'xcltools' folder checked out via SVN. The folder 'extractor', 'comparator'
and 'XCLExplorer' need to contain the compiled binary with the same name as these 
folders, and all included resources.

To support embedded identification of image files (without giving an XCEL):

Make sure the ImageMagick 'identify' executable is available on the PATH 
when running Java (testable with output of "System.getenv("PATH")").