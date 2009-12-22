package eu.planets_project.services.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.modify.Modify;

/**
 * Tests for convenience methods in {@link ServiceUtils} for instantiating remote services.
 * <p/>
 * NOTE: These tests assume a running server, as they test remote service instantiation. They therefore are NOT part of
 * any test suite and do NOT run with the Ant tests.
 * <p/>
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class RemoteServiceCreationTests {

    private static final String ENDPOINT = "http://localhost:8080/pserv-pa-shotgun/ShotgunModify?wsdl";
    private static final String NAME = "Shotgun";
    private static final String INTERFACE_NAME = "eu.planets_project.services.modify.Modify";
    private static final String IMPLEMENTATION_NAME = "eu.planets_project.services.shotgun.ShotgunModify";
    private static final DigitalObject DIGITAL_OBJECT = new DigitalObject.Builder(Content
            .byValue(new File("build.xml"))).build();
    private static final URI FORMAT = FormatRegistryFactory.getFormatRegistry().createExtensionUri("xml");

    @Test
    public void createService() throws MalformedURLException, ClassNotFoundException {
        @SuppressWarnings("unchecked")/*
                                       * Some casting nastiness as we can't reference a service implementation here
                                       * (would fail compilation by Ant, as common does not depend on the services)
                                       */
        Modify modify = ServiceUtils.createService(Modify.QNAME, (Class<Modify>) Class.forName(IMPLEMENTATION_NAME),
                new URL(ENDPOINT));
        modify.modify(DIGITAL_OBJECT, FORMAT, null);
    }

    @Test
    public void createServiceFromDescription() throws MalformedURLException {
        ServiceDescription.Builder builder = new ServiceDescription.Builder(NAME, INTERFACE_NAME);
        builder.classname(IMPLEMENTATION_NAME).endpoint(new URL(ENDPOINT));
        ServiceDescription shotgun = builder.build();
        Modify modify = ServiceUtils.createService(shotgun);
        modify.modify(DIGITAL_OBJECT, FORMAT, null);
    }

    @Test(expected = IllegalArgumentException.class)
    /* We need endpoint, type and class name */
    public void invalidServiceCreation() {
        ServiceUtils.createService(new ServiceDescription.Builder(NAME, INTERFACE_NAME).build());
    }

}
