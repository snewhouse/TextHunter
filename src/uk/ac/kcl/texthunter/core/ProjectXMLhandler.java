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


package uk.ac.kcl.texthunter.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.xml.sax.InputSource;
import uk.ac.kcl.texthunter.utils.SQLCommands;

/**
 *
 * @author rjackson1
 */
public class ProjectXMLhandler {
    private Document doc;
    private File file;
    private Connection con;
    private String tableName;
    private int gsPos = 0; 
    private int gsneg = 0; 
    private int gsunk = 0;   
    private int gsform = 0;  
    private int seedpos = 0;   
    private int seedneg = 0;
    private int seedunk = 0;
    private int seedform = 0;    
    private int alpos = 0;   
    private int alneg = 0;
    private int alunk = 0;
    private String precision =  "";
    private String recall =  "";
    private String f1 = "";    

    public Connection getCon() {
        return con;
    }

    public String getTableName() {
        return tableName;
    }

    public int getGsPos() {
        return gsPos;
    }

    public int getGsneg() {
        return gsneg;
    }

    public String getPrecision() {
        return precision;
    }

    public String getRecall() {
        return recall;
    }

    public String getF1() {
        return f1;
    }

    public int getGsunk() {
        return gsunk;
    }

    public int getGsform() {
        return gsform;
    }

    public int getSeedpos() {
        return seedpos;
    }

    public int getSeedneg() {
        return seedneg;
    }

    public int getSeedunk() {
        return seedunk;
    }

    public int getSeedform() {
        return seedform;
    }

    public int getAlpos() {
        return alpos;
    }

    public int getAlneg() {
        return alneg;
    }

    public int getAlunk() {
        return alunk;
    }

    public int getAlform() {
        return alform;
    }
    private int alform = 0;      

    public Document getDoc() {
        return doc;
    }

    public File getFile() {
        return file;
    }
    
    public  ProjectXMLhandler(){
        
    }
    
