/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.services.utils.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Set;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;

/**
 * Enum containing files to test i.e. identification with. Each entry contains
 * the file location and the expected results. In the tests, we iterate over all
 * files, identify the file at the location and compare the received results
 * with the expected ones
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public enum TestFile {
    /*
     * CAUTION: adding an extension here without adding a corresponding file to
     * the test files folder of PSERV will cause all these tests to fail!
     */
    XML, PDF, GIF, JPG, PNG, HTML, TXT;
    /** We retrieve test files and correct PRONOM IDs automatically. */
    private final String location = FileAccess.INSTANCE.get(toString())
            .getAbsolutePath();
    private final Set<URI> expected = FormatRegistryFactory.getFormatRegistry()
            .getUrisForExtension(toString());

    /**
     * @return the location of the test file
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * @return the expected pronom IDs for the file
     */
    public Set<URI> getTypes() {
        return this.expected;
    }

    /**
     * @param f The test file enum element for the file type to test
     * @param identify The Identify implementation to use for testing
     *        identification of the given file type
     * @return True, if identification was successful
     */
    public static boolean testIdentification(final TestFile f,
            final Identify identify) {
        boolean match = false;
        System.out.println("Testing " + f);
        List<URI> identifyResult = identify.identify(
                new DigitalObject.Builder(Content.byReference(new File(f
                        .getLocation()))).build(), null).getTypes();
        if (identifyResult != null) {
            /*  */
            for (URI uri : identifyResult) {
                match = f.getTypes().contains(uri);
                if (match) {
                    break;
                }
            }
            String message = String
                    .format(
                            "Identification failed for %s, expected one of %s but was %s ",
                            f.getLocation(), f.getTypes(), identifyResult);
            if (!match) {
                System.err.println(message);
            }
        }
        return match;
    }

    /**
     * @param identify The identify implementation to test with all test files
     * @return True, if identification was successful
     */
    public static boolean testIdentification(final Identify identify) {
        for (TestFile file : TestFile.values()) {
            if (!testIdentification(file, identify)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param f The test file to validate
     * @param validate The Validate implementation instance to test
     * @return True, if validation was successful
     */
    public static boolean testValidation(final TestFile f,
            final Validate validate) {
        System.out.println("Testing validation of: " + f);
        /* For each we get the sample file: */
        String location = f.getLocation();
        /* And try validating it: */
        boolean match = false;
        try {
            DigitalObject digitalObject = new DigitalObject.Builder(Content
                    .byReference(new File(location).toURI().toURL())).build();
            /*
             * This approach to testing validation is very automated, therefore
             * not very specific: we check if any of the pronom IDs associated
             * with the test file matche the validation result:
             */
            for (URI uri : f.getTypes()) {
                ValidateResult vr = validate.validate(digitalObject, uri, null);
                match = vr.isValidInRegardToThisFormat() && vr.isOfThisFormat();
                if (match) {
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return match;
    }

    /**
     * @param validate The Validate implementation to test with all test files
     * @return True, if validation was successful
     */
    public static boolean testValidation(final Validate validate) {
        for (TestFile file : TestFile.values()) {
            if (!testValidation(file, validate)) {
                return false;
            }
        }
        return true;
    }

}