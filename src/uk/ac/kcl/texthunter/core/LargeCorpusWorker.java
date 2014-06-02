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

import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rjackson1
 */
public class LargeCorpusWorker implements Runnable {

    private final ArrayList<File> files;
    private CorpusController workerApp;
    private Corpus oneDoc;
    private volatile AtomicInteger threadProgress;
    private String mode;
    private final Charset ENCODING;    
    private Corpus microCorpus;
    private final ThreadSafeResultsMover drop;
    private BlockingQueue<CorpusController> pool;
    private Object lock;
    //threadProgress
    public LargeCorpusWorker( CorpusController workerApp, Corpus corpus, String mode, BlockingQueue<CorpusController> pool, ThreadSafeResultsMover drop, Object lock) throws SQLException{    
        this.threadProgress = new AtomicInteger(0);
        this.mode = mode;
        this.ENCODING = StandardCharsets.UTF_8;
        //order AL results
        files = null;
        this.microCorpus = corpus;
        this.pool = pool;
        this.drop = drop;
        this.lock = lock;
        this.workerApp = workerApp;
    }
    

    public int getThreadProgress(){
        return this.threadProgress.get();
    }

    @Override
    public void run(){        
        try {       
            double startT = System.currentTimeMillis();
            executeClassify();
            double endT = (System.currentTimeMillis() - startT)/1000;
            
            System.out.println("batch done on " + Thread.currentThread().getName() + "in " + endT);            
        }  catch (ResourceInstantiationException | SQLException | InterruptedException ex) {
            Logger.getLogger(LargeCorpusWorker.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            pool.add(workerApp);
        }
    }


    private void executeClassify() throws ResourceInstantiationException, SQLException, InterruptedException{
        workerApp.setCorpus(microCorpus);                                                       
        extractResultsFromCorpus();
        destroyMicroCorpus();                     
    }
                        
    public CorpusController getWorkerApp() {
        return workerApp;
    }

    public Corpus getOneDoc() {
        return oneDoc;
    }
    
    private void destroyMicroCorpus() throws ResourceInstantiationException, SQLException { 
        synchronized(lock){
//            while(!microCorpus.isEmpty()){
//                Factory.deleteResource(microCorpus.get(0));
//            }
            Factory.deleteResource(microCorpus);
        }
    }
    
    private void extractResultsFromCorpus() throws ResourceInstantiationException, SQLException, InterruptedException {    
        try {
            List sqlStatements = new ArrayList<>();
                synchronized(lock){
                    Thread.sleep(1500);
                }
                workerApp.execute();
                for (gate.Document currentDoc : microCorpus) {
                switch (mode) {
                    case "Classify":
                        {
                            sqlStatements.add(currentDoc);
                            threadProgress.addAndGet(1);
                            break;
                        }
                    case "Hunter":
                        {
                            try{
                                if(currentDoc.getAnnotations().get("TargetKeyword") !=null){
                                    sqlStatements.add(currentDoc);
                                }
                                threadProgress.addAndGet(1);
                                break;
                            }catch (java.lang.NullPointerException ex){};
                        }
                    }
                }
            drop.put(sqlStatements);
            } catch (ExecutionException ex) {
                Logger.getLogger(LargeCorpusWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }                
       
