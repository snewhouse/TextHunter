/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TextHunter;

import TextHunter.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author rjackson1
 */
public class UtilsTest {
            Connection con = null;
    public UtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {              
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of checkForTable method, of class Utils.
     */
    @Before
    public void openConnection(){
//        try{
//            con = DriverManager.getConnection("jdbc:sqlserver://BRHNSQL094;databaseName=GateDB_Cris;" + "integratedSecurity=true");
//
//            if (con.isValid(10)) {
//                System.out.println("Connection OK!");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
//        }       
        con = Utils.loadAndConnectDerby(con);
    }
    
    @After
    public void closeConnection(){
//        try{
//            con.close();
//
//        } catch (SQLException ex) {
//            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
//        }        
        Utils.shutdownDerby();
        try {
            assertTrue(con.isClosed());
        } catch (SQLException ex) {
            Logger.getLogger(UtilsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testCheckForTable() {
        System.out.println("checkForTable"); 
        String projectName = "testTable";
        Utils.checkForTable(con, projectName);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
    @Test
    public void testCreateTable() {
        System.out.println("testCreateTable"); 
        String projectName = "testTable";
        if(!Utils.checkForTable(con, projectName)){
            Utils.createAnnotationsTable(con, projectName);            
        }
        
        assertTrue(Utils.checkForTable(con, projectName));

        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }    
    @Test
    public void testDropTable() {
        System.out.println("testDropTable"); 
        String projectName = "testTable";
        if(!Utils.checkForTable(con, projectName)){
            Utils.createAnnotationsTable(con, projectName);   
            Utils.dropTable(con, projectName);
        }else{
            Utils.dropTable(con, projectName);            
        };
        
        assertFalse(Utils.checkForTable(con, projectName));

        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }  
}