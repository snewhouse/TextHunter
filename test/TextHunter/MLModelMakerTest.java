/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TextHunter;

import gate.Corpus;
import gate.FeatureMap;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rjackson1
 */
public class MLModelMakerTest {
    
    public MLModelMakerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of loadApps method, of class MLModelMaker.
     */
    @Test
    public void testLoadApps() {
        System.out.println("loadApps");
        MLModelMaker instance = null;
        instance.loadApps();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setResume method, of class MLModelMaker.
     */
    @Test
    public void testSetResume() {
        System.out.println("setResume");
        boolean choice = false;
        MLModelMaker instance = null;
        instance.setResume(choice);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of executeApplyAll method, of class MLModelMaker.
     */
    @Test
    public void testExecuteApplyAll() {
        System.out.println("executeApplyAll");
        MLModelMaker instance = null;
        FeatureMap expResult = null;
        FeatureMap result = instance.executeApplyAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of executeTrainAll method, of class MLModelMaker.
     */
    @Test
    public void testExecuteTrainAll() {
        System.out.println("executeTrainAll");
        MLModelMaker instance = null;
        instance.executeTrainAll();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of executeXVal method, of class MLModelMaker.
     */
    @Test
    public void testExecuteXVal() throws Exception {
        System.out.println("executeXVal");
        MLModelMaker instance = null;
        FeatureMap expResult = null;
        FeatureMap result = instance.executeXVal();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProgressThroughParamList method, of class MLModelMaker.
     */
    @Test
    public void testGetProgressThroughParamList() {
        System.out.println("getProgressThroughParamList");
        MLModelMaker instance = null;
        Integer expResult = null;
        Integer result = instance.getProgressThroughParamList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of outerValidation method, of class MLModelMaker.
     */
    @Test
    public void testOuterValidation() {
        System.out.println("outerValidation");
        MLModelMaker instance = null;
        Parameter expResult = null;
        Parameter result = instance.outerValidation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cleanUp method, of class MLModelMaker.
     */
    @Test
    public void testCleanUp() {
        System.out.println("cleanUp");
        MLModelMaker instance = null;
        instance.cleanUp();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDefaultParameters method, of class MLModelMaker.
     */
    @Test
    public void testSetDefaultParameters() {
        System.out.println("setDefaultParameters");
        Parameter parameters = null;
        MLModelMaker instance = null;
        instance.setDefaultParameters(parameters);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setUpCorpora method, of class MLModelMaker.
     */
    @Test
    public void testSetUpCorpora() {
        System.out.println("setUpCorpora");
        Corpus corpus = null;
        MLModelMaker instance = null;
        ArrayList expResult = null;
        ArrayList result = instance.setUpCorpora(corpus);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resumeXValidation method, of class MLModelMaker.
     */
    @Test
    public void testResumeXValidation() {
        System.out.println("resumeXValidation");
        MLModelMaker instance = null;
        instance.resumeXValidation();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}