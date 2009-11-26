package eu.planets_project.fedora.connector;

import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.fedora.connector.planets.Datastream;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;

import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import fedora.client.FedoraClient;
import fedora.server.types.gen.MIMETypedStream;
import fedora.server.types.gen.ObjectProfile;
import fedora.server.types.gen.DatastreamControlGroup;

import javax.xml.rpc.ServiceException;

/**
 * The interface to the fedora repository system. This interface defines the few
 * methods that the planets connector must be able to use. All the more advanced
 * features can be build on top of this.
 */
public class FedoraConnector {


    private String username;

    private String server;

    private FedoraClient fedora;

    //TODO: This is the list of content models compatible with planets
    private List<String> PLANETS_MODELS;

    private String newObject = "" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<foxml:digitalObject xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\" VERSION=\"1.1\"\n" +
            "                     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                     xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml#\n" +
            "                                         http://www.fedora.info/definitions/1/0/foxml1-1.xsd\">\n" +
            "  <foxml:objectProperties>\n" +
            "    <foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/>\n" +
            "  </foxml:objectProperties>\n" +
            "</foxml:digitalObject>";

    public FedoraConnector(String username, String password, String server) throws FedoraConnectionException {
        this.username = username;
        this.server = server;
        PLANETS_MODELS = new ArrayList<String>();
        PLANETS_MODELS.add("info:fedora/demo:Planets_ContentModel");
        try {
            fedora = new FedoraClient(server, username, password);
        } catch (MalformedURLException e) {
            throw new FedoraConnectionException(e);
        }
    }

