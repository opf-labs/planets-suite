package eu.planets_project.ifr.core.common.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * NativeRunner Tester.
 */
public class ProcessRunnerTest extends TestCase {
    public ProcessRunnerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSetParameters() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetProcessOutput() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetProcessError() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetProcessOutputAsString() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetProcessErrorAsString() throws Exception {
        //TODO: Test goes here...
    }

    public void testSimpleCall() throws Exception {
        ProcessRunner runner = new ProcessRunner("true");
        runner.run();
        assertEquals("The execution of true should work fine",
                     0, runner.getReturnCode());

        runner = new ProcessRunner("false");
        runner.run();
        assertEquals("The execution of false should give 1",
                     1, runner.getReturnCode());
    }

    public void testTimeout() throws Exception {

        ProcessRunner runner = new ProcessRunner(Arrays.asList("sleep","2"));

        runner.setTimeout(100);
        runner.run();
        if(!runner.isTimedOut()){
            fail("The execution of sleep should time out");
        }
    }

    public void testEnvironment() throws Exception {

        Map<String,String> env = new HashMap<String,String>();
        env.put("FLAM","flim");


        ProcessRunner runner =
                new ProcessRunner(Arrays.asList("/bin/sh","-c", "echo $FLAM"));
        runner.setEnviroment(env);
        //Nessesary for the command variable expansion to work correctly

        runner.run();
        assertEquals("The execution of echo should work fine",
                     0, runner.getReturnCode());
        assertEquals("The result of echo should be flim",
                     "flim\n", runner.getProcessOutputAsString());
    }

    public void testNullEnvironment () throws Exception {
        // Make sure we don't throw NPEs on null envs
        ProcessRunner runner = new ProcessRunner(Arrays.asList("/bin/echo",
                                                               "boo"));
    }


    public void testFeedProcess() throws Exception{
        Map<String,String> env = new HashMap<String,String>();
                env.put("FLAM","flim");

        File f = new File("testfile");
        f.createNewFile();
        f.deleteOnExit();
        InputStream in = new FileInputStream(f);
        Writer out = new FileWriter(f);
        out.write("echo $FLAM");
        out.flush();
        ProcessRunner runner =
                new ProcessRunner(Arrays.asList("/bin/sh"));
        runner.setEnviroment(env);
        runner.setInputStream(in);

        runner.run();

        //Nessesary for the command variable expansion to work correctly

        assertEquals("The execution of echo should work fine",
                     0, runner.getReturnCode());
        assertEquals("The result of echo should be flim",
                     "flim\n", runner.getProcessOutputAsString());

    }
    public static Test suite() {
        return new TestSuite(ProcessRunnerTest.class);
    }
}