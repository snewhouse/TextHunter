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

import uk.ac.kcl.texthunter.utils.Utils;
import gate.*;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.AnnotationDiffer;
import gate.util.persistence.PersistenceManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.FileUtils;

public class MLModelMaker {

    private Corpus fincorpus;
    private Corpus opcorpus;
    private int threadCount;
    private CopyOnWriteArrayList<XValidate> alXValidate;
    private Iterator<XValidate> xvalARit;
    private long outerCorpSizeRatio;
    private ProjectXMLhandler projectXML;
    private File allClassGapp;
    private int folds = 2;
    private Corpus goldStandardCorpus;

    public File getAllClassGapp() {
        return allClassGapp;
    }

    public CopyOnWriteArrayList<Parameter> getParamsList() {
        return paramsList;
    }
    private String annSetA = null;
    private String annSetB = null;
    private String type = null;
    private String feature = null;
    private String resultsFile = null;
    private String MLConfigFile = null;
    private Parameter currentParams;
    private Corpus trainingDocs = null;
    private String applicationLocation;
    private CorpusController allClassApp;
    private Parameter finalParams;
    private boolean roughValidation;
    private volatile CopyOnWriteArrayList<Parameter> paramsList;
    private boolean resumeXValidation;
    private volatile double bestResultF1 = 0.0;
    private static final Object loadLock = new Object();
    private static final Object writeLock = new Object();
    private CountDownLatch cdl;
    private TextHunterMLCCWrapper app;
    private boolean multiClassMode;
    private volatile boolean busy;

    public synchronized boolean isBusy() {
        return busy;
    }

    public synchronized void setBusy(boolean busy) {
        this.busy = busy;
    }
    private static MLModelMaker instance = new MLModelMaker();
    
    
    
    private MLModelMaker() {
        this.busy = false;        
        //new way with goldstandard xval
        this.annSetA = GlobalParameters.TESTKEYANNOTSETNAME;
        this.annSetB = GlobalParameters.MLANNOTSETNAME;
        this.type = GlobalParameters.CONTEXT;
        this.feature = GlobalParameters.MLFEATURENAME;
        this.outerCorpSizeRatio = 5;
        this.annSetA = GlobalParameters.TESTKEYANNOTSETNAME;
        this.annSetB = GlobalParameters.MLANNOTSETNAME;
        this.type = GlobalParameters.CONTEXT;
        this.feature = GlobalParameters.MLFEATURENAME;             
    }
//    MLModelMaker(String appLoc, String resultsLoc,
//            int foldsNo, String gateHomeLoc,
//            Corpus allDocsCorpus, boolean rough,
//            boolean resume, int threadCount,
//            ProjectXMLhandler projectXML, boolean multiClassMode) {
//        //old way with internal xval
//        this.alXValidate = new CopyOnWriteArrayList();
//        this.applicationLocation = appLoc;
//        applicationLocation
//        this.folds = foldsNo;
//        this.annSetA = GlobalParameters.TESTKEYANNOTSETNAME;
//        this.annSetB = GlobalParameters.MLANNOTSETNAME;
//        this.type = GlobalParameters.CONTEXT;
//        this.feature = GlobalParameters.MLFEATURENAME;
//        this.resultsFile = resultsLoc;
//        this.MLConfigFile = appLoc + File.separator + "MLconfig.xml";
//        this.trainingDocs = allDocsCorpus;
//        this.roughValidation = rough;
//        this.resumeXValidation = resume;
//        this.threadCount = threadCount;
//        this.outerCorpSizeRatio = 5;
//        this.projectXML = projectXML;
//        this.multiClassMode = multiClassMode;
//        if (this.multiClassMode) {
//            System.out.println("TextHunter is in MultiClass mode");
//        } else {
//            System.out.println("TextHunter is in Positive Instance mode");
//        }
//    }
//
    
//    and other
//    MLModelMaker(String appLoc, String resultsLoc,
//            int foldsNo,
//            Corpus goldStandardCorpus, Corpus modelTrainingCorpus, boolean rough,
//            boolean resume, int threadCount,
//            ProjectXMLhandler projectXML, boolean multiClassMode) {
//        //new way with goldstandard xval
//        this.alXValidate = new CopyOnWriteArrayList();
//        this.applicationLocation = appLoc;
//        this.allClassGapp = new File(appLoc + File.separator + "all_classes.gapp");
//        this.folds = foldsNo;
//        this.annSetA = GlobalParameters.TESTKEYANNOTSETNAME;
//        this.annSetB = GlobalParameters.MLANNOTSETNAME;
//        this.type = GlobalParameters.CONTEXT;
//        this.feature = GlobalParameters.MLFEATURENAME;
//        this.resultsFile = resultsLoc;
//        this.MLConfigFile = appLoc + File.separator + "MLconfig.xml";
//        this.goldStandardCorpus = goldStandardCorpus;
//        this.trainingDocs = modelTrainingCorpus;
//        this.roughValidation = rough;
//        this.resumeXValidation = resume;
//        this.threadCount = threadCount;
//        this.outerCorpSizeRatio = 5;
//        this.projectXML = projectXML;
//        this.multiClassMode = multiClassMode;
//
//        if (this.multiClassMode) {
//            System.out.println("TextHunter is in MultiClass mode");
//        } else {
//            System.out.println("TextHunter is in Positive Instance mode");
//        }
//    }

    
    public Corpus getFincorpus() {
        return fincorpus;
    }