    public  ProjectXMLhandler(String path) throws IOException{
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            file = new File(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream (file),"utf-8"));
            InputSource input = new InputSource(reader);
            doc = builder.parse(input);
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(ProjectXMLhandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public static void makeNewProject(String projectNameString , String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element project = doc.createElement("project");
            doc.appendChild(project);
            Element projectName = doc.createElement("projectName");
            projectName.setAttribute("id", "projectName");
            project.appendChild(projectName);
            projectName.appendChild(doc.createTextNode(projectNameString));
            Element keyWords = doc.createElement("keyWords");
            keyWords.setAttribute("id", "keyWords");
            keyWords.setIdAttribute("id", true);            
            project.appendChild(keyWords);
            Element otherWords = doc.createElement("otherWords");
            otherWords.setAttribute("id", "otherWords");
            otherWords.setIdAttribute("id", true);
            project.appendChild(otherWords);               
            Element sqlConn = doc.createElement("sqlConn");
            project.appendChild(sqlConn);                  
            Element externalMode = doc.createElement("embeddedMode");
            project.appendChild(externalMode);                                                                       
            Element gspos = doc.createElement("gspos");
            project.appendChild(gspos); 
            Element gsneg = doc.createElement("gsneg");
            project.appendChild(gsneg); 
            Element gsform = doc.createElement("gsform");
            project.appendChild(gsform); 
            Element gsunk = doc.createElement("gsunk");
            project.appendChild(gsunk); 
            Element seedpos = doc.createElement("seedpos");
            project.appendChild(seedpos); 
            Element seedneg = doc.createElement("seedneg");
            project.appendChild(seedneg); 
            Element seedunk = doc.createElement("seedunk");
            project.appendChild(seedunk); 
            Element seedform = doc.createElement("seedform");
            project.appendChild(seedform); 
            Element alpos = doc.createElement("alpos");
            project.appendChild(alpos); 
            Element alneg = doc.createElement("alneg");
            project.appendChild(alneg); 
            Element alform = doc.createElement("alform");
            project.appendChild(alform); 
            Element alunk = doc.createElement("alunk");
            project.appendChild(alunk);             
            Field[] arr = Parameter.class.getDeclaredFields();            
            for (int i =0;i<=arr.length-1;i++){
                Element parameter = doc.createElement(arr[i].getName());
                project.appendChild(parameter);                   
            }
            DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StreamResult sr = new StreamResult(new File(path + File.separator + projectNameString +".xml")); 
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, sr);      
            transformer.reset();        
            builder.reset();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ProjectXMLhandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex){
            Logger.getLogger(ProjectXMLhandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ProjectXMLhandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    

    public void writeBestParams(Parameter param) throws NoSuchFieldException{            
        Field[] arr = Parameter.class.getDeclaredFields();
        for (int i =0;i<=arr.length-1;i++){                  
            Node node =  doc.getElementsByTagName(arr[i].getName()).item(0) ;
            if(arr[i].getName().equals("type") ){
                node.setTextContent(param.getType());
            }else if (arr[i].getName().equals("RUN_NEGEX")){
                node.setTextContent(Boolean.toString(param.isRUN_NEGEX()));                
            }else if (arr[i].getName().equals("c")){
                node.setTextContent(String.valueOf(param.getC()));  
            }else if (arr[i].getName().equals("d")){
                node.setTextContent(String.valueOf(param.getD()));  
            }else if (arr[i].getName().equals("t")){
                node.setTextContent(String.valueOf(param.getT()));  
            }else if (arr[i].getName().equals("tau")){
                node.setTextContent(String.valueOf(param.getTau()));  
            }else if (arr[i].getName().equals("removeStopWords")){
                node.setTextContent(Boolean.toString(param.isRemoveStopWords()));  
            }else if (arr[i].getName().equals("F1")){
                node.setTextContent(Double.toString(param.getF1()));  
            }else if (arr[i].getName().equals("precision")){
                node.setTextContent(Double.toString(param.getPrecision()));  
            }else if (arr[i].getName().equals("ID")){
                node.setTextContent(Integer.toString(param.getID()));  
            }else if (arr[i].getName().equals("recall")){
                node.setTextContent(Double.toString(param.getRecall()));  
            }else if (arr[i].getName().equals("folds")){
                node.setTextContent(Integer.toString(param.getFolds()));  
            }
            
        }                 
    }
    
    public void updateWords(String[] words, boolean keyOrOther){
        
        ArrayList<String> kwal = new ArrayList<String>(Arrays.asList(words));        
        Node keyWords = keyOrOther ? doc.getElementsByTagName("keyWords").item(0) : doc.getElementsByTagName("otherWords").item(0);
        removeAllChildNodes(keyWords);
        int keywordID = 0;
        for (String word : kwal){
            Element wordElement = doc.createElement("word");
            wordElement.setAttribute("id", Integer.toString(keywordID));
            wordElement.appendChild(doc.createTextNode(word.trim()));   
            keyWords.appendChild(wordElement);
            keywordID++;    
        }
    }
    
    private static void removeAllChildNodes(Node node) {        
        NodeList childNodes = node.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node childNode = childNodes.item(i);
            if(childNode instanceof Element) {
                if(childNode.hasChildNodes()) {
                    removeAllChildNodes(childNode);                     
                }        
                node.removeChild(childNode);  
            }
        }
        while( node.hasChildNodes() )
        node.removeChild( node.getFirstChild() );
}


    public  void saveDoc() {
        try {
            doc.normalizeDocument();
            DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StreamResult sr = new StreamResult(file); 
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(domSource, sr);
            transformer.reset();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(ProjectXMLhandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ProjectXMLhandler.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
    public String getWords(boolean keyOrOther){
        String returnString = "";              
        Node keyWords = keyOrOther ? doc.getElementsByTagName("keyWords").item(0) : doc.getElementsByTagName("otherWords").item(0);
        NodeList nodeList = keyWords.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                returnString = returnString + currentNode.getTextContent() + "\n";
            }
        }                
       return returnString.trim();
    }
    
    public String getsqlConn(){
        String returnString = "";              
        Node currentNode = doc.getElementsByTagName("sqlConn").item(0);
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            //calls this method for all the children which is Element
            returnString = returnString + currentNode.getTextContent();
        }        
       return returnString;
    }    
    
    public boolean getEmbeddedMode(){
        String returnString = "";              
        Node currentNode = doc.getElementsByTagName("embeddedMode").item(0);
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            //calls this method for all the children which is Element
            returnString =  currentNode.getTextContent();
        }        
        boolean result = Boolean.parseBoolean(returnString);
        
       return result;
    }     
    
