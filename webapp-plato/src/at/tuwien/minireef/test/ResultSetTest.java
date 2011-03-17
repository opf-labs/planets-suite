/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package at.tuwien.minireef.test;

import java.util.Arrays;
import java.util.List;

import org.jboss.remoting.samples.chat.exceptions.InvalidArgumentException;
import org.junit.Test;

import at.tuwien.minireef.ResultSet;


public class ResultSetTest {
    
    @Test
    public void testContent() throws InvalidArgumentException {
        ResultSet result = new ResultSet();
        result.setColumnNames(Arrays.asList("count", "a", "b"));
        result.addRow(Arrays.asList("1", "a1", "b1"));
        result.addRow(Arrays.asList("2", "a2", "b2"));
        result.addRow(Arrays.asList("3", "a3", "b3"));
        
        List<String> colResults = result.getColResults("count");
        assert (colResults.size() == 3);
        
        for (int i = 0; i < colResults.size(); i ++) {
            assert ((""+i).equals(colResults.get(i)));
        }
        colResults = result.getColResults("b");
        assert (colResults.size() == 3);
        
        for (int i = 0; i < colResults.size(); i ++) {
            assert (("b"+i).equals(colResults.get(i)));
        }
    }

}
