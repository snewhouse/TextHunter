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

import gate.Factory;
import gate.FeatureMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rjackson1
 */
public final class DBCommitter implements Runnable {
    private final List commitList;
    private final String mode;
    private final int batchSize;
    private final String targetTableName;
    private final Connection commitCon;
    private final ThreadSafeResultsMover drop;
//    private final commitBlocker cb;
//    private BlockingQueue<DBCommitterAction> pool;
    private ExecutorService threads;
    DBCommitter(String mode, String targetTableName,  Connection commitCon, ThreadSafeResultsMover drop){
        this.commitList = Collections.synchronizedList(new ArrayList());
        this.mode = mode;
        this.batchSize = GlobalParameters.DBCOMMITBATCHSIZE;
        this.targetTableName = targetTableName; 
        this.commitCon = commitCon;
        this.drop = drop;
        this.threads = Executors.newFixedThreadPool(GlobalParameters.MAXIMUMDBCOMMITERPOOLSIZE);
//        pool = new LinkedBlockingQueue<>();
//        for(int i = 1;i<=GlobalParameters.MAXIMUMDBCOMMITERPOOLSIZE;i++){
//            pool.add(new DBCommitterAction());
//        }
        
    }

    private List subsetList(){
        List smallList = new ArrayList();        
        synchronized(commitList){
            Iterator<Object> it = commitList.iterator();
            for(int i =0;i<=batchSize;i++){
                if(it.hasNext()){
                    Object o = it.next();
                    smallList.add(o);
                    it.remove();
                }                
            }
        }
        return smallList;
    }