    public String getGateHome(){
        String returnString = "";              
        Node currentNode = doc.getElementsByTagName("gateHome").item(0);   
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            //calls this method for all the children which is Element
            returnString = returnString + currentNode.getTextContent();
        }        
       return returnString;
    }    
    
    public Parameter getBestParameter(){
        if(doc.getElementsByTagName("type").item(0).getTextContent().equalsIgnoreCase("")){
            return null;
        }else{
            Parameter parameter = new Parameter(
                    doc.getElementsByTagName("type").item(0).getTextContent(),
                    Integer.parseInt(doc.getElementsByTagName("ID").item(0).getTextContent()),
                    Double.parseDouble(doc.getElementsByTagName("c").item(0).getTextContent()),
                    Integer.parseInt(doc.getElementsByTagName("t").item(0).getTextContent()),    
                    Integer.parseInt(doc.getElementsByTagName("d").item(0).getTextContent()),
                    Double.parseDouble(doc.getElementsByTagName("tau").item(0).getTextContent()),
                    Boolean.parseBoolean(doc.getElementsByTagName("RUN_NEGEX").item(0).getTextContent()),
                    Boolean.parseBoolean(doc.getElementsByTagName("removeStopWords").item(0).getTextContent()),                 
                    Double.parseDouble(doc.getElementsByTagName("precision").item(0).getTextContent()),
                    Double.parseDouble(doc.getElementsByTagName("recall").item(0).getTextContent()),
                    Double.parseDouble(doc.getElementsByTagName("F1").item(0).getTextContent()),
                    Integer.parseInt(doc.getElementsByTagName("folds").item(0).getTextContent())                 
                    );

           return parameter;
        }
    }  
    
    
    public void updateMetadata( String sqlConn, String externalMode ){
      
 

        Node node =  doc.getElementsByTagName("sqlConn").item(0) ;            
        node.setTextContent(sqlConn);         
        node =  doc.getElementsByTagName("embeddedMode").item(0) ;            
        node.setTextContent(externalMode);              


    }
    
    
    public void updateProjectSummary(Connection con, String tableName) throws SQLException{

        Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = statement.executeQuery(SQLCommands.getInstance().getModelSummary());  
        Node node;
        while (rs.next()) {
            switch (rs.getString(1)+"_"+rs.getString(2)) {
                case "gold_positive":
                    this.gsPos = rs.getInt(3);
                    node =  doc.getElementsByTagName("gspos").item(0) ;            
                    node.setTextContent(Integer.toString(gsPos));                      
                    break;
                case "gold_negative": 
                    this.gsneg = rs.getInt(3);                    
                    node =  doc.getElementsByTagName("gsneg").item(0) ;            
                    node.setTextContent(Integer.toString(gsneg));                       
                    break;
                case "gold_unknown":
                    this.gsunk = rs.getInt(3);                
                    node =  doc.getElementsByTagName("gsunk").item(0) ;            
                    node.setTextContent(Integer.toString(gsunk));                       
                    break;
                case "gold_form": 
                    this.gsform = rs.getInt(3);      
                    node =  doc.getElementsByTagName("gsform").item(0) ;            
                    node.setTextContent(Integer.toString(gsform));                       
                    break;        
                case "seed_positive":
                    this.seedpos = rs.getInt(3);    
                    node =  doc.getElementsByTagName("seedpos").item(0) ;            
                    node.setTextContent(Integer.toString(seedpos));                       
                    break;
                case "seed_negative": 
                    this.seedneg = rs.getInt(3);       
                    node =  doc.getElementsByTagName("seedneg").item(0) ;            
                    node.setTextContent(Integer.toString(seedneg));                       
                    break;
                case "seed_unknown":
                    this.seedunk = rs.getInt(3);     
                    node =  doc.getElementsByTagName("seedunk").item(0) ;            
                    node.setTextContent(Integer.toString(seedunk));                       
                    break;
                case "seed_form": 
                    this.seedform = rs.getInt(3);  
                    node =  doc.getElementsByTagName("seedform").item(0) ;            
                    node.setTextContent(Integer.toString(seedform));                                        
                    break;  
                case "al_positive":
                    this.alpos = rs.getInt(3);      
                    node =  doc.getElementsByTagName("alpos").item(0) ;            
                    node.setTextContent(Integer.toString(alpos));                                         
                    break;
                case "al_negative": 
                    this.alneg = rs.getInt(3);  
                    node =  doc.getElementsByTagName("alneg").item(0) ;            
                    node.setTextContent(Integer.toString(alneg));                                        
                    break;
                case "al_unknown":
                    this.alunk = rs.getInt(3);  
                    node =  doc.getElementsByTagName("alunk").item(0) ;            
                    node.setTextContent(Integer.toString(alunk));                                         
                    break;
                case "al_form": 
                    this.alform = rs.getInt(3);   
                    node =  doc.getElementsByTagName("alform").item(0) ;            
                    node.setTextContent(Integer.toString(alform));                                         
                    break;                      
            }
        }        
        this.precision = doc.getElementsByTagName("precision").item(0).getTextContent();
        this.recall = doc.getElementsByTagName("recall").item(0).getTextContent();
        this.f1 = doc.getElementsByTagName("F1").item(0).getTextContent();                        
    }        
}
