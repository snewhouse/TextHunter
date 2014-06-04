//    Text Hunter: User friendly concept extraction from text
//
//    Copyright (C) 2014  Richard Jackson (richgjackson@yahoo.co.uk)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.


package uk.ac.kcl.texthunter.utils;

import gate.AnnotationSet;
import gate.Factory;
import gate.Gate;
import gate.Resource;
import gate.util.GateException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import uk.ac.kcl.texthunter.core.GlobalParameters;
import uk.ac.kcl.texthunter.core.MLModelMaker;

/**
 *
 * @author rjackson1
 */
public class Utils {
   
    public static void deleteGateResources(Object lock){
            synchronized (lock) {
                for (Resource res : Gate.getCreoleRegister().getPublicPrInstances()) {
                    System.out.println("Attempting to delete GATE resources" );
                    try {
                        Factory.deleteResource(res);
                    } catch (java.lang.NullPointerException | java.lang.IndexOutOfBoundsException ex) {
                    }
                    System.out.println("GATE resources deleted" );                    
                }

                for (Resource res : Gate.getCreoleRegister().getPublicLrInstances()) {
                    try {
                        System.out.println("Attempting to delete: " + res.getName() + " " + res.getClass());
                        Factory.deleteResource(res);
                    } catch (java.lang.NullPointerException | java.lang.IndexOutOfBoundsException ex) {
                    }
                }
            }        
    }
    
