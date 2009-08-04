The services in this component require the latest version of the XCL tools
(extractor, comparator, explorer).

Source code and building instructions are available from:

- http://gforge.planets-project.eu/gf/project/xcltools/
- http://planetarium.hki.uni-koeln.de/public/XCL/

The binaries need to be placed in directories pointed to by environment 
variables, the following variables should point to directories containing 
these binaries:

EXTRACTOR_HOME -> Directory containing 'extractor' binary and resources
COMPARATOR_HOME -> Directory containing 'comparator' binary and resources
FPMTOOL_HOME -> Directory containing 'fpmTool' binary and resources

To support embedded identification of image files (without giving an XCEL):

Make sure the ImageMagick 'identify' executable is available on the PATH 
when running Java (testable with output of "System.getenv("PATH")").