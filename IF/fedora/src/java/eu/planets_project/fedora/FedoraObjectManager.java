package eu.planets_project.fedora;

import dk.statsbiblioteket.doms.ContentModel;
import dk.statsbiblioteket.doms.DataObject;
import dk.statsbiblioteket.doms.Fedora;
import dk.statsbiblioteket.doms.FedoraFactory;
import dk.statsbiblioteket.doms.FedoraUserToken;
import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.ObjectNotFoundException;
import eu.planets_project.fedora.queries.TripleStoreQuery;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryString;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Metadata;

import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO abr forgot to document this class
 */
public class FedoraObjectManager implements DigitalObjectManager{


    public static final String transformMethod = "transform";


    Fedora fedora;
    private static final String PLANETSCONTENTMODEL = "doms:PlanetsContentModelContentModel";
    private  ContentModel planetsmodel;
    private boolean initialised = false;

    public FedoraObjectManager(String username, String password, String server) {
        FedoraUserToken token = new FedoraUserToken(username,password,server);

        FedoraFactory factory = new FedoraFactory();

        fedora = factory.getInstance(token);
        try {

            planetsmodel = fedora.getObject(PLANETSCONTENTMODEL);
            initialised = true;
        } catch (FedoraConnectionException e) {
            initialised = false;
        } catch (ObjectNotFoundException e) {
            initialised = false;
        }

    }


    public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        throw new DigitalObjectNotStoredException("The current DigitalObjectManager interface is not compatible with store routines in Fedora");
/*

        try {
            dk.statsbiblioteket.doms.DigitalObject object = fedora.getObject(pdURI.toString());


        } catch (FedoraConnectionException e) {
            throw new DigitalObjectNotStoredException(e);
        } catch (ObjectNotFoundException e) {
            throw new DigitalObjectNotStoredException(e);
        }

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

*/
    }

    public boolean isWritable(URI pdURI) {
        try {
            dk.statsbiblioteket.doms.DigitalObject object = fedora.getObject(pdURI.toString());
            return object.isWritable();
        } catch (FedoraConnectionException e) {
            return false;
        } catch (ObjectNotFoundException e) {
            return false;
        }


    }

    public List<URI> list(URI pdURI) {
        //TODO implement

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    /**
     * Retrieves a digital object from a fedora repository.
     * <br>
     * The fedora repository is queried through it's REST interface.
     * <br>
     *
     * This method draws upon a custom Fedora disseminator, that
     * transform the object to planets xml format.
     * <br>
     * <ul>
     * <li>The object title is set to the fedora object title
     * <li>The DC datastream is stored as a Metadata object
     * <li>The Content datastream is stored as a reference in the content object
     * <li>The permanentURL is set the the url of the object in the repository
     *</ul>

     *
     * @param pdURI
     *            URI with the fedora pid of the object
     * @return a new immutable digital object with the information from the repository
     * @throws DigitalObjectNotFoundException If the object is not found in the repository
     * @throws RepositoryException If there is a problem communication with the repository
     */
    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException, RepositoryException {
        if (!initialised){
            throw new RepositoryException("Connector not initialised");
        }

        try {


            //find the object
            dk.statsbiblioteket.doms.DigitalObject object = fedora.getObject(pdURI.toString());

            if (object instanceof DataObject) {
                DataObject o = (DataObject) object;
                //find the planets content model for the object
                List<ContentModel> contentmodels;
                contentmodels = o.getContentModels();

                ContentModel planetsmodel = null;
                for (ContentModel contentmodel:contentmodels){
                    if (isPlanetsModel(contentmodel)){
                        planetsmodel = contentmodel;
                        break;
                    }
                }
                if (planetsmodel == null){
                    throw new RepositoryException("Object is not a planets object");
                }
                //decode the streams for the planets object
                URL contenturl = getContentStream(planetsmodel,o);
                String title = getTitle(planetsmodel,o);
                Metadata[] metadata = getMetadata(planetsmodel,o);
                URI permanent = getPermanentURI(planetsmodel,o);

                //grab the relevant streams, and create a planets object
                DigitalObject.Builder builder
                        = new DigitalObject.Builder(
                        Content.byReference(contenturl));
                builder.title(title);
                builder.metadata(metadata);
                builder.permanentUri(permanent);

                //return this
                return builder.build();
            } else {
                throw new DigitalObjectNotFoundException("Object is not a data object");
            }

        } catch (FedoraConnectionException e) {
            throw new RepositoryException(e);
        } catch (ObjectNotFoundException e) {
            throw new DigitalObjectNotFoundException("The object was not found",e);
        }
    }

    private URI getPermanentURI(ContentModel planetsmodel, DataObject o) {
        try {
            if (!o.getPid().startsWith("info:fedora/")){
                return new URI("info:fedora/"+o.getPid());
            } else {
                return new URI(o.getPid());
            }
        } catch (URISyntaxException e) {
            throw new RepositoryException("Dataobject has invalid pid",e);
        }
    }

    private Metadata[] getMetadata(ContentModel planetsmodel, DataObject o) {
        return new Metadata[0];  //To change body of created methods use File | Settings | File Templates.
    }

    private String getTitle(ContentModel planetsmodel, DataObject o) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private URL getContentStream(ContentModel planetsmodel, DataObject o) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private boolean isPlanetsModel(ContentModel contentmodel) {
        return contentmodel.hasContentModel(planetsmodel);
    }


    public List<Class<? extends Query>> getQueryTypes() {

        //TODO implement
        ArrayList<Class<? extends Query>> list = new ArrayList<Class<? extends Query>>();
        list.add(TripleStoreQuery.class);
        list.add(QueryString.class);
        return list;


    }

    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {

        //TODO implement
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
