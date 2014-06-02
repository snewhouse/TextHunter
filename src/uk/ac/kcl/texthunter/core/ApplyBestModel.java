//    Text Hunter: User friendly information extraction from text
//
//    Copyright (C) 2014  Richard Jackson (richard.g.jackson@slam.nhs.uk)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as
//    published by the Free Software Foundation, either version 3 of the
//    License, or (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
package uk.ac.kcl.texthunter.core;

import gate.Corpus;
import gate.CorpusController;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rjackson1
 */
public class ApplyBestModel implements Runnable {

    private final ResultSet rs;
    private final CorpusController allClassApp;
    private gate.Document doc;
    private Corpus oneDoc;
    private final Connection  threadCon;
    private volatile AtomicInteger threadProgress;
    private volatile  List sqlStatements;
    //threadProgress
    public ApplyBestModel(String sql, CorpusController app, Connection con, String targetTableName) throws SQLException{
        this.sqlStatements =  Collections.synchronizedList(new ArrayList());
        this.allClassApp = app;
        this.threadCon = con;
        this.threadProgress = new AtomicInteger(0);
        threadCon.setAutoCommit(false); 
        Statement swStmt = threadCon.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        swStmt.setFetchSize(1000);
        System.out.println(sql);
        //order AL results
        rs = swStmt.executeQuery(sql);
    }
    
    public ArrayList<String[]> readAndClear(){
        synchronized(sqlStatements){
            ArrayList<String[]> returnList = new ArrayList();
            returnList.addAll(sqlStatements);
            sqlStatements.clear();
            return returnList;
        }
    }
    
    
    public int getThreadProgress(){
        return this.threadProgress.get();
    }

    @Override
    public void run(){
        try {
            oneDoc = Factory.newCorpus("oneDoc");
            allClassApp.setCorpus(oneDoc);
            FeatureMap result = null;                                                   
            while (rs.next()) {
                try{
                    doc = Factory.newDocument(rs.getString("contextString"));
                    oneDoc.add(doc);
                    allClassApp.execute();
                    Iterator<gate.Annotation> annotIt = doc.getAnnotations("ML").iterator();

                    if (annotIt.hasNext()){
                        gate.Annotation annot = annotIt.next();
                        result =  annot.getFeatures();
                    }
                        
                }catch (Exception e){
                    System.out.println("something went wrong on annotation no " + rs.getString("id"));
                    System.out.println(e);
                }
                
                String[] str = new String[5];
                if( result != null){                                             
                    str[0] =  result.get("observation").toString();
                    str[1] =   result.get("prob").toString();
                    str[2] =   result.get("priority").toString();
                    str[3] =   result.get("numWords").toString();
                    str[4] =   rs.getString("ID");                                            
                }else{                   
                    str[0] =   "error";
                    str[1] =   "error";
                    str[2] =   "error";
                    str[3] =   "error";
                    str[4] =   rs.getString("ID");
                }                  
               // System.out.println("array during" +str[4]);

                //this may need to be synched

                sqlStatements.add(str);
                oneDoc.remove(doc);
                Factory.deleteResource(doc);
                threadProgress.addAndGet(1);
                if(Thread.interrupted()){
                    return;
                }
            }
        } catch (ResourceInstantiationException ex) {
            Logger.getLogger(ApplyBestModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ApplyBestModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CorpusController getAllClassApp() {
        return allClassApp;
    }

    public Corpus getOneDoc() {
        return oneDoc;
    }
    
}