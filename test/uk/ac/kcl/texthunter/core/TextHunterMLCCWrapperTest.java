/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.kcl.texthunter.core;

import gate.Controller;
import gate.CorpusController;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rjackson1
 */
public class TextHunterMLCCWrapperTest {
    TextHunterMLCCWrapper wrapper;

    public TextHunterMLCCWrapperTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {

        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of changeFeatureSelection method, of class TextHunterMLCCWrapper.
     */
    @Test
    public void testChangeFeatureSelection() {
//        System.out.println("changeFeatureSelection");
//        try {
//            Gate.init();
//            Controller theApp = (CorpusController)PersistenceManager.loadObjectFromFile( new File(
//                    
//                    "C:\\Users\\rjackson1\\Documents\\NetBeansProjects\\TextHunter"
//                    + "\\projects\\GateDB_Cris.dbo.gate_hunter_test"
//                    + "\\generic_app\\all_classes.gapp"));  
//            
//            CopyOnWriteArrayList<Parameter> params = Parameter.generateParamList(true, 10);
//            Parameter param = params.get(58);
//            TextHunterMLCCWrapper instance = new TextHunterMLCCWrapper(theApp);
//            System.out.println("Adjusting model parameters..." +"C__" +param.getC()+"T__" + param.getT()+"D__" + param.getD()+"Tau__" + param.getTau() + 
//                    "__negex " + param.isRUN_NEGEX() + "__rmStops " + param.isRemoveStopWords());            
//            instance.changeFeatureSelection(param);
//            String saveloc = "C:\\Users\\rjackson1\\Documents\\"
//                    + "NetBeansProjects\\TextHunter\\projects\\"
//                    + "GateDB_Cris.dbo.gate_hunter_test\\generic_app\\test1.gapp";
//            PersistenceManager.saveObjectToFile(instance.getApp(), new File (saveloc),true,false);
//            
//        // TODO review the generated test code and remove the default call to fail.
//
//            
//        } catch ( PersistenceException | IOException | ResourceInstantiationException ex) {
//            Logger.getLogger(TextHunterMLCCWrapperTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (GateException ex) {
//            Logger.getLogger(TextHunterMLCCWrapperTest.class.getName()).log(Level.SEVERE, null, ex);
//        }        
        
        

    }

//    /**
//     * Test of getApp method, of class TextHunterMLCCWrapper.
//     */
//    @Test
//    public void testGetApp() {
//        System.out.println("getApp");
//        TextHunterMLCCWrapper instance = null;
//        CorpusController expResult = null;
//        CorpusController result = instance.getApp();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setApp method, of class TextHunterMLCCWrapper.
//     */
//    @Test
//    public void testSetApp() {
//        System.out.println("setApp");
//        CorpusController app = null;
//        TextHunterMLCCWrapper instance = null;
//        instance.setApp(app);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reinitialisePRs method, of class TextHunterMLCCWrapper.
//     */
//    @Test
//    public void testReinitialisePRs() throws Exception {
//        System.out.println("reinitialisePRs");
//        TextHunterMLCCWrapper instance = null;
//        instance.reinitialisePRs();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setTrainingMode method, of class TextHunterMLCCWrapper.
//     */
    @Test
    public void testSetTrainingMode() throws Exception {
        System.out.println("changeFeatureSelection");
        try {
            Gate.init();
            Controller theApp = (CorpusController)PersistenceManager.loadObjectFromFile( new File(
                    
                    "C:\\Users\\rjackson1\\Documents\\NetBeansProjects\\TextHunter"
                    + "\\projects\\GateDB_Cris.dbo.gate_hunter_test"
                    + "\\generic_app\\all_classes.gapp"));  
            
            TextHunterMLCCWrapper instance = new TextHunterMLCCWrapper(theApp);        
            instance.setTrainingMode("TRAINING");
            String saveloc = "C:\\Users\\rjackson1\\Documents\\"
                    + "NetBeansProjects\\TextHunter\\projects\\"
                    + "GateDB_Cris.dbo.gate_hunter_test\\generic_app\\train_junit.gapp";
            PersistenceManager.saveObjectToFile(instance.getApp(), new File (saveloc),true,false);
            instance.setTrainingMode("APPLICATION_ALL_CLASSES");
            saveloc = "C:\\Users\\rjackson1\\Documents\\"
                    + "NetBeansProjects\\TextHunter\\projects\\"
                    + "GateDB_Cris.dbo.gate_hunter_test\\generic_app\\test_ac_junit.gapp";
            PersistenceManager.saveObjectToFile(instance.getApp(), new File (saveloc),true,false);
            instance.setTrainingMode("APPLICATION_POS_ONLY");
            saveloc = "C:\\Users\\rjackson1\\Documents\\"
                    + "NetBeansProjects\\TextHunter\\projects\\"
                    + "GateDB_Cris.dbo.gate_hunter_test\\generic_app\\test_pos_junit.gapp";
            PersistenceManager.saveObjectToFile(instance.getApp(), new File (saveloc),true,false);            
        // TODO review the generated test code and remove the default call to fail.

            
        } catch ( PersistenceException | IOException | ResourceInstantiationException ex) {
            Logger.getLogger(TextHunterMLCCWrapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GateException ex) {
            Logger.getLogger(TextHunterMLCCWrapperTest.class.getName()).log(Level.SEVERE, null, ex);
        }          
        

    }
}