    public void setFincorpus(Corpus fincorpus) {
        this.fincorpus = fincorpus;
    }

    public Corpus getOpcorpus() {
        return opcorpus;
    }

    public void setOpcorpus(Corpus opcorpus) {
        this.opcorpus = opcorpus;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public CopyOnWriteArrayList<XValidate> getAlXValidate() {
        return alXValidate;
    }

    public void setAlXValidate(CopyOnWriteArrayList<XValidate> alXValidate) {
        this.alXValidate = alXValidate;
    }

    public Iterator<XValidate> getXvalARit() {
        return xvalARit;
    }

    public void setXvalARit(Iterator<XValidate> xvalARit) {
        this.xvalARit = xvalARit;
    }

    public long getOuterCorpSizeRatio() {
        return outerCorpSizeRatio;
    }

    public void setOuterCorpSizeRatio(long outerCorpSizeRatio) {
        this.outerCorpSizeRatio = outerCorpSizeRatio;
    }

    public ProjectXMLhandler getProjectXML() {
        return projectXML;
    }

    public void setProjectXML(ProjectXMLhandler projectXML) {
        this.projectXML = projectXML;
    }

    public int getFolds() {
        return folds;
    }

    public void setFolds(int folds) {
        this.folds = folds;
    }

    public Corpus getGoldStandardCorpus() {
        return goldStandardCorpus;
    }

    public void setGoldStandardCorpus(Corpus goldStandardCorpus) {
        this.goldStandardCorpus = goldStandardCorpus;
    }

    public String getAnnSetA() {
        return annSetA;
    }

    public void setAnnSetA(String annSetA) {
        this.annSetA = annSetA;
    }

    public String getAnnSetB() {
        return annSetB;
    }

    public void setAnnSetB(String annSetB) {
        this.annSetB = annSetB;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getResultsFile() {
        return resultsFile;
    }

    public void setResultsFile(String resultsFile) {
        this.resultsFile = resultsFile;
    }

    public String getMLConfigFile() {
        return MLConfigFile;
    }

    public void setMLConfigFile(String MLConfigFile) {
        this.MLConfigFile = MLConfigFile;
    }

    public Parameter getCurrentParams() {
        return currentParams;
    }

    public void setCurrentParams(Parameter currentParams) {
        this.currentParams = currentParams;
    }

    public Corpus getTrainingDocs() {
        return trainingDocs;
    }

    public void setTrainingDocs(Corpus trainingDocs) {
        this.trainingDocs = trainingDocs;
    }

    public String getApplicationLocation() {
        return applicationLocation;
    }

    public void setApplicationLocation(String applicationLocation) {
        this.applicationLocation = applicationLocation;
    }

    public CorpusController getAllClassApp() {
        return allClassApp;
    }

    public void setAllClassApp(CorpusController allClassApp) {
        this.allClassApp = allClassApp;
    }

    public Parameter getFinalParams() {
        return finalParams;
    }

    public void setFinalParams(Parameter finalParams) {
        this.finalParams = finalParams;
    }

    public boolean isRoughValidation() {
        return roughValidation;
    }

    public void setRoughValidation(boolean roughValidation) {
        this.roughValidation = roughValidation;
    }

    public boolean isResumeXValidation() {
        return resumeXValidation;
    }

    public void setResumeXValidation(boolean resumeXValidation) {
        this.resumeXValidation = resumeXValidation;
    }

    public double getBestResultF1() {
        return bestResultF1;
    }

    public void setBestResultF1(double bestResultF1) {
        this.bestResultF1 = bestResultF1;
    }

    public CountDownLatch getCdl() {
        return cdl;
    }

    public void setCdl(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    public TextHunterMLCCWrapper getApp() {
        return app;
    }

    public void setApp(TextHunterMLCCWrapper app) {
        this.app = app;
    }

    public boolean isMultiClassMode() {
        return multiClassMode;
    }

    public void setMultiClassMode(boolean multiClassMode) {
        this.multiClassMode = multiClassMode;
    }

    public static MLModelMaker getInstance() {
        return instance;
    }

    public static void setInstance(MLModelMaker instance) {
        MLModelMaker.instance = instance;
    }


    public void loadApps() {
        try {
            this.allClassApp =
                    (CorpusController) PersistenceManager.loadObjectFromFile(this.allClassGapp);
            this.app = new TextHunterMLCCWrapper(allClassApp);
        } catch (IOException | ResourceInstantiationException | PersistenceException ex) {
            Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setResume(boolean choice) {
        resumeXValidation = choice ? true : false;
    }

    public void executeTrainAll() {
        try {
            setBusy(true);
            //Train only    
            resetModel();
            System.out.println();
            System.out.println();
            finalParams = projectXML.getBestParameter();
            System.out.println("Training best model on " + trainingDocs.size() + " documents on model ID " + finalParams.getID());
            //save best app before training             
            app.changeFeatureSelection(finalParams);
            finalParams.xmlConfigGenerator(MLConfigFile);
            app.reinitialisePRs();
            app.getApp().setCorpus(null);
            app.setTrainingMode("APPLICATION_ALL_CLASSES");
            PersistenceManager.saveObjectToFile(app.getApp(), allClassGapp, true, false);
            //execute training
            app.setTrainingMode("TRAINING");
            app.getApp().setCorpus(trainingDocs);
            app.getApp().execute();
        } catch (IOException | ExecutionException | PersistenceException | ResourceInstantiationException ex) {
            Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ArrayList<ProcessingResource> trPRsList = new ArrayList(app.getApp().getPRs());
            for (ProcessingResource pr : trPRsList) {
                Factory.deleteResource(pr);
            }
            Factory.deleteResource(app.getApp());
            setBusy(false);
        }
    }

    public FeatureMap executeXVal() {
        setBusy(true);
        //before threads are started set countdown to one for cleanup routine
        cdl = new CountDownLatch(1);
        //load docs
        ArrayList<Corpus> corpusSetup;
        try {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }

            try {
                File file = new File(resultsFile);
                File xvalResultsFile = new File(file.getAbsoluteFile() + File.separator + "crossValidationResults.tsv");
                if (resumeXValidation) {
                    ArrayList<Parameter> prevResults = parseCmdLineParamFile(xvalResultsFile);
                    this.folds = prevResults.get(0).getFolds();
                    if (prevResults.get(0).getType().equals("clean")) {
                        paramsList = Parameter.generateParamList(false, folds);
                    } else {
                        paramsList = Parameter.generateParamList(true, folds);
                    }
                    for (Parameter toDoParam : paramsList) {
                        for (Parameter savedParam : prevResults) {
                            if (toDoParam.getID() == savedParam.getID()) {
                                toDoParam.setF1(savedParam.getF1());
                                toDoParam.setPrecision(savedParam.getPrecision());
                                toDoParam.setRecall(savedParam.getRecall());
                            }
                        }
                    }
                } else {
                    paramsList = Parameter.generateParamList(roughValidation, folds);
                }

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                if (folds > trainingDocs.size()) {
                    System.out.println("Insufficient documents available (" + trainingDocs.size() + ") for " + folds + " folds.");
                } else {
                    File xvalCorpusDir = new File(resultsFile + File.separator + "xvalCorpus");
                    File finCorpusDir = new File(resultsFile + File.separator + "outerCorpus");

                    File tempAppFolder = new File(resultsFile + File.separator + "temp");
                    if (!tempAppFolder.exists()) {
                        tempAppFolder.mkdir();
                    }
                    if (!resumeXValidation) {
                        for (int i = 0; i <= 5; i++) {
                            try {
                                Thread.sleep(500);
                                FileUtils.deleteDirectory(xvalCorpusDir);
                                FileUtils.deleteDirectory(finCorpusDir);
                                break;
                            } catch (IOException ex) {
                                System.out.println("Attempting deletion " + i);
                            }
                        }
                        corpusSetup = setUpCorpora(trainingDocs, true);
                        this.fincorpus = corpusSetup.get(0);
                        this.opcorpus = corpusSetup.get(1);
                        // if file doesnt exists, then create it
                        if (file.mkdir()) {
                            System.out.println("Directory Created");
                        } else {
                            System.out.println("Directory is not created");
                        }
                        //save corpora in case of interruption
                        if (finCorpusDir.mkdir()) {
                            System.out.println("outerCorpus dir Created");
                        } else {
                            System.out.println("outerCorpus dir  is not created");
                        }

                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                        for (gate.Document doc : fincorpus) {
                            try {
                                gate.corpora.DocumentStaxUtils.writeDocument(doc, new File(finCorpusDir.getAbsolutePath() + File.separator + doc.getName()));
                            } catch (XMLStreamException | IOException ex) {
                                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if (xvalCorpusDir.mkdir()) {
                            System.out.println("xvalCorpus dir Created");
                        } else {
                            System.out.println("xvalCorpus dir  is not created");
                        }
                        for (gate.Document doc : opcorpus) {
                            try {
                                gate.corpora.DocumentStaxUtils.writeDocument(doc, new File(xvalCorpusDir.getAbsolutePath() + File.separator + doc.getName()));
                            } catch (XMLStreamException | IOException ex) {
                                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    } else {
                        fincorpus = Factory.newCorpus("OuterCorpus");
                        opcorpus = Factory.newCorpus("OptimisationCorpus");
                        this.fincorpus.populate((finCorpusDir.toURI().toURL()), null, "UTF-8", false);
                        this.opcorpus.populate(xvalCorpusDir.toURI().toURL(), null, "UTF-8", false);
                    }
                    if (getProgressThroughParamList() == paramsList.size()) {
                        return null;
                    }


                    //write header
                    FileWriter fw = new FileWriter((xvalResultsFile), resumeXValidation);
                    try (BufferedWriter bw = new BufferedWriter(fw)) {
                        String content = "type\tID\tc\tt\td\ttau\tnegex\trmStops\tmissing\tspurious\tcorrect\tP\tR\tF1\tfolds";
                        if (!resumeXValidation) {
                            bw.write(content);
                            bw.newLine();
                        }
                    }
                    int processorCount = threadCount;
//                int processorCount = 1;
                    int paramsPerCore = paramsList.size() / processorCount;
                    int[][] paramIDAL = new int[processorCount][2];


                    int runningCount = 0;
                    for (int i = 0; i <= processorCount - 1; i++) {

                        if (i == 0) {
                            paramIDAL[i][0] = 0;
                            paramIDAL[i][1] = paramsPerCore;
                        } else if (i == processorCount - 1) {
                            paramIDAL[i][0] = runningCount;
                            paramIDAL[i][1] = paramsList.size();
                        } else {
                            paramIDAL[i][0] = runningCount;
                            paramIDAL[i][1] = runningCount + paramsPerCore;
                        }
                        runningCount = runningCount + paramsPerCore + 1;
                    }
                    for (int i = 0; i <= processorCount - 1; i++) {
                        XValidate xval = new XValidate(opcorpus, paramIDAL[i]);
                        synchronized (loadLock) {
                            xval.loadThreadApps();
                        }
                        alXValidate.add(xval);
                    }
                    System.out.println("X val ready to go with " + alXValidate.size() + " threads!");
                    this.xvalARit = alXValidate.iterator();
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                    for (int i = 0; i <= processorCount - 1; i++) {
                        newThread nt = new newThread(xvalARit.next());
                        Thread t = new Thread(nt);
                        t.start();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                        }
                    }
                    //once threads are started, set CDL to number of threads and 
                    cdl = new CountDownLatch(processorCount+1);                    
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }                    

                }
            } catch (IOException | ResourceInstantiationException ex) {
                Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (InterruptedException ex) {}
        //count down by one as MLmodelmaker has run its main routine
        cdl.countDown();       
        return null;
    }

    public Integer getProgressThroughParamList() {
        Integer i = 0;

        for (Parameter param : paramsList) {
            if (param.getF1() != null) {
                i++;
            }
        }
        return i;

    }

    public Parameter outerValidation() {
        setBusy(true);
        FileWriter fw2 = null;
        try {
            synchronized (loadLock) {
                loadApps();
            }
            ArrayList<Parameter> newAR = new ArrayList<>();
            newAR.addAll(paramsList);
            File file = new File(resultsFile);
            Collections.sort(newAR, new F1ParameterComparator());
            //reset bestResult to hold comparisons of best models
            double bestResultf1 = 0.0;
            fw2 = new FileWriter(file.getAbsoluteFile() + File.separator + "nestedValidationResults.tsv");
            ArrayList<Prob> probValues;
            TreeMap<String, String> observations;
            try (BufferedWriter bw2 = new BufferedWriter(fw2)) {
                System.out.println("testing best models against unseen corpus");
                String content = "type\tID\tc\tt\td\ttau\tnegex\trmStops\tmissing\tspurious\tcorrect\tP\tR\tF1\tfolds";
                bw2.write(content);
                bw2.newLine();
                probValues = new ArrayList();
                int i = 1;
                observations = new TreeMap();
                for (gate.Document doc : fincorpus) {
                    String keyAnnotID = doc.getName();
                    String ob = doc.getAnnotations(GlobalParameters.KEYANNOTSETNAME).get(GlobalParameters.KEYANNOTSETTYPE).get(0).getFeatures().get(GlobalParameters.MLFEATURENAME).toString();
                    observations.put(keyAnnotID, ob);
                }
                for (Parameter resultParam : newAR) {
                    resultParam.setPrecision(0.0);
                    resultParam.setRecall(0.0);
                    resultParam.setF1(0.0);
                    currentParams = resultParam;
                    resetModel();
                    resultParam.xmlConfigGenerator(MLConfigFile);
                    app.changeFeatureSelection(resultParam);
                    synchronized (loadLock) {
                        app.reinitialisePRs();
                    }
                    app.setTrainingMode("TRAINING");
                    app.getApp().setCorpus(opcorpus);
                    app.getApp().execute();

                    synchronized (loadLock) {
                        app.reinitialisePRs();
                        //reinitialisePRs(applicationApp.getPRs());     
                    }
                    if (multiClassMode) {
                        app.setTrainingMode("APPLICATION_ALL_CLASSES");
                    } else {
                        app.setTrainingMode("APPLICATION_POS_ONLY");
                    }
                    app.getApp().setCorpus(fincorpus);
                    app.getApp().execute();
                    //                 applicationApp.setCorpus(fincorpus);
                    //                 applicationApp.execute();
                    int[] nestedResults = evaluate(fincorpus, true);
                    resultParam.calculateResults(nestedResults[0], nestedResults[1], nestedResults[2]);

                    //calculate if best model when testing more than one
                    if (resultParam.getF1() == bestResultf1) {
                        finalParams = resultParam;
                        System.out.println("Model is equal to best.");

                    } else if (resultParam.getF1() > bestResultf1 && bestResultf1 == 0.0) {
                        finalParams = resultParam;
                        System.out.println("first model tested");
                        bestResultf1 = resultParam.getF1();
                    } else if (resultParam.getF1() > bestResultf1) {
                        finalParams = resultParam;
                        System.out.println("Model is better!");
                        bestResultf1 = resultParam.getF1();
                    }



                    bw2.write(resultParam.getType() + "\t" + resultParam.getID() + "\t" + resultParam.getC() + "\t" + resultParam.getT() + "\t"
                            + resultParam.getD() + "\t" + resultParam.getTau() + "\t" + resultParam.isRUN_NEGEX() + "\t" + resultParam.isRemoveStopWords()
                            + "\t" + nestedResults[0] + "\t" + nestedResults[1] + "\t"
                            + nestedResults[2] + "\t" + resultParam.getPrecision() + "\t" + resultParam.getRecall() + "\t" + resultParam.getF1() + "\t" + resultParam.getFolds());
                    bw2.newLine();
                    bw2.flush();
                    System.out.println("Testing on unseen data done:");
                    System.out.println("Missing = " + nestedResults[0] + ", Spurious = " + nestedResults[1] + ", Correct = " + nestedResults[2]);
                    System.out.println("Precision = " + resultParam.getPrecision() + ", Recall = " + resultParam.getRecall() + ", F1 = " + resultParam.getF1());
                    //capture probabilities
                    synchronized (loadLock) {
                        app.reinitialisePRs();
                    }

                    app.setTrainingMode("APPLICATION_ALL_CLASSES");
                    app.getApp().setCorpus(fincorpus);
                    app.getApp().execute();
                    Prob result = new Prob();
                    result.setModelID(resultParam.getID());
                    for (gate.Document doc : fincorpus) {
                        String keyAnnotID = doc.getName();
                        float prob;
                        if (gate.Utils.getOnlyAnn(doc.getAnnotations("ML")).getFeatures().get("observation").toString().equalsIgnoreCase("positive")) {
                            prob = Float.parseFloat(gate.Utils.getOnlyAnn(doc.getAnnotations("ML")).getFeatures().get("prob").toString());
                        } else {
                            prob = Float.parseFloat(gate.Utils.getOnlyAnn(doc.getAnnotations("ML")).getFeatures().get("prob").toString()) * -1;
                        }
                        result.getMap().put(keyAnnotID, prob);
                    }
                    probValues.add(result);
                    //break after top x models
                    if (i == GlobalParameters.MODELSTOCARRYFORWARD) {
                        break;
                    }
                    i++;
                }
            }
            writeBestModelProbs(probValues, file, observations);
            //finally, set paremeters as best bested
            resetModel();
            app.getApp().setCorpus(null);
        } catch (IOException | ExecutionException | ResourceInstantiationException ex) {
            Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw2.close();
            } catch (IOException ex) {
                Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        setBusy(false);
        return finalParams;
    }

    public void writeBestModelProbs(ArrayList<Prob> probValues, File file, TreeMap<String, String> observations) throws IOException {
        try (BufferedWriter bw3 = new BufferedWriter(new FileWriter(file.getAbsoluteFile() + File.separator + "bestModelProbabilities.tsv"))) {
            System.out.println("outputting probabilites");
            Collections.sort(probValues);
            String probHeader = "annotID\tobservation\t";
            for (Prob result : probValues) {
                probHeader = probHeader + result.getModelID() + "\t";
            }
            bw3.write(probHeader);
            bw3.newLine();
            for (Map.Entry<String, String> entry : observations.entrySet()) {
                String probLine = String.valueOf(entry.getKey()) + "\t" + entry.getValue() + "\t";
                for (Prob result : probValues) {
                    probLine = probLine + String.valueOf(result.getMap().get(entry.getKey())) + "\t";
                }
                bw3.write(probLine);
                bw3.newLine();
            }
        }
    }

    public void cleanUp() {
        setBusy(true);
        Thread.currentThread().interrupt();
        if (this.alXValidate != null) {
            for (XValidate xval : this.alXValidate) {
                if (xval.thread != null) {
                    try {
                        xval.thread.interrupt();
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
            if(Thread.interrupted()){
            try {
                cdl.await(2, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
            }
            }
            Utils.deleteGateResources(loadLock);
        }
        setBusy(false);
    }

    public void setDefaultParameters(Parameter parameters) {
        parameters.xmlConfigGenerator(MLConfigFile);
        app.changeFeatureSelection(parameters);
    }

    private void resetModel() throws IOException {

        //delete model between runs
        File mlModelDirectory = new File(MLConfigFile).getParentFile();
        File learnedModels = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "learnedModels.save");
        File featureVectorsData = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "featureVectorsData.save");
        File LabelsList = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "LabelsList.save");
        File NLPFeatureData = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "NLPFeatureData.save");
        File NLPFeatureList = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "NLPFeatureList.save");
        for (int i = 0; i <= 5; i++) {
            try {
                Thread.sleep(500);
                FileUtils.deleteDirectory(learnedModels);
            } catch (java.io.IOException | InterruptedException ex) {
                System.out.println("Attempting to Cancel");
            }
        }
        featureVectorsData.delete();
        LabelsList.delete();
        NLPFeatureData.delete();
        NLPFeatureList.delete();

    }

    public ArrayList<Corpus> setUpCorpora(Corpus corpus, boolean goldStandard) {
        ArrayList<Corpus> setupCorpus = new ArrayList();
        if (goldStandard) {
            fincorpus = goldStandardCorpus;
            opcorpus = corpus;
            setupCorpus.add(fincorpus);
            setupCorpus.add(opcorpus);
        } else {
            try {
                fincorpus = Factory.newCorpus("OuterCorpus");
                opcorpus = Factory.newCorpus("OptimisationCorpus");
                int pos = 0;
                for (int thisdoc = 0; thisdoc < corpus.size(); thisdoc++) {
                    gate.Document doc1 = corpus.get(thisdoc);
                    if (pos == 0) {
                        fincorpus.add(doc1);
                    } else {
                        opcorpus.add(doc1);
                    }
                    pos++;
                    if (pos >= outerCorpSizeRatio) {
                        pos = 0;
                    }
                }
                System.out.println("setup module outer corpus size = " + fincorpus.size());
                System.out.println("setup module optimization corpus size = " + opcorpus.size());
                setupCorpus.add(fincorpus);
                setupCorpus.add(opcorpus);
            } catch (ResourceInstantiationException ex) {
                System.out.println(ex);
            }
        }
        System.out.println("positive observations in training set " + getPosCount(opcorpus));
        System.out.println("positive observations in testing set " + getPosCount(fincorpus));
        return setupCorpus;
    }

    private int getPosCount(Corpus corpus) {
        int posCount = 0;
        for (gate.Document doc : corpus) {
            for (gate.Annotation annot : doc.getAnnotations("Key").get("ManualAnnotation")) {
                if (annot.getFeatures().get("observation").toString().equalsIgnoreCase("positive")) {
                    posCount++;
                }
            }
        }
        return posCount;
    }

    private int[] evaluate(Corpus corpus, boolean errorAnalysis) {
        int[] results = new int[3];
        int foldmissing = 0;
        int foldspurious = 0;
        int foldcorrect = 0;

        for (gate.Document doc : corpus) {
            AnnotationSet manual = doc.getAnnotations(annSetA).get(type);
            AnnotationSet auto = doc.getAnnotations(annSetB).get(type);
            Set<String> importantFeatures = Collections.singleton(feature);
            AnnotationDiffer differ = new AnnotationDiffer();
            differ.setSignificantFeaturesSet(importantFeatures);
            differ.calculateDiff(manual, auto);  // Key, Response
            Set<Annotation> missingSet = differ.missingAnnotations;
            Set<Annotation> spuriousSet = differ.spuriousAnnotations;
            Set<Annotation> correctSet = differ.correctAnnotations;
            foldmissing += missingSet.size();
            foldspurious += spuriousSet.size();
            foldcorrect += correctSet.size();
            if (errorAnalysis) {
                outputErroneousClassifications(spuriousSet, doc, "spurious");
                outputErroneousClassifications(missingSet, doc, "missing");
            }
        }

        System.out.println("Fold missing: " + foldmissing + ", spurious: " + foldspurious
                + ", correct: " + foldcorrect + ".");

        results[0] = foldmissing;
        results[1] = foldspurious;
        results[2] = foldcorrect;
        return results;
    }

    private void outputErroneousClassifications(Set<Annotation> annotationSet, gate.Document doc, String type) {
        FileWriter fw;
        try {
            File file = new File(resultsFile + File.separator + type + "_annotations.tsv");


            fw = new FileWriter(file, true);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                if (file.length() == 0) {
                    String content = "id\tc\tt\td\ttau\tnegex\trmStops\tstring\tdocName";
                    bw.write(content);
                    bw.newLine();
                }
                for (Annotation annot : annotationSet) {
                    String annotString = gate.Utils.cleanStringFor(doc, annot);
                    bw.write(currentParams.getID() + "\t" + currentParams.getC() + "\t" + currentParams.getT() + "\t"
                            + currentParams.getD() + "\t" + currentParams.getTau() + "\t"
                            + currentParams.isRUN_NEGEX() + "\t" + currentParams.isRemoveStopWords()
                            + "\t" + annotString + "\t" + doc.getName());
                    bw.newLine();
                    bw.flush();

                }
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private static ArrayList<Parameter> parseCmdLineParamFile(File resultsFile) {
        FileReader fr = null;
        ArrayList<Parameter> returnArrayList = new ArrayList();
        try {
            fr = new FileReader(resultsFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            //skiip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] lineArray = line.split("\t");
                Parameter parameters = new Parameter(
                        lineArray[0].toString(),
                        Integer.parseInt(lineArray[1]),
                        Double.parseDouble(lineArray[2]),
                        Integer.parseInt(lineArray[3]),
                        Integer.parseInt(lineArray[4]),
                        Double.parseDouble(lineArray[5]),
                        Boolean.parseBoolean(lineArray[6]),
                        Boolean.parseBoolean(lineArray[7]),
                        //                        Integer.parseInt( lineArray[7]),   
                        //                        Integer.parseInt( lineArray[8]),   
                        //                        Integer.parseInt( lineArray[9]),                           
                        Double.parseDouble(lineArray[11]),
                        Double.parseDouble(lineArray[12]),
                        Double.parseDouble(lineArray[13]),
                        Integer.parseInt(lineArray[14]));

                returnArrayList.add(parameters);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Bad Line");
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (NumberFormatException ex) {
            // wasn't a valid number
            System.out.println(ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            return returnArrayList;
        }
    }

    private static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public void resumeXValidation() {
        setBusy(true);
        try {
            FileReader fr = new FileReader(resultsFile + File.separator + "crossValidationResults.tsv");
            try (BufferedReader br = new BufferedReader(fr)) {
                String line = null;
                String[] lastLine = null;



                while ((line = br.readLine()) != null) {
                    lastLine = line.split("\t");

                }

                if (lastLine[0].equalsIgnoreCase("rough")) {
                    paramsList = Parameter.generateParamList(true, folds);
                } else {
                    paramsList = Parameter.generateParamList(false, folds);
                }

                Iterator<Parameter> it = paramsList.iterator();

                while (it.hasNext()) {
                    if (it.next().getID() <= Integer.parseInt(lastLine[1])) {
                        it.remove();
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void checkBestParamsPopulated() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void prepareForBlastOff() {
        this.alXValidate = new CopyOnWriteArrayList();
        this.allClassGapp = new File(applicationLocation + File.separator + "all_classes.gapp");
        this.MLConfigFile = applicationLocation + File.separator + "MLconfig.xml";
        this.outerCorpSizeRatio = 5;
        if (this.multiClassMode) {
            System.out.println("TextHunter is in MultiClass mode");
        } else {
            System.out.println("TextHunter is in Positive Instance mode");
        }
    }

    public class newThread implements Runnable {

        public XValidate xval;

        newThread(XValidate xval) {
            this.xval = xval;
        }

        @Override
        public void run() {
            xval.execute();

        }
    }

    public class XValidate {

        private File threadTrainingGapp = null;
        private File threadApplicationGapp = null;
        private String threadMLConfigFile;
        public Corpus masterXValCorpus = null;
        private String threadApplicationLocation;
        public Controller threadTrainingApp;
//        public Controller threadApplicationApp;
        public TextHunterMLCCWrapper threadApp;
        //public CorpusController threadAllClassApp;
        int[] IDsToProcess;
        Corpus threadXValCorpus;
        private Parameter currentParams;
        public Thread thread;
        File tempDir;

        XValidate(Corpus xValCorpus, int[] paramIDAL) {
            this.threadMLConfigFile = null;
            this.masterXValCorpus = xValCorpus;
            this.IDsToProcess = paramIDAL;
        }

        private void writeResults(Parameter parameters, int[] results) throws IOException {
            synchronized (writeLock) {
                bestResultF1 = parameters.getF1();
                FileWriter fw = new FileWriter((new File(resultsFile) + File.separator + "crossValidationResults.tsv"), true);
                BufferedWriter bw = new BufferedWriter(fw);

                //write allFoldsResults to file
                bw.write(parameters.getType() + "\t" + parameters.getID() + "\t" + parameters.getC() + "\t" + parameters.getT() + "\t"
                        + parameters.getD() + "\t" + parameters.getTau() + "\t" + parameters.isRUN_NEGEX() + "\t" + parameters.isRemoveStopWords()
                        + "\t" + results[0] + "\t" + results[1] + "\t"
                        + results[2] + "\t" + parameters.getPrecision() + "\t" + parameters.getRecall() + "\t" + parameters.getF1() + "\t" + parameters.getFolds());
                bw.newLine();
                bw.flush();
            }
        }

        private void threadResetModel() {
            try {
                //delete model between runs
                File mlModelDirectory = new File(threadMLConfigFile).getParentFile();
                System.out.println(mlModelDirectory.getCanonicalPath() + " thread ml config loc");
                File learnedModels = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "learnedModels.save");
                File featureVectorsData = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "featureVectorsData.save");
                File LabelsList = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "LabelsList.save");
                File NLPFeatureData = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "NLPFeatureData.save");
                File NLPFeatureList = new File(mlModelDirectory.getAbsolutePath() + File.separator + "savedFiles" + File.separator + "NLPFeatureList.save");
                FileUtils.deleteDirectory(learnedModels);
                featureVectorsData.delete();
                LabelsList.delete();
                NLPFeatureData.delete();
                NLPFeatureList.delete();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

        public void loadThreadApps() {
            try {
                //make temp dir
                File tempAppFolder = new File(resultsFile + File.separator + "temp");
                File applicationFolder = new File(applicationLocation);
                Path path = Files.createTempDirectory(tempAppFolder.toPath(), null);
                tempDir = path.toFile();
                //copy files        
                Utils.copyFolder(applicationFolder, tempDir);
                //set fields
                this.threadApplicationLocation = path.toString();
                this.threadTrainingGapp = new File(threadApplicationLocation + File.separator + "all_classes.gapp");
                this.threadMLConfigFile = threadApplicationLocation + File.separator + "MLconfig.xml";
                threadXValCorpus = Factory.newCorpus("Thread Corpus");
                synchronized (loadLock) {
                    for (gate.Document doc : masterXValCorpus) {
                        gate.Document newDoc = (gate.Document) Factory.duplicate(doc);
                        newDoc.getAnnotations("Key").addAll(doc.getAnnotations("Key"));
                        threadXValCorpus.add(newDoc);
                    }
                this.threadTrainingApp =
                        (CorpusController) PersistenceManager.loadObjectFromFile(threadTrainingGapp);                    
                }


                threadApp = new TextHunterMLCCWrapper(threadTrainingApp);
            } catch (PersistenceException | IOException | ResourceInstantiationException ex) {
                Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void execute() {
            ///normal bit
            this.thread = Thread.currentThread();
            try {
                Iterator<Parameter> it = paramsList.iterator();
                while (it.hasNext()) {
                    Parameter parameters = it.next();
                    System.out.println(parameters.getID() + " vs " + IDsToProcess[0] + " " + IDsToProcess[1]);
                    if (parameters.getID() >= IDsToProcess[0] & parameters.getID() <= IDsToProcess[1] & parameters.getF1() == null) {
                        //for use elsewhere
                        currentParams = parameters;
                        //update SVM XML
                        System.out.println("Adjusting model parameters..." + "C__" + parameters.getC() + "T__" + parameters.getT() + "D__" + parameters.getD() + "TAU__" + parameters.getTau()
                                + "__negex " + parameters.isRUN_NEGEX() + "__rmStops " + parameters.isRemoveStopWords());
                        System.out.println();
                        parameters.xmlConfigGenerator(threadMLConfigFile);
                        threadApp.changeFeatureSelection(parameters);
                        //return final cross validated allFoldsResults
                        System.out.println("Beginning cross validation...");
                        System.out.println();
                        System.out.println();
                        int[] results = crossValidate2();
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                        parameters.calculateResults(results[0], results[1], results[2]);
                        writeResults(parameters, results);
                        //delete model between runs
                        System.out.println("cross validation of Parameter Results done - C = "
                                + parameters.getC() + " T = " + parameters.getT() + " D = " + parameters.getD() + " tau = "
                                + parameters.getTau() + " negex = " + parameters.isRUN_NEGEX() + " rmStops = " + parameters.isRemoveStopWords() + " Total Missing = " + results[0] + " Total Spurious = " + results[1] + " Total Correct = "
                                + results[2] + " precision = " + parameters.getPrecision() + " recall = " + parameters.getRecall()
                                + " f1 = " + parameters.getF1());
                    }
                }
            } catch (IOException | ResourceInstantiationException | ExecutionException ex) {
                Logger.getLogger(MLModelMaker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                System.out.println("Thread " + thread.getName() + " successfully stopped");
            }
            cdl.countDown();
        }

        private int[] crossValidate2() throws ResourceInstantiationException, ExecutionException, gate.creole.ExecutionInterruptedException, InterruptedException {

            int[] allFoldsResults = new int[3];

            for (int fold = 0; fold < folds; fold++) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                Corpus threadtrcorpus = Factory.newCorpus("TrainingCorpus");
                Corpus threadapcorpus = Factory.newCorpus("ApplicationCorpus");
                int pos = fold;
                for (int thisdoc = 0; thisdoc < threadXValCorpus.size(); thisdoc++) {
                    gate.Document doc1 = threadXValCorpus.get(thisdoc);
                    if (pos == 0) {
                        threadapcorpus.add(doc1);
                    } else {
                        threadtrcorpus.add(doc1);
                    }
                    pos++;
                    if (pos >= folds) {
                        pos = 0;
                    }
                }
                System.out.println("positive observations in training set " + getPosCount(threadtrcorpus));
                System.out.println("positive observations in testing set " + getPosCount(threadapcorpus));

                //apply the apps to the corpora
                System.out.println("Fold " + fold + ": Applying training application to " + threadtrcorpus.size() + " documents.");
                System.out.println();
                threadResetModel();
                synchronized (loadLock) {
                    threadApp.reinitialisePRs();
                }
                threadApp.setTrainingMode("TRAINING");
                threadApp.getApp().setCorpus(threadtrcorpus);
                //for some reason, this is needed to prevent crashes with the training mode?
//                synchronized (loadLock) {
//                    Thread.sleep(2000);
//                }
                long startTimeMs = System.currentTimeMillis();
                threadApp.getApp().execute();
                long taskTimeMs = System.currentTimeMillis() - startTimeMs;
                System.out.println("ParamID " + currentParams.getID() + " on Thead " + thread.getName() + " train Mode done in " + (taskTimeMs / 1000) + "s");


                System.out.println("Fold " + fold + ": Applying application application to " + threadapcorpus.size() + " documents.");
                System.out.println();
                System.out.println();
                System.out.println();
                synchronized (loadLock) {
                    threadApp.reinitialisePRs();
                }
                if (multiClassMode) {
                    threadApp.setTrainingMode("APPLICATION_ALL_CLASSES");
                } else {
                    threadApp.setTrainingMode("APPLICATION_POS_ONLY");
                }

                threadApp.getApp().setCorpus(threadapcorpus);
                startTimeMs = System.currentTimeMillis();
                threadApp.getApp().execute();
                taskTimeMs = System.currentTimeMillis() - startTimeMs;
                System.out.println("ParamID " + currentParams.getID() + " on Thead " + thread.getName() + " apply Mode done in " + (taskTimeMs / 1000) + "s");


                //Evaluate - use all folds results array to temp hold results of a single fold. add to total ....
                int[] currentFoldResult = threadEvaluate(threadapcorpus);

                //missing
                allFoldsResults[0] = allFoldsResults[0] + currentFoldResult[0];
                //spurious
                allFoldsResults[1] = allFoldsResults[1] + currentFoldResult[1];
                //correct
                allFoldsResults[2] = allFoldsResults[2] + currentFoldResult[2];
                Factory.deleteResource(threadapcorpus);
                Factory.deleteResource(threadtrcorpus);
            }
            return allFoldsResults;
        }

        private int[] threadEvaluate(Corpus corpus) {
            int[] results = new int[3];
            int foldmissing = 0;
            int foldspurious = 0;
            int foldcorrect = 0;

            for (gate.Document doc : corpus) {
                AnnotationSet manual = doc.getAnnotations(annSetA).get(type);
                AnnotationSet auto = doc.getAnnotations(annSetB).get(type);
                Set<String> importantFeatures = Collections.singleton(feature);
                AnnotationDiffer differ = new AnnotationDiffer();
                differ.setSignificantFeaturesSet(importantFeatures);
                differ.calculateDiff(manual, auto);  // Key, Response
                Set<Annotation> missingSet = differ.missingAnnotations;
                Set<Annotation> spuriousSet = differ.spuriousAnnotations;
                Set<Annotation> correctSet = differ.correctAnnotations;
                foldmissing += missingSet.size();
                foldspurious += spuriousSet.size();
                foldcorrect += correctSet.size();
            }

            System.out.println("Fold missing: " + foldmissing + ", spurious: " + foldspurious
                    + ", correct: " + foldcorrect + ".");

            results[0] = foldmissing;
            results[1] = foldspurious;
            results[2] = foldcorrect;
            return results;
        }
    }
}
