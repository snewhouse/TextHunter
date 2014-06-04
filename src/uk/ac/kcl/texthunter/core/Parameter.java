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

import gate.Controller;
import gate.LanguageAnalyser;
import gate.creole.AnalyserRunningStrategy;
import gate.creole.ConditionalController;
import gate.creole.RunningStrategy;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 *
 * @author rjackson1
 */


public class Parameter{

//public static int missing;    
//public static int spurious;
//public static int correct;


private  Double precision;
private  Double recall;
private  Double F1;

private String type;
private int ID;
private Double c;
private int d;
private int t;
private Double tau;
private boolean RUN_NEGEX;
private Integer folds;
private boolean removeStopWords;

    public Parameter(String typeValue, Integer IDvalue, Double cValue, 
            Integer tValue, Integer dValue,  Double tauValue, Boolean RUN_NEGEXval, Boolean removeStopsVal, 
            Double precisionVal, Double recallVal, Double F1val, Integer foldVal){
        
        this.type = typeValue;
        this.ID = IDvalue;        
        this.c = cValue;
        this.t = tValue;        
        this.d = dValue;
        this.tau = tauValue;
        this.RUN_NEGEX = RUN_NEGEXval;        
        this.precision = precisionVal;
        this.recall = recallVal;
        this.F1 = F1val;        
        this.folds = foldVal;
        this.removeStopWords = removeStopsVal;       
    }
    
public static  CopyOnWriteArrayList<Parameter> generateParamList(boolean rough, int folds){
    Double [] mlXMLConfigc;
    int [] mlXMLConfigt;
    int [] mlXMLConfigd ;
    double [] mlXMLConfigtau;
    boolean [] mlnegEx;
    boolean [] removeStops;
    
    if(rough){
        boolean [] removeStops1 = {true,false};        
        Double [] mlXMLConfigc1 = { 
            0.1,
            0.2,
            0.4,
            0.5,
            0.6,
            0.7,
            0.8,
            0.9,
            1.0
        };
        
        int [] mlXMLConfigt1 = { 
            0,
            1
        };  
        
       int [] mlXMLConfigd1 = { 
            3,
            2
        }; 
        
        double [] mlXMLConfigtau1 = { 

            1.0

        }; 
        boolean [] negEx1 = {
            true,
            false            
        };
        mlXMLConfigc = mlXMLConfigc1;
        mlXMLConfigt = mlXMLConfigt1;
        mlXMLConfigd = mlXMLConfigd1 ;
        mlXMLConfigtau = mlXMLConfigtau1;   
        mlnegEx = negEx1;
        removeStops = removeStops1;      
    }else{
        boolean [] removeStops1 = {true,false};        
        Double [] mlXMLConfigc1 = { 
            0.1,
            0.2,
            0.4,
            0.5,
            0.6,
            0.7,
            0.8,
            0.9,
            1.0
        };
        
        int [] mlXMLConfigt1 = { 
            0,
            1
        };  
        
       int [] mlXMLConfigd1 = { 
            3,
            2
        }; 
        
        double [] mlXMLConfigtau1 = { 
            0.01,
            0.01,
            1.0,
            4.0,
            6.0
        }; 
        boolean [] negEx1 = {
            true,
            false            
        };
        mlXMLConfigc = mlXMLConfigc1;
        mlXMLConfigt = mlXMLConfigt1;
        mlXMLConfigd = mlXMLConfigd1 ;
        mlXMLConfigtau = mlXMLConfigtau1;   
        mlnegEx = negEx1;
        removeStops = removeStops1;
    }
    
    ArrayList<Parameter> miniParamsList = new ArrayList();
    //loop through all possible parameters for SVM
    
    int paramID = 1;
    String xvalType = rough ? "rough" :"clean";
    
    for(int rsi = 0; rsi<=removeStops.length-1;rsi++){
        for(int negexi = 0; negexi<=mlnegEx.length-1;negexi++){
            for(int ci = 0;ci<=mlXMLConfigc.length-1;ci++){
                for(int ti = 0;ti<=mlXMLConfigt.length-1;ti++){
                    for(int di = 0; di<=mlXMLConfigd.length-1;di++){
                        for (int taui = 0; taui<= mlXMLConfigtau.length-1;taui++){
                            Parameter newParams = new Parameter(xvalType,paramID,mlXMLConfigc[ci],
                                    mlXMLConfigt[ti],mlXMLConfigd[di],
                                    mlXMLConfigtau[taui],mlnegEx[negexi], 
                                    removeStops[rsi],
                                    null,null,null,folds); 
                            miniParamsList.add(newParams);
                            paramID++;
                        }                    
                    }
                }
            }
        }
    }
    Collections.sort(miniParamsList,new IDParameterComparator());
    CopyOnWriteArrayList<Parameter> returnList = new CopyOnWriteArrayList<Parameter>();
    returnList.addAll(miniParamsList);
    return returnList;
    
    
} 

    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    public void setRecall(Double recall) {
        this.recall = recall;
    }

