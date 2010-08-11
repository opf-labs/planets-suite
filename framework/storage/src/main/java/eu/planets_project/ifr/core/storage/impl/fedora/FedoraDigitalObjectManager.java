package eu.planets_project.ifr.core.storage.impl.fedora;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.impl.fedora.connector.planets.ContentdatastreamType;
import eu.planets_project.ifr.core.storage.impl.fedora.connector.planets.MetadatastreamType;
import eu.planets_project.ifr.core.storage.impl.fedora.connector.planets.MetadatastreamsType;
import eu.planets_project.ifr.core.storage.impl.fedora.connector.planets.PlanetsDatastreamType;
import eu.planets_project.ifr.core.storage.impl.fedora.connector.FedoraConnectionException;
import eu.planets_project.ifr.core.storage.impl.fedora.connector.FedoraConnector;
import eu.planets_project.ifr.core.storage.impl.fedora.connector.ParseException;
import eu.planets_project.ifr.core.storage.impl.fedora.connector.StoreException;
import eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryString;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Metadata;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBElement;

/**
 * The Fedora Digital Object Manager. Implements the DigitalObjectManager from planets.
 * Must be initialized by the constructor, before use. Then use the methods
 * storeAsNew, updateExisting or retrieve.
 *
 * @see #storeAsNew(eu.planets_project.services.datatypes.DigitalObject)
 * @see #updateExisting(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
 * @see #retrieve(java.net.URI)  
 */
public class FedoraDigitalObjectManager extends DigitalObjectManagerBase implements DigitalObjectManager{

    private static Logger logger = Logger.getLogger(FedoraDigitalObjectManager.class.getName());

    FedoraConnector fedora;
    private boolean initialised = false;
    private String managerUri = null; 

    public static final String PLANETS_DATASTREAM = "PLANETS";
    
	public final static String USERNAME = "manager.username";
	public final static String PASSWORD = "manager.password";
	public final static String SERVER = "manager.server";
	public final static String MANAGERURI = "manager.identifier.uri";
    
    public FedoraDigitalObjectManager() throws FedoraConnectionException {
        super(null);
    }
    
    public FedoraDigitalObjectManager(String username, String password, String server) throws FedoraConnectionException {
        super(null);
    	fedora = new FedoraConnector(username,password,server);
    }

    /**
     * {@inheritDoc}
     * @param config 
     */
    public FedoraDigitalObjectManager(Configuration config) {
    	super(config);
    	try {
        	logger.info("FedoraDigitalObjectManager(config)");
        	String username = config.getString(USERNAME);
        	String password = config.getString(PASSWORD);
        	String server = config.getString(SERVER);
        	this.managerUri = config.getString(MANAGERURI);
        	fedora = new FedoraConnector(username,password,server);
        	logger.info("created FEDORA client with: "+username + " at " + server);
    	} catch (NoSuchElementException e) {
        	logger.info("FedoraDigitalObjectManager(config) error. Path property with key " + USERNAME + " and " + PASSWORD + " and " + SERVER + " not found in config");
    		throw new IllegalArgumentException("Path property with key " + USERNAME + " and " + PASSWORD + " and " + SERVER + " not found in config");
    	} catch (FedoraConnectionException e) {
			logger.log(Level.INFO, "error initializing fedora", e);
		}
    }

    
    /**
     * Stores the given object as a new object in Fedora. Will use the preferred contentmodel, ie. the one specified
     * in the PlanetsContentModelContentModel
     * @see #storeAsNew(eu.planets_project.services.datatypes.DigitalObject)
     * @param pdURI The suggested pid for the object. Ignored
     * @param digitalObject the digital object to store
     * @return the pid of the new object
     * @throws DigitalObjectNotStoredException if the object could not be stored
     */
    public URI storeAsNew( URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        return this.storeAsNew(digitalObject);
    }

