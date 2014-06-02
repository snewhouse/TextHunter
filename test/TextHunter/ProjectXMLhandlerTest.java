/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TextHunter;

import TextHunter.Parameter;
import TextHunter.ProjectXMLhandler;
import gate.CorpusController;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import java.io.File;
import java.io.IOException;
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
public class ProjectXMLhandlerTest {
    
    public ProjectXMLhandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }


    /**
     * Test of writeBestParams method, of class ProjectXMLhandler.
     */
    @Test
    public void testWriteBestParams() throws Exception {
        System.out.println("writeBestParams");
        Parameter param = new Parameter( "clean",  1,  0.5, 
             2,  1,   1.0,  true,  true, 
            0.97,  0.84,  0.89,  10);
        ProjectXMLhandler instance = new ProjectXMLhandler("C:\\work\\GateDB_Cris.dbo.gate_hunter_stroke_test2.XML");        
        instance.writeBestParams(param);
        // TODO review the generated test code and remove the default call to fail.
        instance.saveDoc();
    }

    /**
     * Test of updateWords method, of class ProjectXMLhandler.
     */
    @Test
    public void testUpdateWords() throws IOException {
        System.out.println("updateWords");
        String[] words = {"hello" , "from", "a", "test\n"};
        boolean keyOrOther = false;
        ProjectXMLhandler instance = new ProjectXMLhandler("C:\\work\\GateDB_Cris.dbo.gate_hunter_stroke_test2.XML");   
        instance.updateWords(words, keyOrOther);
        instance.saveDoc();
        // TODO review the generated test code and remove the default call to fail.

    }

   @Test
    public void testGetBestParameter() throws IOException {
        System.out.println("getBestParameter");
        ProjectXMLhandler instance = new ProjectXMLhandler("C:\\work\\GateDB_Cris.dbo.gate_hunter_stroke_test2.XML");   
        Parameter parameter = instance.getBestParameter();
        assertEquals(Double.valueOf("0.89"), parameter.F1);
        // TODO review the generated test code and remove the default call to fail.

    }
   
//    @Test
//    public void testWhatthisdoes() {
//        try {
//            Gate.init();
//            CorpusController  trainingApp =
//                (CorpusController)PersistenceManager.loadObjectFromFile
//                    (new File("C:\\Users\\rjackson1\\Documents\\NetBeansProjects\\AnnotationPipelineV2\\GateDB_Cris.dbo.gate_hunter_stroke_test\\generic_app\\testv3.gapp"));
//            
//            PersistenceManager.saveObjectToFile(trainingApp, new File("C:\\work\\wtf.xgapp"));
//         // TODO review the generated test code and remove the default call to fail.
//        } catch (PersistenceException ex) {
//            Logger.getLogger(ProjectXMLhandlerTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(ProjectXMLhandlerTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ResourceInstantiationException ex) {
//            Logger.getLogger(ProjectXMLhandlerTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (GateException ex) {
//            Logger.getLogger(ProjectXMLhandlerTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
    /**
     * Test of saveDoc method, of class ProjectXMLhandler.
     */
//    @Test
//    public void testSaveDoc() throws IOException {
//        System.out.println("saveDoc");
//        ProjectXMLhandler instance = new ProjectXMLhandler("C:\\work\\GateDB_Cris.dbo.gate_hunter_test");
//        instance.saveDoc();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getWords method, of class ProjectXMLhandler.
//     */
//    @Test
//    public void testGetWords() throws IOException {
//        System.out.println("getWords");
//        boolean keyOrOther = false;
//        ProjectXMLhandler instance = new ProjectXMLhandler("C:\\work\\GateDB_Cris.dbo.gate_hunter_test");
//        String expResult = "";
//        String result = instance.getWords(keyOrOther);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getsqlConn method, of class ProjectXMLhandler.
     */
    @Test
    public void testGetsqlConn() throws IOException {
        System.out.println("getsqlConn");
        ProjectXMLhandler instance = new ProjectXMLhandler("C:\\work\\GateDB_Cris.dbo.gate_hunter_stroke_test2.XML");
        String expResult = "";
        String result = instance.getsqlConn();
        assertEquals("jdbc:sqlserver://BRHNSQL094;databaseName=GateDB_Cris;", result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
//
//    /**
//     * Test of getGateHome method, of class ProjectXMLhandler.
//     */
//    @Test
//    public void testGetGateHome() {
//        System.out.println("getGateHome");
//        ProjectXMLhandler instance = new ProjectXMLhandler();
//        String expResult = "";
//        String result = instance.getGateHome();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of updateMetadata method, of class ProjectXMLhandler.
//     */
//    @Test
//    public void testUpdateMetadata() {
//        System.out.println("updateMetadata");
//        String gateHome = "";
//        String sqlConn = "";
//        ProjectXMLhandler instance = new ProjectXMLhandler();
//        instance.updateMetadata(gateHome, sqlConn);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}