/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.kcl.texthunter.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rjackson1
 */
public class IDParameterComparatorTest {
    
    public IDParameterComparatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of compare method, of class IDParameterComparator.
     */
    @Test
    public void testCompare() {
        System.out.println("compare");
        Parameter p1 = null;
        Parameter p2 = null;
        IDParameterComparator instance = new IDParameterComparator();
        int expResult = 0;
        int result = instance.compare(p1, p2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}