    @Override
    public void run(){

        //loop while workers are alive
        while(!Thread.interrupted()&!drop.isFinalBatch()){
            List newBatch = (ArrayList)drop.take();
            if(newBatch!=null){
                synchronized(commitList){
                    commitList.addAll(newBatch);
                }
            }
//            if(commitList.size()>batchSize&pool.peek()!=null){  
              if(commitList.size()>batchSize){
                DBCommitterAction newCommiter = new DBCommitterAction();
                threads.execute(newCommiter);
            }
        }

        while(!Thread.interrupted()&(commitList.size()>batchSize)){
            if(commitList.size()>batchSize){                
                DBCommitterAction newCommiter = new DBCommitterAction();
                threads.execute(newCommiter);
            }
        }        
        
        if(!Thread.interrupted()){
                DBCommitterAction newCommiter = new DBCommitterAction();
                threads.execute(newCommiter);      
                threads.shutdown();
            try {
                threads.awaitTermination(2, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                Logger.getLogger(DBCommitter.class.getName()).log(Level.SEVERE, null, ex);
            }
//            try {
////                while(pool.size()!=GlobalParameters.MAXIMUMDBCOMMITERPOOLSIZE){
////                    Thread.sleep(500);
////                }
////                Thread t = null;
////                DBCommitterAction newCommiter = pool.take();         
////                t = new Thread(newCommiter);
////                t.start();
////                t.join();      
//                
//                
//            } catch (InterruptedException ex) {
//                Logger.getLogger(DBCommitter.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
       
    }               


    private final class DBCommitterAction implements Runnable{
        private boolean committing;



        public DBCommitterAction() {
            this.committing = false;
        }
        

        
        @Override
        public void run() {
            try {
                commitUpdatesToDB();
                //pool.add(this);
            } catch (SQLException ex) {
                Logger.getLogger(DBCommitter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void setCommitting(boolean committing) {
            this.committing = committing;
        }
        
        public void commitUpdatesToDB() throws SQLException{
            commitCon.setAutoCommit(false);        
            switch (mode) {
                case "Classify":
                    {
                        String insertTableSQL = "UPDATE "+ targetTableName+" "
                        + " Set mlObservation1 = ?, \n"
                        + " prob = ?, \n"
                        + " MLPRIORITY = ?, \n"
                        + " numWords = ? "                                 
                        + " WHERE ID = ?";
                        PreparedStatement preparedStatement = commitCon.prepareStatement(insertTableSQL);
                        List<gate.Document> batchList = subsetList();
                        for(gate.Document currentDoc :batchList){
                            Iterator<gate.Annotation> annotIt = currentDoc.getAnnotations("ML").iterator();
                            FeatureMap result = null;
                            ArrayList<String[]> sqlStatements = new ArrayList();
                            while (annotIt.hasNext()){
                                gate.Annotation annot = annotIt.next();
                                result =  annot.getFeatures();
                                String[] returnArray = new String[5];
                                if( result != null){                                             
                                    returnArray[0] =  result.get("observation").toString();
                                    returnArray[1] =   result.get("prob").toString();
                                    returnArray[2] =   result.get("priority").toString();
                                    returnArray[3] =   result.get("numWords").toString();                                                                   
                                    returnArray[4] =   currentDoc.getFeatures().get("id").toString();
                                }else{
                                    returnArray[0] =   "error";
                                    returnArray[1] =   "error";
                                    returnArray[2] =   "error";
                                    returnArray[3] =   "error";                                                                    
                                    returnArray[4] =   currentDoc.getFeatures().get("id").toString();
                                }
                                sqlStatements.add(returnArray);        
                            }
                            for(String[] sql : sqlStatements){                            
                                    preparedStatement.setString(1,sql[0]);
                                    preparedStatement.setString(2,sql[1]);
                                    preparedStatement.setString(3,sql[2]);
                                    preparedStatement.setString(4,sql[3]);
                                    preparedStatement.setString(5,sql[4]);                    
                                    preparedStatement.addBatch();
                                }                                
                            }
                            System.out.println("Committing batch of " + batchList.size());    
                            preparedStatement.executeBatch();     
                            commitCon.commit();
                            System.out.println("Batch Committed");
                            preparedStatement.close();  
                            Iterator<gate.Document> it = batchList.iterator();
                            //delete gate docs
                            while(it.hasNext()){
                                Factory.deleteResource(it.next());
                                it.remove();
                            }                            
                        }                       
                    break;                    
                case "Hunter":
                    {
                        String insertDocumentTableSQL = "INSERT INTO "+ targetTableName+"_DOCUMENTS "
                                + " (FILENAME, TEXTCONTENT) "
                                + "VALUES(?,?)";                       
                        PreparedStatement preparedStatement1 = commitCon.prepareStatement(insertDocumentTableSQL , Statement.RETURN_GENERATED_KEYS);                            
                       
                        
                        
                        String insertAnnotationTableSQL = "INSERT INTO "+ targetTableName 
                                + " ( ANNOTSTART, ANNOTEND, \"MATCH\", "
                                + "CONTEXTSTART, CONTEXTEND, CONTEXTSTRING, CN_DOC_ID) "
                                + "VALUES (?,?,?,?,?,?,?)";      
                        PreparedStatement preparedStatement2 = commitCon.prepareStatement(insertAnnotationTableSQL);
                                      
                        List<gate.Document> batchList = subsetList();
                        for(gate.Document currentDoc :batchList){
                            if(currentDoc.getAnnotations("").get("TargetKeyword").size()!=0){
                                preparedStatement1.setString(1, currentDoc.getName());
                                preparedStatement1.setString(2, currentDoc.getContent().toString());      
                                preparedStatement1.addBatch();
                                Iterator<gate.Annotation> annotIt = currentDoc.getAnnotations("").get("TargetKeyword").iterator();                                                              
                                preparedStatement1.executeBatch();
                                ResultSet rs = null;
                                rs = preparedStatement1.getGeneratedKeys();
                                Integer hageId = null;
                                if (rs.next()) {
                                    hageId = rs.getInt(1);
                                    while (annotIt.hasNext()){
                                        FeatureMap result = annotIt.next().getFeatures();
                                        if( result != null){
                                            preparedStatement2.setString(1, result.get("annotStart").toString());
                                            preparedStatement2.setString(2, result.get("annotEnd").toString());
                                            preparedStatement2.setString(3, result.get("name").toString());
                                            preparedStatement2.setString(4, result.get("contextStart").toString());
                                            preparedStatement2.setString(5, result.get("contextEnd").toString());
                                            preparedStatement2.setString(6, result.get("contextString").toString());                                                                  
                                            preparedStatement2.setInt(7, hageId);                                      
                                            preparedStatement2.addBatch();
                                        }
                                    }
                                }
                                rs.close();
                            }
                        }            
                        System.out.println("Committing batch of " + batchList.size());   
                        preparedStatement2.executeBatch();                    
                        commitCon.commit();
                        preparedStatement2.close();
                        preparedStatement1.close();            
                        System.out.println("Batch Committed");
                        Iterator<gate.Document> it = batchList.iterator();
                        //delete gate docs
                        while(it.hasNext()){
                            Factory.deleteResource(it.next());
                            it.remove();
                        }
                    }                     
                    break;                
                }            
            
            }
        

        public boolean isCommitting() {
            return committing;
        }
    }           
}