    /**
     * Store the given object as a new object in Fedora.  Will use the preferred contentmodel, ie. the one specified
     * in the PlanetsContentModelContentModel
     * @param digitalObject The object to store
     * @return the pid to the new object
     * @throws DigitalObjectNotStoredException if the object could not be stored
     */
    public URI storeAsNew(DigitalObject digitalObject) throws DigitalObjectNotStoredException {

        try {
            String pid = null;
            try {
                pid = fedora.newObject();
            } catch (StoreException e) {
                throw new DigitalObjectNotStoredException(e);
            }

            URI pdUri = null;
            try {
                pdUri = fedora.pid2Uri(pid);
            } catch (ParseException e) {//Autogenerated pid not valid.....
                throw new DigitalObjectNotStoredException("Autogenerated pid not valid",e);
            }

            try{
                updateObject(pdUri,digitalObject);
            }
            catch (StoreException e){
                try {
                    fedora.purgeObject(pid);
                } catch (DigitalObjectNotFoundException e1) {
                    //Newly created object not found anyhow, not a problem
                }

                throw new DigitalObjectNotStoredException("Failed to store the object",e);
            }
            return pdUri;

        } catch (FedoraConnectionException fce){
            throw new DigitalObjectNotStoredException("Fedora connection problem",fce);
        }

    }

    /**
     * Update an already existing object with the information in the given object.
     * @param pdURI the pid of the object to update
     * @param digitalObject The digital object to update from
     * @return the pid of the new version of the object == pdURI
     * @throws DigitalObjectNotStoredException if the object could not be updated
     * @throws DigitalObjectNotFoundException if the pdURI Object could not be found
     */
    public URI updateExisting(URI pdURI, DigitalObject digitalObject)
            throws DigitalObjectNotStoredException,
            DigitalObjectNotFoundException {
        try {
            try {
                return updateObject(pdURI,digitalObject);
            } catch (StoreException e) {
                throw new DigitalObjectNotStoredException(e);
            }
        } catch (FedoraConnectionException fce){
            throw new DigitalObjectNotStoredException(fce);
        }

    }



    private URI updateObject(URI piduri, DigitalObject digitalObject)
            throws StoreException,
            FedoraConnectionException{

        String pid;

        pid = fedora.uri2Pid(piduri);
        boolean exists = fedora.exists(pid);
        if (!exists){
            throw new StoreException("Object does not already exist");
        }


        if (!isWritable(piduri)){
            throw new StoreException("This object is not writable");
        }

        String planetsModel;

        try {
            if (!fedora.isPlanetsObject(pid)){
                throw new StoreException("pdURI "+pid.toString()+" is not a planets compatible object");
            }
            planetsModel = fedora.getPlanetsContentModel(pid);
        } catch (DigitalObjectNotFoundException e) {
            //we have just checked that it exists...
            throw new StoreException("Object was there, but then it was gone",e);
        }

        PlanetsDatastreamType planetsDatastreamObject;
        try {
            planetsDatastreamObject = readPlanetsDatastream(planetsModel);
        } catch (ParseException e) {
            throw new StoreException(e);
        } catch (DigitalObjectNotFoundException e) {
            throw new StoreException("Object '"+pid+"' was a planets object but cannot read the content model",e);
        }
        //If so far, we have now read the datastream in as a java class



        //decode the streams for the planets object

        MetadatastreamsType metadatastreams = planetsDatastreamObject.getMetadatastreams();
        Map<String, MetadatastreamType> metamap = new HashMap<String, MetadatastreamType>();
        for (MetadatastreamType datastream: metadatastreams.getMetadatastream()){
            metamap.put(datastream.getPlanetsName(),datastream);
        }



        for (Metadata metadata : digitalObject.getMetadata()) {
            MetadatastreamType ds = metamap.get(metadata.getName());
            URI formatURI = metadata.getType();
            if (ds!=null){

                try {
                    fedora.modifyDatastream(pid,ds.getName(),metadata.getContent(),formatURI);
                } catch (ParseException e) {
                    throw new StoreException(e);
                }
            }
        }

        try {
            fedora.setObjectLabel(pid,digitalObject.getTitle());
        } catch (DigitalObjectNotFoundException e) {
            //we have just checked that it exists...
            throw new StoreException("Object was there, but then it was gone",e);
        }

        ContentdatastreamType filedatastream = planetsDatastreamObject.getContentdatastream();
        URI formatURI = digitalObject.getFormat();

        try {
            fedora.modifyDatastream(pid,filedatastream.getName(),digitalObject.getContent().getInputStream(),formatURI);
            return  fedora.pid2Uri(pid);
        } catch (ParseException e) {
            throw new StoreException(e);//cannot happen
        }

    }


