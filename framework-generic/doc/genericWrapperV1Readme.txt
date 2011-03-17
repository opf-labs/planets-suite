The generic wrapper uses an XML configuration file to understand how it is
supposed to function. See exampleConfigfile.xml for an example.

It returns by reference as default, but this is switchable to return by value,
depending on system load etc.

The service description characteristics and fields are discussed at:
http://www.planets-project.eu/private/pages/wiki/index.php/IF/Services/Datatypes/ServiceDescription#classname_.3Cclassname.3E

Each migration path (defined as using a single migration service and having
a single output format) requires a <path>-description.
this includes input formats - It is recommended to use PRONOM URIs where
possible in order to achieve the most accurate description of the formats.
File suffixes are inaccurate as most file formats are available in several
revisions.

Similarly a single outputformat must be defined, again PRONOM URIs are
recommended, for the same reasons as above.

An sh command string is used (given in <commandline>, with the last <cmd>-set
containing the actual migration service call and it's parameters.

As an alternative to giving full path with parameters, it is possible to call
the migration service with a 'mode' - which defines a full set of parameters.