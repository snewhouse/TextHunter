/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.kcl.texthunter.core;

import gate.Corpus;
import gate.CorpusController;
import gate.FeatureMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
     * Test of getAllClassGapp method, of class MLModelMaker.
     */
    @Test
    public void testGetAllClassGapp() {
        System.out.println("getAllClassGapp");
        MLModelMaker instance = null;
        File expResult = null;
        File result = instance.getAllClassGapp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTrainingGapp method, of class MLModelMaker.
     */
    @Test
    public void testGetTrainingGapp() {
        System.out.println("getTrainingGapp");
        MLModelMaker instance = null;
        File expResult = null;
        File result = instance.getTrainingGapp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getApplicationGapp method, of class MLModelMaker.
     */
    @Test
    public void testGetApplicationGapp() {
        System.out.println("getApplicationGapp");
        MLModelMaker instance = null;
        File expResult = null;
        File result = instance.getApplicationGapp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTrainingApp method, of class MLModelMaker.
     */
    @Test
    public void testGetTrainingApp() {
        System.out.println("getTrainingApp");
        MLModelMaker instance = null;
        CorpusController expResult = null;
        CorpusController result = instance.getTrainingApp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getApplicationApp method, of class MLModelMaker.
     */
    @Test
    public void testGetApplicationApp() {
        System.out.println("getApplicationApp");
        MLModelMaker instance = null;
        CorpusController expResult = null;
        CorpusController result = instance.getApplicationApp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllClassApp method, of class MLModelMaker.
     */
    @Test
    public void testGetAllClassApp() {
        System.out.println("getAllClassApp");
        MLModelMaker instance = null;
        CorpusController expResult = null;
        CorpusController result = instance.getAllClassApp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParamsList method, of class MLModelMaker.
     */
    @Test
    public void testGetParamsList() {
        System.out.println("getParamsList");
        MLModelMaker instance = null;
        CopyOnWriteArrayList expResult = null;
        CopyOnWriteArrayList result = instance.getParamsList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
     * Test of writeBestModelProbs method, of class MLModelMaker.
     */
    @Test
    public void testWriteBestModelProbs() throws Exception {
        System.out.println("writeBestModelProbs");
        ArrayList<Prob> probValues = new ArrayList();
        File file = new File("c:\\work");
        Map<Long, Double> map = new TreeMap();
        map.put(new Long(1), 0.1);
        map.put(new Long(10), 0.101);
        map.put(new Long(112), 0.112);
        map.put(new Long(13), 0.13);                
        Prob b1 = new Prob(1,map);        
        probValues.add(b1);
        Map<Long, Double> map2 = new TreeMap();
        map2.put(new Long(10), 0.1);
        map2.put(new Long(1), 0.101);
        map2.put(new Long(13), 0.11221313123);
        map2.put(new Long(112), 0.134234242);                
        Prob b2 = new Prob(2,map2);        
        probValues.add(b2);
                
        
        MLModelMaker instance = new MLModelMaker();
        instance.writeBestModelProbs(probValues, file);
        // TODO review the generated test code and remove the default call to fail.
  
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