    public String newObject() throws FedoraConnectionException, StoreException {
        try {
            String pid = fedora.getAPIM().ingest(
                    newObject.getBytes("UTF-8"),
                    "info:fedora/fedora-system:FOXML-1.1", null);
            fedora.getAPIM().addRelationship(pid,"info:fedora/fedora-system:def/model#hasModel",PLANETS_MODELS.get(0),false,null);
            return pid;
        } catch (RemoteException e) {
            throw new StoreException(e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
    }

    public URI pid2Uri(String pid) throws ParseException {
        pid = "info:fedora/"+pid;
        try {
            return new URI(pid);
        } catch (URISyntaxException e) {
            throw new ParseException(e);
        }
    }
    public String uri2Pid(URI pdURI) {
        String pid = pdURI.toString();
        pid = pid.replace("info:fedora/","");
        return pid;
    }


    public void purgeObject(String pid) throws FedoraConnectionException, DigitalObjectManager.DigitalObjectNotFoundException {
        try {
            fedora.getAPIM().purgeObject(pid, null, false);
        } catch (RemoteException e) {
            throw new DigitalObjectManager.DigitalObjectNotFoundException(e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
    }


    public boolean exists(String pid) throws FedoraConnectionException {
        try {
            if (fedora.getAPIA().getObjectProfile(pid, null) == null) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            return false;
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
    }

    public boolean isPlanetsObject(String pid) throws FedoraConnectionException, DigitalObjectManager.DigitalObjectNotFoundException {
        try {
            ObjectProfile profile = fedora.getAPIA().getObjectProfile(pid, null);
            List<String> models = Arrays.asList(profile.getObjModels());
            return !Collections.disjoint(models, PLANETS_MODELS);
        } catch (RemoteException e) {
            throw new DigitalObjectManager.DigitalObjectNotFoundException(e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
    }

    public String getPlanetsContentModel(String pid) throws
            FedoraConnectionException,
            DigitalObjectManager.DigitalObjectNotFoundException {
        try {
            ObjectProfile profile = fedora.getAPIA().getObjectProfile(pid, null);
            List<String> models = Arrays.asList(profile.getObjModels());
            for (String model : models) {
                if (PLANETS_MODELS.contains(model)) {
                    return model;
                }
            }
            return null;
        } catch (RemoteException e) {
            throw new DigitalObjectManager.DigitalObjectNotFoundException(e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }

    }

    public boolean isWritable(String pid) throws FedoraConnectionException {
        String random = Math.random() + "";
        try {
            boolean result;
            result = fedora.getAPIM().addRelationship(pid, "info:fedora/testOfWritable", "isThisWritable?" + random, true, null);
            result = result && fedora.getAPIM().purgeRelationship(pid, "info:fedora/testOfWritable", "isThisWritable?" + random, true, null);
            return result;
        } catch (RemoteException e) {
            return false;
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
    }

    private String fedoraURI(String pid) throws ParseException {
        return pid2Uri(pid).toString();
    }

    public void modifyDatastream(String pid, String ds, String content,URI formatURI)
            throws FedoraConnectionException, ParseException, StoreException {
        try {
            fedora.server.types.gen.Datastream datastream;
            try {

                datastream = fedora.getAPIM().getDatastream(pid, ds, null);
            } catch (RemoteException e) {//presumably thrown if datastream not exists
                addManagedDatastream(pid, ds, content,formatURI);
                return;
            }
            String controlgroup = datastream.getControlGroup().getValue();
            try {
                if (controlgroup.equals("X")) {
                    modifyInlineDatastream(pid, ds, content,formatURI);
                } else if (controlgroup.equals("M")) {
                    modifyManagedDatastream(pid, ds, content,formatURI);
                } else {
                    throw new ParseException("Datastream is of incorrect type, cannot update");
                }
            } catch (RemoteException e) {//error on the servoer
                throw new StoreException(e);
            }
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
    }

    private String uploadFile(String content) throws IOException {
        File tempfile = null;
        try {
            tempfile = File.createTempFile("temp", null);
            Writer writer = new FileWriter(tempfile);
            writer.append(content);
            writer.close();
            return fedora.uploadFile(tempfile);

        } finally {
            if (tempfile != null) {
                tempfile.delete();
            }
        }
    }

    private void modifyManagedDatastream(String pid, String ds, String content, URI formatURI)
            throws IOException, ServiceException {
        String fileurl = uploadFile(content);
        fedora.getAPIM().modifyDatastreamByReference(
                pid,//pid
                ds,//dsid
                null,//altIDs
                null,//label
                "application/octet-stream",//mimetype
                formatURI.toString(),//formatURI
                fileurl,//content
                null,//checksumtype
                null,//checksum
                null,//logmessage
                true//force
        );

    }

    private void modifyInlineDatastream(String pid, String ds, String content, URI formatURI) throws IOException, ServiceException {
        fedora.getAPIM().modifyDatastreamByValue(
                pid,//pid
                ds,//dsid
                null,//altIDs
                null,//label
                "application/xml",//mimetype
                formatURI.toString(),//formatURI
                content.getBytes("UTF-8"),//content
                null,//checksumtype
                null,//checksum
                null,//logmessage
                true//force
        );
    }

    private void addManagedDatastream(String pid, String ds, String content, URI formatURI) throws IOException, ServiceException {
        String fileurl = uploadFile(content);
        fedora.getAPIM().addDatastream(
                pid,//pid
                ds,//dsid
                null,//altIDs
                "label",//label
                true,
                "application/octet-stream",//mimetype
                formatURI.toString(),//formatURI
                fileurl,//contentlocation
                "M",//controlgroup
                "A",//state
                null,//checksumtype
                null,//checksum
                null//logmessage
        );
    }

    public void modifyDatastream(String pid, String ds, InputStream content, URI formatURI)
            throws FedoraConnectionException, StoreException, ParseException {

        try {
            String fileURL = uploadFile(content);
            fedora.server.types.gen.Datastream datastream;
            try {

                datastream = fedora.getAPIM().getDatastream(pid, ds, null);
            } catch (RemoteException e) {//presumably thrown if datastream not exists
                fedora.getAPIM().addDatastream(
                        pid,//pid
                        ds,//dsid
                        null,//altIDs
                        null,//label
                        true,
                        "application/octet-stream",//mimetype
                        formatURI.toString(),//formatURI
                        fileURL,//contentlocation
                        "M",//controlgroup
                        "A",//state
                        null,//checksumtype
                        null,//checksum
                        null//logmessage
                );
                return;
            }
            String controlgroup = datastream.getControlGroup().getValue();
            try {
                if (controlgroup.equals("M")) {
                    fedora.getAPIM().modifyDatastreamByReference(
                            pid,//pid
                            ds,//dsid
                            null,//altIDs
                            null,//label
                            null,//mimetype
                            formatURI.toString(),//formatURI
                            fileURL,//content
                            null,//checksumtype
                            null,//checksum
                            null,//logmessage
                            true//force
                    );

                } else {
                    throw new ParseException("Datastream is of incorrect type, cannot update");
                }
            } catch (RemoteException e) {//error on the servoer
                throw new StoreException(e);
            }
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
    }

    private String uploadFile(InputStream content) throws IOException {
        File tempfile = null;
        try {
            tempfile = File.createTempFile("temp", null);
            FileOutputStream output = new FileOutputStream(tempfile);
            byte[] dump = new byte[1024];
            int length;
            while ((length = content.read(dump)) !=-1 ){
                output.write(dump,0,length);
            }
            output.close();
            return fedora.uploadFile(tempfile);

        } finally {
            if (tempfile != null) {
                tempfile.delete();
            }
        }
    }



    public Document getDatastreamXML(String pid, String datastream) throws FedoraConnectionException, DigitalObjectManager.DigitalObjectNotFoundException, ParseException {
        try {
            MIMETypedStream datastreamdiss = fedora.getAPIA().getDatastreamDissemination(pid, datastream, null);
            return DocumentUtils.bytesToDocument(datastreamdiss.getStream());
        } catch (RemoteException e) {
            throw new DigitalObjectManager.DigitalObjectNotFoundException(e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        } catch (SAXException e) {
            throw new ParseException(e);
        }

    }

    public boolean isDataObject(String pid) throws FedoraConnectionException {
        //TODO
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    public URI getDatastreamFormat(String pid, String name) throws FedoraConnectionException, DigitalObjectManager.DigitalObjectNotFoundException, ParseException {
        try {
            String formatURI = fedora.getAPIM().getDatastream(pid, name, null).getFormatURI();
            return new URI(formatURI);
        } catch (RemoteException e) {
            throw new DigitalObjectManager.DigitalObjectNotFoundException(e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        } catch (URISyntaxException e) {
            throw new ParseException(e);
        }
    }

    public URL getDatastreamURL(String pid, String name) throws FedoraConnectionException, ParseException {
        try {
            return new URL(server+"/objects/"+pid+"/datastreams/"+name+"/content");
        } catch (MalformedURLException e) {
            throw new ParseException(e);
        }
    }

    public String getDatastreamString(String pid, String name) throws FedoraConnectionException, DigitalObjectManager.DigitalObjectNotFoundException {
        try {
            MIMETypedStream datastreamdiss = fedora.getAPIA().getDatastreamDissemination(pid, name, null);
            return new String(datastreamdiss.getStream(), "UTF-8");
        } catch (RemoteException e) {
            throw new DigitalObjectManager.DigitalObjectNotFoundException(e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
    }

    public String getObjectLabel(String pid) throws FedoraConnectionException, DigitalObjectManager.DigitalObjectNotFoundException {
        ObjectProfile profile = null;
        try {
            profile = fedora.getAPIA().getObjectProfile(pid, null);

        } catch (RemoteException e){
            throw new DigitalObjectManager.DigitalObjectNotFoundException(e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
        return profile.getObjLabel();

    }

    public void setObjectLabel(String pid, String label) throws DigitalObjectManager.DigitalObjectNotFoundException, FedoraConnectionException {
        try {
            fedora.getAPIM().modifyObject(pid,null,label,null,null);
        } catch (RemoteException e){
            throw new DigitalObjectManager.DigitalObjectNotFoundException(e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(e);
        } catch (IOException e) {
            throw new FedoraConnectionException(e);
        }
    }
}
