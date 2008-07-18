1.) The metadata-extractor loads a configuration file using the class loader, so to run the test locally, 
you should add the resources folder to the classpath in eclipse (if it is not already configured that way)

2.) This service uses a modified version of the metadata-extractor, so the metadata-planets.jar cannot 
simply be replaced by a downloaded version of metadata.jar (which uses the system class loader which will
not work inside JBoss)

3.) A dependency on a recent version of PDFBox made it necessary to replace the old version of PDFBox found
inside the jackrabbit-jca.rar and the storage-webdav.war files in the IF with the version that comes with the 
metadata extractor, so these should not be replaced with the ones that come with JBoss to keep this service 
working