    /**
     * Tests if the object is writable
     * @param pdURI the pid of the object
     * @return true if writable
     */
    public boolean isWritable(URI pdURI) {
        String pid = fedora.uri2Pid(pdURI);
        try {
            return fedora.isWritable(pid);
        } catch (FedoraConnectionException e) {
            return false;
        }
    }

    /**
     * List all objects beneath this. Not implemented, as there are no default object hierarchy in Fedora
     * @param pdURI the object
     * @return always null.
     */
    public List<URI> list(URI pdURI){

        try {
    	
        	logger.info("FEDORA list called w URI: "+pdURI);
        	URI baseUri = new PDURI(pdURI.normalize()).formDataRegistryRootURI();
        	logger.info("FedoraDigitalObjectManager list() base URI " + baseUri);	
        	
        	if (pdURI.equals(baseUri) == false){
        		logger.severe("FEDORA DOM list method does not support hierarchical browsing");
        		return null;
        	}        		        	
		
            List<String> objects = fedora.listPlanetsCompatibleObjects();
            List<URI> urilist = new ArrayList<URI>();
            for (String object : objects) {
            	System.out.println("FEDORA.list: pid="+object+" uri="+fedora.pid2Uri(object));
            	String[] parts = object.split("/");
            	URI oURI = URI.create(baseUri+"/"+parts[parts.length-1]).normalize();
            	urilist.add(oURI);
            	logger.info("FEDOARA list adding pdURI: "+oURI);
            }
            return urilist;
            
        } catch (FedoraConnectionException e) {
            throw new RuntimeException("Failed to connect to Fedora",e);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse list of planets objects",e);

        } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
        	throw new RuntimeException("",e);
		}

    }


    /**
     * Export the specified object as a Planets Object.
     * @param pdURI the pid of the object to export
     * @return the object as a planets object
     * @throws DigitalObjectNotFoundException if the object is not found in Fedora, or something else failed.
     */
    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException{

    	String leafName = null;
    	try {
			leafName = new PDURI(pdURI).getLeafname();
		} catch (URISyntaxException e1) {
			throw new RuntimeException("Failed to generate PDURI",e1);
		}
    	
		pdURI = URI.create(managerUri+"/"+leafName).normalize();
        String pid = fedora.uri2Pid(pdURI);
		logger.info("FEDORA.retrieve managerUri: "+managerUri+" pdURI: "+pdURI+" pid: "+pid);
        //find the object
        
        logger.info("FEDORA trying to retrieve object with pid: "+pid);

        try {
            boolean exists = fedora.exists(pid);
            if (!exists){
                throw new DigitalObjectNotFoundException("Object does not already exist");
            }
        } catch (FedoraConnectionException e) {
            throw new DigitalObjectNotFoundException(e);
        }

        String planetsmodel;
        try {
            if (!fedora.isDataObject(pid)) {
                throw new DigitalObjectNotFoundException("Object is not a data object");
            }
            //find the planets content model for the object
            if (!fedora.isPlanetsObject(pid)){
                throw new DigitalObjectNotFoundException("Object "+pdURI+" is not planets compatible");
            }

            planetsmodel = fedora.getPlanetsContentModel(pid);

        } catch (FedoraConnectionException e) {
            throw new DigitalObjectNotFoundException(e);
        }

        PlanetsDatastreamType planetsDatastreamObject;
        try {
            planetsDatastreamObject = readPlanetsDatastream(planetsmodel);
        } catch (ParseException e) {
            throw new DigitalObjectNotFoundException("Object "+pdURI+" is not planets compatible",e);
        } catch (FedoraConnectionException e) {
            throw new DigitalObjectNotFoundException("Object "+pdURI+" is not planets compatible",e);
        }
        //If so far, we have now read the datastream in as a java class



        //decode the streams for the planets object

        MetadatastreamsType metadatastreams = planetsDatastreamObject.getMetadatastreams();
        Map<String, MetadatastreamType> metamap = new HashMap<String, MetadatastreamType>();
        for (MetadatastreamType datastream: metadatastreams.getMetadatastream()){
            metamap.put(datastream.getPlanetsName(),datastream);
        }

        List<Metadata> metadata = new ArrayList<Metadata>();
        for (MetadatastreamType ds: metadatastreams.getMetadatastream()){
            String contents = null;
            URI formatURI = null;
            try {
                contents = fedora.getDatastreamString(pid,ds.getName());
                try {
                    formatURI = fedora.getDatastreamFormat(pid,ds.getName());
                } catch (ParseException e) {
                    //So, format URI is null
                }
            } catch (FedoraConnectionException e) {
                throw new DigitalObjectNotFoundException(e);
            }
            metadata.add(new Metadata(formatURI,ds.getPlanetsName(),contents));
        }


        ContentdatastreamType filedatastream = planetsDatastreamObject.getContentdatastream();
        URL contenturl = null;
        URI objectformat = null;
        try {
            contenturl = fedora.getDatastreamURL(pid,filedatastream.getName());
            try {
                objectformat = fedora.getDatastreamFormat(pid,filedatastream.getName());
            } catch (ParseException e) {
                //So, format URI is nulll
            }
        } catch (FedoraConnectionException e) {
            throw new DigitalObjectNotFoundException(e);
        } catch (ParseException e) {
            throw new DigitalObjectNotFoundException(e);
        }
        String title;
        try {

            title = fedora.getObjectLabel(pid);
            //logger.info("FEDORA: title:"+title+" pid:"+pid+" pdURI:"+pdURI);
        } catch (FedoraConnectionException e) {
            throw new DigitalObjectNotFoundException(e);
        }

        //grab the relevant streams, and create a planets object
        DigitalObject.Builder builder
                = new DigitalObject.Builder(
                Content.byReference(contenturl));


        builder.metadata(metadata.toArray(new Metadata[metadata.size()]));
        builder.format(objectformat);
        builder.permanentUri(pdURI);
        builder.title(title);
        
        logger.info("FEDORA retrieve returning: "+builder.build());

        return builder.build();

    }

    /**
     * Unimplemented. @see #list
     * @return
     */
    public List<Class<? extends Query>> getQueryTypes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List<URI> list(URI uri, Query query) throws QueryValidationException {
        return list(uri);
    }

    private PlanetsDatastreamType readPlanetsDatastream(String planetsmodel) throws ParseException, FedoraConnectionException, DigitalObjectNotFoundException {
        try {
            Document planetsstream = fedora.getDatastreamXML(planetsmodel,PLANETS_DATASTREAM);
            //JAXBContext jaxb = JAXBContext.newInstance("eu.planets_project.fedora.connector.planets");
            JAXBContext jaxb = JAXBContext.newInstance("eu.planets_project.ifr.core.storage.impl.fedora.connector.planets");            
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            //Read the planets content stream
            JAXBElement unmarshalled = (JAXBElement) unmarshaller.unmarshal(planetsstream);
            PlanetsDatastreamType planetsDatastreamObject = (PlanetsDatastreamType) unmarshalled.getValue();
            return planetsDatastreamObject;

        } catch (JAXBException e) {
            throw new ParseException(e);
        }
    }


}