    public static boolean checkForTable(Connection con, String projectName){
        DatabaseMetaData meta;
        try {
            meta = con.getMetaData();
            ResultSet res = meta.getTables(null, null, null, 
               new String[] {"TABLE"});
            while (res.next()) {
               System.out.println(
                  "   "+res.getString("TABLE_CAT") 
                 + ", "+res.getString("TABLE_SCHEM")
                 + ", "+res.getString("TABLE_NAME")
                 + ", "+res.getString("TABLE_TYPE")
                 + ", "+res.getString("REMARKS"));               
               if(res.getString("TABLE_NAME").equalsIgnoreCase(projectName)){
                         return true;               
               }                     
            }

        } catch (SQLException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);       
        }      
            return false;     
    }
    
    public static Connection loadAndConnectDerby(Connection con) {
        /*
         *  The JDBC driver is loaded by loading its class.
         *  If you are using JDBC 4.0 (Java SE 6) or newer, JDBC drivers may
         *  be automatically loaded, making this code optional.
         *
         *  In an embedded environment, this will also start up the Derby
         *  engine (though not any databases), since it is not already
         *  running. In a client environment, the Derby engine is being run
         *  by the network server framework.
         *
         *  In an embedded environment, any static Derby system properties
         *  must be set before loading the driver to take effect.
         */

        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        String protocol = "jdbc:derby:";
        try {
            Class.forName(driver).newInstance();
        //    System.out.println("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver " + driver);
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
        } catch (InstantiationException ie) {
            System.err.println(
                    "\nUnable to instantiate the JDBC driver " + driver);
            ie.printStackTrace(System.err);
        } catch (IllegalAccessException iae) {
            System.err.println(
                    "\nNot allowed to access the JDBC driver " + driver);
            iae.printStackTrace(System.err);
        }

        /* By default, the schema APP will be used when no username is
         * provided.
         * Otherwise, the schema name is the same as the user name (in this
         * case "user1" or USER1.)
         *
         * Note that user authentication is off by default, meaning that any
         * user can connect to your database using any password. To enable
         * authentication, see the Derby Developer's Guide.
         */
        String dbName = "annotationDB"; // the name of the database
        try {
            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
            con = DriverManager.getConnection(protocol + dbName
                    + ";create=true");          
        } catch (SQLException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return con;

    }    
    public static void shutdownDerby() {
        try {
            // the shutdown=true attribute shuts down Derby
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
            // To shut down a specific database only, but keep the
            // engine running (for example for connecting to other
            // databases), specify a database in the connection URL:
            //DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
        } catch (SQLException se) {
            if (((se.getErrorCode() == 50000)
                    && ("XJ015".equals(se.getSQLState())))) {
                // we got the expected exception
                System.out.println("Derby shut down normally");
                // Note that for single database shutdown, the expected
                // SQL state is "08006", and the error code is 45000.
            } else {
                // if the error code or SQLState is different, we have
                // an unexpected exception (shutdown failed)
                System.err.println("Derby did not shut down normally");
                printSQLException(se);
            }
        }


    }    
        public static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }

    public static void copyFolder(File src, File dest)
            throws IOException {

        if (src.isDirectory()) {

            //if directory not exists, create it
            if (!dest.exists()) {
                dest.mkdir();
//                System.out.println("Directory copied from "
//                        + src + "  to " + dest);
            }

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile, destFile);
            }

        } else {
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes 
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
//            System.out.println("File copied from " + src + " to " + dest);
        }
    }    
    
    public static void dropTable(Connection con, String tableName){
        Statement stmt = null;
        try{
            stmt = con.createStatement();
            String sql = "DROP TABLE "+tableName;
            stmt.executeUpdate(sql);
        }catch(SQLException ex){
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);            
        }            
    }
    
    public static void dropIndex(Connection con, String indexName){
        Statement stmt = null;
        try{
            stmt = con.createStatement();
            String sql = "DROP INDEX "+indexName;
            stmt.executeUpdate(sql);
        }catch(SQLException ex){
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);            
        }            
    }    
    
    public static void createAnnotationsTable(Connection con, String tableName){
        Statement stmt = null;
        try{
            stmt = con.createStatement();            
            String sql = SQLCommands.getInstance().createAnnotationsTable();
            System.out.println(sql);
            stmt.executeUpdate(sql);
            sql = SQLCommands.getInstance().createAnnotationTableIDIndex();
            System.out.println(sql);
            stmt.executeUpdate(sql);            
            sql = SQLCommands.getInstance().createAnnotationTableDoc_IDIndex();
            System.out.println(sql);
            stmt.executeUpdate(sql);   
            sql = SQLCommands.getInstance().createAnnotationTableDContextStringIndex();
            System.out.println(sql);
            stmt.executeUpdate(sql);            
        }catch(SQLException ex){
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);            
        }        
    }
    
        public static void createFullDocsTable(Connection con, String tableName){
        Statement stmt = null;
        try{
            stmt = con.createStatement();          
            String sql = SQLCommands.getInstance().createFullDocsTable();
            stmt.executeUpdate(sql);
            sql = SQLCommands.getInstance().createFullDocsTableIndex();
            System.out.println(sql);
            stmt.executeUpdate(sql);            
        }catch(SQLException ex){
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);            
        }        
    }

   public  static String cleanTargetTableName(String text) {        
        String returnText = text.replaceAll("\\s+","");
        return returnText;               
    }
   
    public static void checkGateInit(String gateHomeTextField, boolean debugOn) {
        if (!Gate.isInitialised()) {
            Gate.runInSandbox(true);
            Gate.setGateHome(new File(gateHomeTextField));
            // disable log4j
//            if(debugOn){
//                org.apache.log4j.BasicConfigurator.configure();
//            }else{
//                org.apache.log4j.Logger.getRootLogger().setLevel(
//                org.apache.log4j.Level.OFF);                
//            }
            try {
                
                Gate.init();
//                MainFrame.getInstance().setVisible(true);
            } catch (GateException ex) {
                Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }   

	public static void buildKnowtatorXMLDocumentFromGATEXML(Document xmlDocument, gate.Document gateDocument, 
                int instanceSuffix, String projectName) {
		Element annotationsElement = new Element("annotations");                                
		xmlDocument.addContent(annotationsElement);
                //should be doc name
		annotationsElement.setAttribute("textSource", gateDocument.getName());

                //need to ref KEY!!
                AnnotationSet inputAS = gateDocument.getAnnotations(GlobalParameters.KEYANNOTSETNAME).get(GlobalParameters.KEYANNOTSETTYPE);                
                for (gate.Annotation annotation : inputAS) {
                    
                    instanceSuffix++;
                    int annotationSuffix = instanceSuffix;
                    instanceSuffix++;
                    int annotatorSuffix = instanceSuffix;                    
                    instanceSuffix++;
                    int classSuffix = instanceSuffix;
                                        
                    Element annotationElement = new Element("annotation");
                    Element mentionElement = new Element("mention");
                    mentionElement.setAttribute("id", projectName +"_" + annotationSuffix);
                    annotationElement.addContent(mentionElement);
                    
                    String annotatorText = "not implemented";
                    Element annotatorElement = new Element("annotator");
                    annotatorElement.setAttribute("id", projectName +"_" + annotatorSuffix);
                    annotatorElement.addContent(annotatorText);
                    annotationElement.addContent(annotatorElement);                                                     
                    
                    Element spanElement = new Element("span");
                    spanElement.setAttribute("start", "" + gate.Utils.start(annotation));
                    spanElement.setAttribute("end", "" + gate.Utils.end(annotation));
                    annotationElement.addContent(spanElement);
				                    
                    Element spannedTextElement = new Element("spannedText");
                    spannedTextElement.addContent(gate.Utils.stringFor(gateDocument, annotation));
                    annotationElement.addContent(spannedTextElement);     
                    
                    annotationsElement.addContent(annotationElement);
                    
                    //classMention
                    Element classMention = new Element("classMention");
                    classMention.setAttribute("id", projectName +"_" +annotationSuffix );
                    Element mentionClass = new Element("mentionClass");
                    mentionClass.setAttribute("id", "TargetKeyword" );
                    mentionClass.addContent("TargetKeyword");
                    classMention.addContent(mentionClass);
                    Element hasSlotMention = new Element("hasSlotMention");
                    hasSlotMention.setAttribute("id", projectName +"_" + classSuffix);
                    classMention.addContent(hasSlotMention);                                                                                                   
                    annotationsElement.addContent(classMention);
                    //slotmention
                    Element stringSlotMention = new Element("stringSlotMention");
                    stringSlotMention.setAttribute("id", projectName +"_" +classSuffix );                    
                    Element mentionSlot = new Element("mentionSlot");
                    mentionSlot.setAttribute("id", GlobalParameters.MLFEATURENAME );
                    Element stringSlotMentionValue = new Element("stringSlotMentionValue");
                    stringSlotMentionValue.setAttribute("value", annotation.getFeatures().get(GlobalParameters.MLFEATURENAME).toString());
                    stringSlotMention.addContent(mentionSlot);
                    stringSlotMention.addContent(stringSlotMentionValue);
                    annotationsElement.addContent(stringSlotMention);
		}

	}        
}
    