    public void setF1(Double F1) {
        this.F1 = F1;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setC(Double c) {
        this.c = c;
    }

    public void setD(int d) {
        this.d = d;
    }

    public void setT(int t) {
        this.t = t;
    }

    public void setTau(Double tau) {
        this.tau = tau;
    }

    public void setRUN_NEGEX(boolean RUN_NEGEX) {
        this.RUN_NEGEX = RUN_NEGEX;
    }

    public void setFolds(Integer folds) {
        this.folds = folds;
    }

    public void setRemoveStopWords(boolean removeStopWords) {
        this.removeStopWords = removeStopWords;
    }

    public Double getPrecision() {
        return precision;
    }

    public Double getRecall() {
        return recall;
    }

    public Double getF1() {
        return F1;
    }

    public String getType() {
        return type;
    }

    public int getID() {
        return ID;
    }

    public Double getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    public int getT() {
        return t;
    }

    public Double getTau() {
        return tau;
    }

    public boolean isRUN_NEGEX() {
        return RUN_NEGEX;
    }

    public Integer getFolds() {
        return folds;
    }

    public boolean isRemoveStopWords() {
        return removeStopWords;
    }

    public synchronized void calculateResults(int missing, int spurious, int correct){


            precision = (double)correct/((double)correct+(double)spurious);
            precision = Double.isNaN(precision)  ? 0.0 : precision;                  
            recall = (double)correct/((double)correct+(double)missing);
            recall = Double.isNaN(recall) ? 0.0 : recall;                  
            F1 = 2*((precision*recall)/(precision+recall));
            F1 = Double.isNaN(F1) ? 0.0 : F1;    
            
    }
    
public void xmlConfigGenerator(String path){
            //--get xml doc and parse
        
    try{
            DocumentBuilderFactory docBuilderFactoryIn = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilderIn = docBuilderFactoryIn.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilderIn.parse (new File(path));
            doc.getDocumentElement().normalize();

            NodeList selectedNode = doc.getElementsByTagName("ENGINE");
            for (int i = 0; i<= selectedNode.getLength()-1; i++){
                Element engineElement = (Element)selectedNode.item(i);
                engineElement.setAttribute("options", " -c "+this.c +" -t "+this.t+" -d "+this.d+" -m 100 -tau "+this.tau+"  ");
            }

            DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(new File(path)); 
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, sr);      


            transformer.reset();
            docBuilderIn.reset();
        }catch(Exception e){
            System.out.println(e);

        }
        
        
        
    }     



}
class F1ParameterComparator implements Comparator<Parameter> {
    @Override
    public int compare(Parameter p1, Parameter p2) {
        if (p1.getF1() > p2.getF1() ) return -1;
        if (p1.getF1()  < p2.getF1() ) return 1;        
        
        return 0;
    }
    
}

class IDParameterComparator implements Comparator<Parameter> {
    @Override
    public int compare(Parameter p1, Parameter p2) {
        if (p1.getID() < p2.getID() ) return -1;
        if (p1.getID()  > p2.getID() ) return 1;        
        
        return 0;
    }    
    
    
}