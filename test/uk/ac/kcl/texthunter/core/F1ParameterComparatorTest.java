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
public class F1ParameterComparatorTest {
    
    public F1ParameterComparatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of compare method, of class F1ParameterComparator.
     */
    @Test
    public void testCompare() {
        System.out.println("compare");
        Parameter p1 = null;
        Parameter p2 = null;
        F1ParameterComparator instance = new F1ParameterComparator();
        int expResult = 0;
        int result = instance.compare(p1, p2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}