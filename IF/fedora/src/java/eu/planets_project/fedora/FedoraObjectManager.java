package eu.planets_project.fedora;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryString;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.fedora.planetsdatastream.PlanetsdatastreamType;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBElement;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * TODO abr forgot to document this class
 */
public class FedoraObjectManager implements DigitalObjectManager{



    private FedoraConnector connector;

    public FedoraObjectManager(String username, String password, String server) {
        connector = new FedoraConnector(username,password,server);
    }


    public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        if (!isWritable(pdURI)){
            throw new DigitalObjectNotStoredException("This object is not writable");
        }
        Set<URI> cms = connector.getContentModels(pdURI);
        //Find the planets content model
        URI planetsModel = null;
        for (URI cm:cms){
            if (connector.isPlanetsModel(cm)){
                planetsModel = cm;
                break;
            }
        }

        if (planetsModel == null){
            throw new DigitalObjectNotStoredException("Target URI is not a planets object");
        }
        //Read the planets content stream
        Document planetsstream = connector.getDatastream(planetsModel,"PLANETS");
        try {
            JAXBContext jaxb = JAXBContext.newInstance("eu.planets_project.fedora.planetsdatastream");
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            PlanetsdatastreamType planetsDatastreamObject = (PlanetsdatastreamType)unmarshaller.unmarshal(planetsstream);
            //If so far, we have now read the datastream in as a java class
            String metadatastream = planetsDatastreamObject.getMetadatastream().getName();
            //TODO do not forget to store the metadata

            //decode the streams for the planets object



            //Grab the object, update the relevant content streams

            //return



            String filedatastream = planetsDatastreamObject.getFiledatastream().getName();
            boolean result = connector.modifyDatastream(pdURI,filedatastream,digitalObject.getContent().read());
            if (!result){
                throw new DigitalObjectNotStoredException("Failed to upload content to fedora");
            }


        } catch (JAXBException e) {
            throw new DigitalObjectNotStoredException("Failed to read in the " +
                    "planets datastream, so the store procedure could not be " +
                    "completed",e);
        }

    }

    public boolean isWritable(URI pdURI) {
        return connector.isWritable(pdURI);
    }

    public List<URI> list(URI pdURI) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
        if (!connector.exist(pdURI)){
            throw new DigitalObjectNotFoundException("Object not found in the repository");
        }
        //find the object

        //find the planets content model for the object

        //decode the streams for the planets object

        //grab the relevant streams, and create a planets object

        //return this
        return null;
    }

    public List<Class<? extends Query>> getQueryTypes() {
        ArrayList<Class<? extends Query>> list = new ArrayList<Class<? extends Query>>();
        list.add(TripleStoreQuery.class);
        list.add(QueryString.class);
        return list;


    }

    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        if (q instanceof TripleStoreQuery){
            return null;
        }
        else if (q instanceof QueryString){
            return null;

        }
        else {
            throw new QueryValidationException("Unknown type of query");
        }
    }

}
