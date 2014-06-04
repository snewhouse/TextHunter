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

import uk.ac.kcl.texthunter.utils.SQLCommands;
import uk.ac.kcl.texthunter.utils.Utils;
import com.google.common.collect.Iterators;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.Resource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.InvalidOffsetException;
import gate.util.persistence.PersistenceManager;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.SwingWorker;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.commons.io.FileUtils;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author rjackson1
 */
public class AnnotationEditor extends javax.swing.JFrame {

    private int firstId;
    private LaunchHunterSWMultithreaded2 launchHunterSWMultithreaded2;
    private int lastID;
    private MLModelMaker ml = MLModelMaker.getInstance();
    private String keyObservation;
    private String keyPriority;
    /**
     * Creates new form SQLEditor2
     */
    public AnnotationEditor() {
        this.projectLoaded = false;
        this.workerProgress = new AtomicInteger();
        try {
            this.relFilePath = new File(".").getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.projectLocation = relFilePath + File.separator + "projects" + File.separator + targetTableName;
        this.appLocation = projectLocation + File.separator + "generic_app";
        this.resultsDir = projectLocation + File.separator + "results";
        this.gateHomeDir = "";
        this.foldNumber = "";
        this.hextext = new HexText();
        initComponents();
        disableCancelButton();
        this.textHunterJTabbedPane.setEnabledAt(4, false);
        this.textHunterJTabbedPane.setEnabledAt(5, false);
        this.textHunterJTabbedPane.setEnabledAt(6, false);
        this.textHunterJTabbedPane.setEnabledAt(7, false);
        this.textHunterJTabbedPane.setEnabledAt(8, false);
        this.textHunterJTabbedPane.setEnabledAt(9, false);
        this.gateHomeDir = relFilePath + File.separator + "resources" + File.separator;
        //change these two to start in Embededd or 
        this.embeddedMode = GlobalParameters.EMBEDDEDMODE;
        if (this.embeddedMode) {
            this.connectToExternalDatabaseCheckBox.setSelected(false);
        } else {
            this.connectToExternalDatabaseCheckBox.setSelected(false);
        }

        changeMode();
        updateInfoTextPane("Welcome to TextHunter");


    }

    private void updateProjectSummary() {
        try {
            projectXML.updateProjectSummary(con, targetTableName);
        }catch (java.sql.SQLException ex){
            updateProjectStatus("Annotation table not ready");
        }
            StyledDocument doc;
            doc = projectSummaryTextPane.getStyledDocument();
            SimpleAttributeSet newString = new SimpleAttributeSet();
            StyleConstants.setForeground(newString, Color.BLACK);
            StyleConstants.setBold(newString, true);

            StringBuilder newText = new StringBuilder("\n");
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();

            newText.append("------ results updated on ").append(dateFormat.format(date)).append(" ------\n\n");
            newText.append("current project annotations:\n");
            newText = newText.append("Gold Standard Positive Annotations = ").append(projectXML.getGsPos()).append("\n");
            newText = newText.append("Gold Standard Negative Annotations = ").append(projectXML.getGsneg()).append("\n");
            newText = newText.append("Gold Standard Unknown Annotations = ").append(projectXML.getGsunk()).append("\n");
            newText = newText.append("Gold Standard Form Annotations = ").append(projectXML.getGsform()).append("\n");
            newText = newText.append("Seed positive Annotations = ").append(projectXML.getSeedpos()).append("\n");
            newText = newText.append("Seed Negative Annotations = ").append(projectXML.getSeedneg()).append("\n");
            newText = newText.append("Seed Unknown Annotations = ").append(projectXML.getSeedunk()).append("\n");
            newText = newText.append("Seed Form Annotations = ").append(projectXML.getSeedform()).append("\n");
            newText = newText.append("AL Positive Annotations = ").append(projectXML.getAlpos()).append("\n");
            newText = newText.append("AL Negative Annotations = ").append(projectXML.getAlneg()).append("\n");
            newText = newText.append("AL Form Annotations = ").append(projectXML.getAlform()).append("\n");
            newText = newText.append("AL Unknown Annotations = ").append(projectXML.getAlunk()).append("\n\n");
            
            newText = newText.append("Last Pipeline run results:\n");
            newText = newText.append("P = ").append(projectXML.getPrecision()).append("\n");
            newText = newText.append("R = ").append(projectXML.getRecall()).append("\n");
            newText = newText.append("F1 = ").append(projectXML.getF1()).append("\n");            

            try {
                doc.insertString(doc.getLength(), "\n" + newText, newString);
            } catch (Exception e) {
                System.out.println(e);
            }
    }

    private void updateInfoTextPane(String newText) {
        StyledDocument doc;
        doc = infoTextPane.getStyledDocument();

        //  Define a keyword attribute

        SimpleAttributeSet newString = new SimpleAttributeSet();
        StyleConstants.setForeground(newString, Color.RED);
        StyleConstants.setBold(newString, true);

        SimpleAttributeSet oldString = new SimpleAttributeSet();
        StyleConstants.setForeground(oldString, Color.BLACK);
        StyleConstants.setBold(oldString, false);

        //  Add some text

        try {
            doc.setCharacterAttributes(0, doc.getLength(), oldString, true);
            doc.insertString(doc.getLength(), "\n" + newText, newString);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void cancelTasks() {
        try {
            ml.setBusy(false);
            if (applyAllSWMultithreaded != null) {
                applyAllSWMultithreaded.cancel(true);
            }
            if (trainAllSW != null) {
                trainAllSW.cancel(true);
            }
            if (xValidateSW != null) {
                xValidateSW.cancel(true);
            }
            if (launchHunterSWMultithreaded2 != null) {
                launchHunterSWMultithreaded2.cancel(true);
            }
        } catch (java.util.concurrent.CancellationException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void outputModeChanged() {
        if (XMLradioButton.isSelected()) {
            withKeyAnnotationsRadioButton.setEnabled(true);
            noKeyAnnotationsRadioButton.setEnabled(true);
            outputFullDocumentsRadioButton.setEnabled(true);
            outputContextOnlyRadioButton.setEnabled(true);
            gateXMLjRadioButton.setEnabled(true);
            knowtatorXMLjRadioButton.setEnabled(true);
        } else {
            withKeyAnnotationsRadioButton.setEnabled(false);
            noKeyAnnotationsRadioButton.setEnabled(false);
            outputFullDocumentsRadioButton.setEnabled(false);
            outputContextOnlyRadioButton.setEnabled(false);
            gateXMLjRadioButton.setEnabled(false);
            knowtatorXMLjRadioButton.setEnabled(false);
        }
    }

    private void enableCancelButton() {
        cancelButton.setEnabled(true);
        cancelButton.setBackground(Color.red);
    }

    private void disableCancelButton() {
        cancelButton.setEnabled(false);
        cancelButton.setBackground(Color.getHSBColor(240, 240, 240));
    }

    private boolean isMultiClass() throws SQLException {
        boolean isMultiClass = false;
        Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = statement.executeQuery(SQLCommands.getInstance().isMultiClass());
        isMultiClass = false;
        while (rs.next()) {
            String classes = rs.getString(1);
            if (!classes.equalsIgnoreCase("positive")
                    & !classes.equalsIgnoreCase("negative")
                    & !classes.equalsIgnoreCase("unknown")
                    & !classes.equalsIgnoreCase("form")) {
                isMultiClass = true;
            }
        }

        return isMultiClass;
    }

    private class GenericSW extends SwingWorker<Void, Integer> {

        String evt;
        boolean success;

        GenericSW(String evt) {
            this.evt = evt;
            this.success = false;
            turnOnIntedeminateProgressBar();
        }

        @Override
        protected Void doInBackground() {
            switch (evt) {
                case "createNewProject":
                    //loadProject doesn't work on XP. posible threading issue    executing on event dispatcher for now                

                    break;
                case "loadProject":
                    //loadProject doesn't work on XP. posible threading issue

                    break;
                case "connectToDatabase":
                    //also execute on event dispatch for threading reasons

                    break;
                case "useActiveLearning":
                    alCheckBoxChangeSW();
                    break;
                case "getAnnotations":
                    getAnnotationsSW();
                    break;
                case "outputXML":
                    try {
                        outputGATEXML();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                default:
                    System.out.println("error" + evt);
                    break;
            }

            return null;
        }

        private void alCheckBoxChangeSW() {
            try {
                alCheckBoxChange();
                this.success = true;
            } catch (SQLException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void getAnnotationsSW() {
            //should be thread safe
            if (con == null) {
                con = connectToDB();
            }
            try {
                resultSet = getResultSet(getInstancesForAnnotationSQL);
                getRowCount();
                resultSet.setFetchSize(1000);
                this.success = true;
            } catch (SQLException ex) {
                if(ex.getMessage().endsWith("no current row.")){
                    infoBox("No Annotations Found","Error" );
                }else{
                    infoBox(ex.getMessage(),"Error" );
                }
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected void done() {
            turnOffIntedeminateProgressBar();
            switch (evt) {
                case "createNewProject":
                    createProjectSW();
                    if (success) {
                        if (embeddedMode) {
                            enableAnnotationSetupJpane();
                            enableSVMAndOutputJPanes();
                            textHunterJTabbedPane.setSelectedIndex(3);
                            textHunterJTabbedPane.setEnabledAt(4, true);
                        }
                    } else {
                        disableAnnotationSetupJpane();
                        disableAnnotationJPanes();
                        disableSVMAndOutputJPanes();
                        textHunterJTabbedPane.setSelectedIndex(2);
                        textHunterJTabbedPane.setEnabledAt(4, false);
                    }
                    break;
                case "loadProject":
                    loadProjectSW();
                    if (success) {
                        if (embeddedMode) {
                            enableAnnotationSetupJpane();
                            enableSVMAndOutputJPanes();
                            textHunterJTabbedPane.setSelectedIndex(3);
                            textHunterJTabbedPane.setEnabledAt(4, true);
                        }
                    } else {
                        disableAnnotationSetupJpane();
                        disableAnnotationJPanes();
                        disableSVMAndOutputJPanes();
                        textHunterJTabbedPane.setSelectedIndex(2);
                        textHunterJTabbedPane.setEnabledAt(4, false);
                    }
                    break;
                case "connectToDatabase":
                    connectToDBSW();
                    if (success) {
                        updateInfoTextPane("External datasource connected");
                        enableAnnotationSetupJpane();
                        enableSVMAndOutputJPanes();
                        DBConnectjDialog.setVisible(false);
                        //textHunterJTabbedPane.setSelectedIndex(3);
                    } else {
                        disableAnnotationSetupJpane();
                        disableAnnotationJPanes();
                        disableSVMAndOutputJPanes();
                    }
                    break;
                case "getAnnotations":
                    if (success) {
                        updateForm();
                        totalRecordsInResultSetTextField.setText(new Integer(rowCount).toString());
                        updateInfoTextPane(rowCount + " annotations retrieved");
                        enableAnnotationJPanes();
                        textHunterJTabbedPane.setSelectedIndex(6);
                    } else {
                        disableAnnotationJPanes();
                    }
                    break;
                default:
                    break;
            }
        }

        private void connectToDBSW() {
            con = connectToDB();
            if (con != null) {
                this.success = true;
            }
        }

        private void loadProjectSW() {
            loadProject();
            this.success = true;

        }

        private void createProjectSW() {
            createNewProject();
            this.success = true;
        }
    }

    private void outputGATEXML() throws SQLException {
        // TODO add your handling code here:
        if (XMLradioButton.isSelected()) {
            Utils.checkGateInit(this.gateHomeDir, GlobalParameters.debugOn);
            Corpus allDocs;
            if (gateXMLjRadioButton.isSelected()) {
                expressModeRadioButton.setEnabled(true);
                changeMode();
                tab2SQLEditorPane.setText(getAnnotationsXMLOutputSQL);
                if (withKeyAnnotationsRadioButton.isSelected() & outputFullDocumentsRadioButton.isSelected()) {
                    allDocs = mkCorpusFromDB2(getAnnotationsXMLOutputSQL, true, true, -1);
                } else if (noKeyAnnotationsRadioButton.isSelected() & outputFullDocumentsRadioButton.isSelected()) {
                    allDocs = mkCorpusFromDB2(getAnnotationsXMLOutputSQL, false, true, -1);
                } else if (withKeyAnnotationsRadioButton.isSelected() & outputContextOnlyRadioButton.isSelected()) {
                    allDocs = mkCorpusFromDB2(getAnnotationsXMLOutputSQL, true, false, -1);
                } else {
                    allDocs = mkCorpusFromDB2(getAnnotationsXMLOutputSQL, false, false, -1);
                }
                int i = 1; //index for doc count
                for (gate.Document doc : allDocs) {
                    try {
                        gate.corpora.DocumentStaxUtils.writeDocument(doc, new File(outputFileChooser.getCurrentDirectory() + File.separator + i + "_" + doc.getName()));
                    } catch (XMLStreamException | IOException ex) {
                        infoBox(ex.getMessage(), "error");
                        Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    i++;
                }
            } else if (knowtatorXMLjRadioButton.isSelected()) {
                allDocs = getContextStringsFromDB(tab2SQLEditorPane.getText());
                File knowtatorAnnotsDir = new File(outputFileChooser.getCurrentDirectory() + "knowtatorAnnots");
                int n = 0;
                if (knowtatorAnnotsDir.exists()) {
                    Object[] options = {"OK",
                        "Cancel"};
                    n = JOptionPane.showOptionDialog(null,
                            "An annotation folder already exists. Do you want to delete it?\n"
                            + projectLocation,
                            "Warning",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                }
                if (n == 0) {
                    for (int i = 0; i <= 5; i++) {
                        try {
                            Thread.sleep(500);
                            FileUtils.deleteDirectory(knowtatorAnnotsDir);
                        } catch (IOException ex) {
                            System.out.println("Attempting deletion " + i);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (knowtatorAnnotsDir.exists()) {
                        try {
                            FileUtils.deleteDirectory(knowtatorAnnotsDir);
                        } catch (IOException ex) {
                            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                knowtatorAnnotsDir.mkdir();
                int instanceSuffix = 1;
                for (gate.Document doc : allDocs) {
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFileChooser.getCurrentDirectory() + File.separator + doc.getName())))) {
                        bw.write(doc.getContent().toString());
                        org.jdom.Document xmlOutDoc = new org.jdom.Document();
                        instanceSuffix++;
                        Utils.buildKnowtatorXMLDocumentFromGATEXML(xmlOutDoc, doc, instanceSuffix, targetTableName);
                        XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
                        xmlOut.output(xmlOutDoc, new FileOutputStream(new File(knowtatorAnnotsDir, doc.getName()
                                + ".knowtator.xml")));

                    } catch (IOException ex) {
                        Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            System.out.println("XML output complete");
        } else {
            try {
                Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery(SQLCommands.getInstance().getResultsForCSV());
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileChooser.getCurrentDirectory() + File.separator + GlobalParameters.OUTPUTTSV));
                ResultSetMetaData rsmd = rs.getMetaData();
                int colCount = rsmd.getColumnCount();
                for (int i = 1; i <= colCount - 1; i++) {
                    bw.write(rsmd.getColumnName(i) + "\t");
                }
                bw.write(rsmd.getColumnName(colCount) + System.lineSeparator());
                bw.flush();
                int flushIndex = 0;
                int index = 0;
                while (rs.next()) {
                    for (int i = 1; i <= colCount - 1; i++) {
                        bw.write(rs.getString(i) + "\t");
                    }
                    bw.write(rs.getString(colCount) + System.lineSeparator());
                    index++;
                    if (index > flushIndex + 50) {
                        bw.flush();
                        flushIndex = index;
                    }
                }
                bw.close();
            } catch (SQLException | IOException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void alCheckBoxChange() throws SQLException {
        if (this.activeLearningRadioButton.isSelected()) {
            setUpActiveLearningTempTable();
        }
    }

    private void annotatorChange(boolean updateDB){
        if(this.annotator1RadioButton.isSelected()){
            this.keyObservation = "keyObservation1";
            this.keyPriority = "keyPriority1";
        }else if(this.annotator2RadioButton.isSelected()){
            this.keyObservation = "keyObservation2";
            this.keyPriority = "keyPriority2";
        }
        if(updateDB){
//            try {
//                updateDatabase();
//            } catch (SQLException ex) {
//                infoBox(ex.getMessage(), "error");
//                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
            updateForm();

                 
        }        
    }
    private void launchHunter() {
        try {
            changeMode();
            Utils.checkGateInit(this.gateHomeDir, GlobalParameters.debugOn);
            directoryForContextHunterTable = HunterDirJFileChooser.getSelectedFile().getCanonicalPath().toString();
            launchHunterSWMultithreaded2 = new LaunchHunterSWMultithreaded2(new File(directoryForContextHunterTable));
            launchHunterSWMultithreaded2.execute();
            this.longRunningProcessInfoTextPane.setText("Starting Hunter");
            this.longRunningProcessDialog.setVisible(true);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex, "InfoBox: " + "ERROR", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateProjectStatus(String message) {
        this.projectSummaryTextPane.setText(message);
    }

    private Connection connectToDB() {
        try {
            Connection returnCon;
            if (embeddedMode) {
                returnCon = Utils.loadAndConnectDerby(con);
                SQLCommands.getInstance().configSQLCommands("derby", targetTableName);
            } else {
                returnCon = getExternalConnection();
                SQLCommands.getInstance().configSQLCommands("sqlserver", targetTableName);
            };
            //infoTextPane.setText("Connection OK!");
            updateProjectStatus("");
            enableAnnotationSetupJpane();
            enableSVMAndOutputJPanes();
            return returnCon;
        } catch (SQLException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "InfoBox: " + "ERROR", JOptionPane.INFORMATION_MESSAGE);
            disableAnnotationSetupJpane();
            disableSVMAndOutputJPanes();
        }
        return null;
    }

    private void enableAnnotationSetupJpane() {
        textHunterJTabbedPane.setEnabledAt(5, true);
    }

    private void disableAnnotationSetupJpane() {
        textHunterJTabbedPane.setEnabledAt(5, false);
    }

    private void enableAnnotationJPanes() {
        textHunterJTabbedPane.setEnabledAt(6, true);
        textHunterJTabbedPane.setEnabledAt(7, true);
    }

    private void disableAnnotationJPanes() {
        textHunterJTabbedPane.setEnabledAt(6, false);
        textHunterJTabbedPane.setEnabledAt(7, false);
    }

    private void enableSVMAndOutputJPanes() {
        textHunterJTabbedPane.setEnabledAt(8, true);
        textHunterJTabbedPane.setEnabledAt(9, true);
    }

    private void disableSVMAndOutputJPanes() {
        textHunterJTabbedPane.setEnabledAt(8, false);
        textHunterJTabbedPane.setEnabledAt(9, false);
    }

    private void saveProject() throws IOException {

        ArrayList<StringTokenizer> stAL = makeArrayListofST(japeKeyPhraseTextArea.getText());
        String targetPattern = "";
        for (StringTokenizer st : stAL) {
            targetPattern = targetPattern + makeJapePattern(st) + "|\n";
        }
        String targetRule = "Imports: { import static gate.Utils.*; }\n"
                + "Phase: quick_jape\n"
                + "Input: Token\n"
                + "Options: control = Appelt\n"
                + "\n"
                + "\n"
                + "Rule: target_keyword\n"
                + "(\n"
                + targetPattern.substring(0, targetPattern.length() - 2) + "\n"
                + "\n"
                + "):match\n"
                + "-->\n"
                + ":match.TargetKeyword = {name = :match@cleanString , TargetKeyword = \"yes\", rule = \"target_keyword\"}\n";

        
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(
                new File(projectLocation + File.separator + "generic_app" + File.separator + "th_jape1.jape")));
        bw.write(targetRule);
        bw.close();        
        
        stAL = makeArrayListofST(japeOtherPhraseTextArea.getText());
        String otherPattern = "";
        for (StringTokenizer st : stAL) {
            otherPattern = otherPattern + makeJapePattern(st) + "|\n";
        }

        String javarulepart1 =  "{\n" +
                            "gate.AnnotationSet othermatch = (gate.AnnotationSet) bindings.get(\"othermatch\");\n" +
                            "gate.Annotation othermatchAnn = (gate.Annotation) othermatch.iterator().next(); \n" +
                            "gate.AnnotationSet targetmatch = (gate.AnnotationSet) bindings.get(\"targetmatch\");\n" +
                            "gate.Annotation targetmatchAnn = (gate.Annotation) targetmatch.iterator().next();  \n" +
                            "FeatureMap features = targetmatchAnn.getFeatures();\n" +
                            "features.put(\"otherRoot\", othermatchAnn.getFeatures().get(\"root\"));\n";

        
        String javarulepart1_1 = "features.put(\"prox\", \"within_comma\");\n" +
                                 "}";        
        String javarulepart1_2 = "features.put(\"prox\", \"within_sentence\");\n" +
                                 "}";                
        if (!japeOtherPhraseTextArea.getText().equalsIgnoreCase("")) {
            targetRule = "Imports: { import static gate.Utils.*; }\n" +
                    "Phase: quick_jape\n" +
                    "Input: Token TargetKeyword Split SpaceToken\n" +
                    "Options: control = Appelt\n\n" +
                    "Macro: OTHER_TERMS\n" +
                    "(" + 
                    otherPattern.substring(0, otherPattern.length() - 2) + "\n" +
                    ")\n\n" +
                    "Rule: other_keyword_before_within_comma\n" +
                    "Priority: 25\n" +
                    "(\n" +
                    "(OTHER_TERMS):othermatch\n" +
                    "({Token,!Split, !Token.root == \",\" , !SpaceToken.kind == \"control\" }\n" +
                    "|\n" +
                    "{ SpaceToken,!Split, !Token.root == \",\" , !SpaceToken.kind == \"control\"  })[0,10]\n" +
                    "({"+GlobalParameters.TARGETKEYWORD +"}):targetmatch\n" +
                    ") -->\n" +
                    javarulepart1 + javarulepart1_1 +                  
                    "\n\nRule: other_keyword_after_within_comma\n" +
                    "Priority: 24\n" +
                    "(\n" +
                    "({"+GlobalParameters.TARGETKEYWORD +"}):targetmatch\n" +
                    "({Token,!Split, !Token.root == \",\" , !SpaceToken.kind == \"control\" }\n" +
                    "|\n" +
                    "{ SpaceToken,!Split, !Token.root == \",\" , !SpaceToken.kind == \"control\"  })[0,10]\n" +
                    "(OTHER_TERMS):othermatch\n" +
                    ") -->\n" +
                    javarulepart1 + javarulepart1_1 +                              
                    "\n\nRule: other_keyword_before_within_sentence\n" +
                    "Priority: 23\n" +
                    "(\n" +
                    "(OTHER_TERMS):othermatch\n" +
                    "({Token , !Split , !SpaceToken.kind == \"control\"}\n" +
                    "|\n" +
                    "{SpaceToken, !Split ,!SpaceToken.kind == \"control\"})[0,30]\n" +
                    "({"+GlobalParameters.TARGETKEYWORD +"}):targetmatch\n" +
                    ") -->\n" +
                    javarulepart1 + javarulepart1_2 +                        
                    "\n\nRule: other_keyword_after_within_sentence\n" +
                    "Priority: 22\n" +
                    "(\n" +
                    "({"+GlobalParameters.TARGETKEYWORD +"}):targetmatch\n" +
                    "({Token , !Split , !SpaceToken.kind == \"control\"}\n" +
                    "|\n" +
                    "{SpaceToken, !Split ,!SpaceToken.kind == \"control\"})[0,30]\n" +
                    " (OTHER_TERMS):othermatch\n" +
                    ") -->\n" +
                    javarulepart1 + javarulepart1_2;                                   
        }else{
            targetRule = "Phase: quick_jape\n"
                    + "Input: Nothing\n"
                    + "Options: control = Once\n"
                    + "Rule: matchNothing\n"
                    + "({Nothing}):nothing\n"
                    + " -->\n"
                    + "{}";

        }

        BufferedWriter bw2 = new BufferedWriter(
                new FileWriter(
                new File(projectLocation + File.separator + "generic_app" + File.separator + "th_jape2.jape")));
        bw2.write(targetRule);
        bw2.close();
        saveXML();
        updateInfoTextPane("Project Saved");
    }

    private void saveXML() {
        String[] wordsArray = makeArrayOfStrings(japeKeyPhraseTextArea.getText());
        projectXML.updateWords(wordsArray, true);
        wordsArray = makeArrayOfStrings(japeOtherPhraseTextArea.getText());
        projectXML.updateWords(wordsArray, false);
        projectXML.updateMetadata(jdbcConnectionTextField.getText(), String.valueOf(!connectToExternalDatabaseCheckBox.isSelected()));
        System.out.println(projectXML.getDoc().getBaseURI());
        try{
            projectXML.updateProjectSummary(con, targetTableName);
        }catch(java.sql.SQLException ex){
            updateProjectStatus("Annotation table not ready");
        }
        projectXML.saveDoc();
    }

    private boolean checkbestParamsPupulated() {

        if (projectXML.getBestParameter() == null) {
            return false;
        } else {
            return true;
        }
    }

    private void turnOnIntedeminateProgressBar() {
        svmProgressBar.setEnabled(true);
        svmProgressBar.setStringPainted(false);
        svmProgressBar.setValue(0);
        svmProgressBar.setIndeterminate(true);
        svmProgressBar.repaint();
    }

    private void turnOffIntedeminateProgressBar() {
        svmProgressBar.setEnabled(false);
        svmProgressBar.setStringPainted(false);
        svmProgressBar.setValue(0);
        svmProgressBar.setIndeterminate(false);
        svmProgressBar.repaint();
    }

    public class Numpad1Action extends AbstractAction {

        public Numpad1Action(
                String desc, Integer mnemonic) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (useNumericKeysCheckBox.isSelected()) {
                keyObsEditorPane.setText("positive");
            }
        }
    }

    public class Numpad0Action extends AbstractAction {

        public Numpad0Action(
                String desc, Integer mnemonic) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (useNumericKeysCheckBox.isSelected()) {
                keyObsEditorPane.setText("negative");
            }
        }
    }

    public class Numpad2Action extends AbstractAction {

        public Numpad2Action(
                String desc, Integer mnemonic) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (useNumericKeysCheckBox.isSelected()) {
                keyObsEditorPane.setText("unknown");
            }
        }
    }

    public class Numpad3Action extends AbstractAction {

        public Numpad3Action(
                String desc, Integer mnemonic) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (useNumericKeysCheckBox.isSelected()) {
                keyObsEditorPane.setText("form");
            }
        }
    }

    public class NumpadPlusAction extends AbstractAction {

        public NumpadPlusAction(
                String desc, Integer mnemonic) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (useNumericKeysCheckBox.isSelected()) {
                updateFormSentenceOnly(hextextIterator.next());
            }
        }
    }

    public class KeyTabToCommentsAction extends AbstractAction {

        public KeyTabToCommentsAction(
                String desc, Integer mnemonic) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            commentsEditorPane.requestFocus();
        }
    }

    public class KeyAltToPrevRecordAction extends AbstractAction {

        public KeyAltToPrevRecordAction(
                String desc, Integer mnemonic) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            prevRecord();
            commentsEditorPane.requestFocus();
        }
    }

    public class CommentsTabToNextRecordAction extends AbstractAction {

        public CommentsTabToNextRecordAction(
                String desc, Integer mnemonic) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            nextRecord();
            keyObsEditorPane.requestFocus();
        }
    }

    public class CommentsAltToKeyAction extends AbstractAction {

        public CommentsAltToKeyAction(
                String desc, Integer mnemonic) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            keyObsEditorPane.requestFocus();
        }
    }

    private Connection getExternalConnection() throws SQLException {
        this.DBConnectjDialog.setVisible(true);
        Connection newCon = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", databaseUserNameTextField.getText());
        char[] password = databasePasswordField.getPassword();
        String passwordString = new String(password);
        connectionProps.put("password", passwordString);
        connectionProps.put("defaultRowPrefetch", "1000");
        connectionProps.put("defaultBatchValue", "1000");
        if (integratedSecurityCheckBox.isSelected()) {
            newCon = DriverManager.getConnection(jdbcConnectionTextField.getText());
        } else {
            newCon = DriverManager.getConnection(jdbcConnectionTextField.getText(), connectionProps);
        }
        newCon.setAutoCommit(false);
        return newCon;
    }

    private int getRowCount() throws SQLException {

        //special field that doesn't change            
        resultSet.last();
        rowCount = new Integer(resultSet.getRow());
        lastID = Integer.parseInt(resultSet.getString("id"));
        resultSet.first();
        firstId = Integer.parseInt(resultSet.getString("id"));
        return rowCount;
    }

    private void getAnnotations() {
        changeMode();
        Utils.checkGateInit(gateHomeDir, GlobalParameters.debugOn);
        hextext.loadHextext();
        System.out.println(getInstancesForAnnotationSQL);
        GenericSW genericSW = new GenericSW("getAnnotations");
        genericSW.execute();
    }

    private boolean createNewProject() {
        try {
            //key files for a project
            unloadProject();
        } catch (SQLException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        String newTargetTableName = Utils.cleanTargetTableName(newProjectJTextField.getText());
        targetTableTextField.setText(newTargetTableName);
        changeMode();
        File app = new File(relFilePath + File.separator + "resources" + File.separator + "generic_app");
        File newApp = new File(projectLocation + File.separator + "generic_app");
        File xml = new File(projectLocation + File.separator + "XML");
        File results = new File(projectLocation + File.separator + "results");
        File newProject = new File(projectLocation);
        // if file doesnt exists, then create it

        int n = 0;
        if (newProject.exists()) {
            Object[] options = {"OK",
                "Cancel"};
            n = JOptionPane.showOptionDialog(null,
                    "A project already exists with this name. Do you want to delete it?\n"
                    + projectLocation,
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
        }
        if (n == 0) {
            for (int i = 0; i <= 5; i++) {
                try {
                    Thread.sleep(500);
                    FileUtils.deleteDirectory(newProject);
                } catch (IOException ex) {
                    System.out.println("Attempting deletion " + i);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (newProject.exists()) {
                try {
                    FileUtils.deleteDirectory(newProject);
                } catch (IOException ex) {
                    Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (newProject.mkdir()) {
            try {
                newApp.mkdir();
                Utils.copyFolder(app, newApp);
                xml.mkdir();
                results.mkdir();
                updateInfoTextPane("Project created");
            } catch (IOException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            updateInfoTextPane("Project is not created");

        }
        ProjectXMLhandler.makeNewProject(targetTableName, projectLocation);
        try {
            projectXML = new ProjectXMLhandler(projectLocation + File.separator + targetTableName + ".xml");
        } catch (FileNotFoundException ex) {
            infoBox("Project " + ex + " not found", "Error");
        } catch (IOException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        embeddedMode = !this.connectToExternalDatabaseCheckBox.isSelected();
        con = connectToDB();
        //check for existence of project table and drop if asked.
        if (embeddedMode) {
            if (Utils.checkForTable(con, targetTableName)) {
                Object[] options = {"OK",
                    "Cancel"};
                n = JOptionPane.showOptionDialog(null,
                        "A table already exists with this name. Do you want to delete it?\n"
                        + "note, this will delete ALL existing manual annotations for this project!"
                        + projectLocation,
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);
                if (n == 0) {
                    Utils.dropTable(con, targetTableName);
                    Utils.dropTable(con, targetTableName + "_DOCUMENTS");
                    Utils.dropIndex(con, targetTableName + "_DOCUMENTS_ID_INDEX");
                    Utils.dropIndex(con, targetTableName + "_ID_INDEX");
                    Utils.dropIndex(con, targetTableName + "_DOC_ID_INDEX");
                    Utils.dropIndex(con, targetTableName + "_CONTEXTSTRING_INDEX");
                    Utils.createFullDocsTable(con, targetTableName);
                    Utils.createAnnotationsTable(con, targetTableName);
                }
            } else {
                Utils.createFullDocsTable(con, targetTableName);
                Utils.createAnnotationsTable(con, targetTableName);
            }
        }
        try {
            saveProject();
            loadProjectJFileChooser.rescanCurrentDirectory();
            this.tableToWorkOnjTextField.setText(this.targetTableName);
            projectLoaded = true;
            updateProjectSummary();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    public static void infoBox(String infoMessage, String location) {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + location, JOptionPane.INFORMATION_MESSAGE);
    }

    public ResultSet getResultSet(String sql) throws SQLException {
        ResultSet newResultSet;
        if (con == null) {
            AnnotationEditor.infoBox("Database not Connected", "Error");
        }
        Statement newStmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        //order AL results
        if (this.activeLearningRadioButton.isSelected()) {
            setUpActiveLearningTempTable();
        }
        cleanUpResultSet();
        newResultSet = newStmt.executeQuery(sql);
        System.out.println("ResultSet retrieved");
        return newResultSet;
    }

    public void getUpdatableResultSet() {
        try {
            Statement stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            updatableResultSet = stmt2.executeQuery("SELECT ID, "+keyObservation+", "+keyPriority+", COMMENTS FROM " + targetTableName
                    + " WHERE ID = " + resultSet.getString("ID"));
            updatableResultSet.first();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    private Corpus getContextStringsFromDB(String sql) {
        try {
            hextext.loadHextext();
            System.out.println("making corpus with: ");
            System.out.println(sql);
            Corpus allDocs = Factory.newCorpus("allDocs");
            resultSet = getResultSet(sql);
            getRowCount();
            ArrayList<String[]> docAnnots = new ArrayList<>();
            resultSet.setFetchSize(1000);
            while (resultSet.next()) {
                String[] currentResults = new String[11];
                currentResults[0] = resultSet.getString(GlobalParameters.CN_DOC_ID);
                currentResults[1] = resultSet.getString(GlobalParameters.SRC_COL);
                currentResults[2] = resultSet.getString(GlobalParameters.KEYOBSERVATION1);
                currentResults[3] = resultSet.getString(GlobalParameters.KEYOBSERVATION2);
                currentResults[7] = resultSet.getString(GlobalParameters.DOCUMENT_DATE);
                currentResults[6] = resultSet.getString(GlobalParameters.SRC_TABLE);
                currentResults[8] = resultSet.getString(GlobalParameters.BRCID);
                currentResults[9] = resultSet.getString(GlobalParameters.ID);
                currentResults[10] = resultSet.getString(GlobalParameters.KEYPRIORITY1);
                currentResults[4] = resultSet.getString(GlobalParameters.CONTEXTSTRING);
                currentResults[5] = String.valueOf(Long.parseLong(resultSet.getString(GlobalParameters.ANNOTSTART)) - Long.parseLong(resultSet.getString(GlobalParameters.CONTEXTSTART)));
                currentResults[6] = String.valueOf(Long.parseLong(resultSet.getString(GlobalParameters.ANNOTEND)) - Long.parseLong(resultSet.getString(GlobalParameters.CONTEXTSTART)));
                String currentDocID = currentResults[0] + "_" + currentResults[1];
                docAnnots.add(currentResults);
                if (resultSet.isAfterLast()) {
                    break;
                } else {
                    //for uses including
                    for (String[] annot : docAnnots) {
                        gate.Document doc = Factory.newDocument(currentResults[4]);
                        FeatureMap fm = Factory.newFeatureMap();
                        fm.put(GlobalParameters.MLFEATURENAME, annot[2]);
                        fm.put("observation2", annot[3]);
                        fm.put("priority", currentResults[10]);
                        String contextString = resultSet.getString("contextString").replace(";;", "\n");
                        hextext.setDoc(contextString);
                        ArrayList<Annotation> hexTextList = new ArrayList<>(hextext.run());
                        Collections.sort(hexTextList, new HexTextComparator((ArrayList<Annotation>) hexTextList));
                        hextextIterator = Iterators.cycle(hexTextList);
                        gate.Annotation htAnnot;
                        do {
                            htAnnot = hextextIterator.next();
                        } while (!htAnnot.getFeatures().get("priority").toString().equalsIgnoreCase(fm.get("priority").toString()));
                        AnnotationSet as2 = doc.getAnnotations(GlobalParameters.KEYANNOTSETNAME);
                        try {
                            //as2.add(Long.parseLong(currentResults[5]),Long.parseLong(currentResults[6]) , GlobalParameters.KEYANNOTSETTYPE, fm);
                            as2.add(gate.Utils.start(htAnnot), gate.Utils.end(htAnnot), GlobalParameters.KEYANNOTSETTYPE, fm);
                        } catch (gate.util.InvalidOffsetException ex) {
                            //change this
                            System.out.println("Error with " + currentResults[0]);
                        }
                        //add metadata
                        doc.getFeatures().put(GlobalParameters.BRCID, currentResults[8]);
                        doc.getFeatures().put(GlobalParameters.CN_DOC_ID, currentResults[0]);
                        doc.getFeatures().put(GlobalParameters.DOCUMENT_DATE, currentResults[7]);
                        doc.getFeatures().put(GlobalParameters.SRC_TABLE, currentResults[6]);
                        doc.getFeatures().put(GlobalParameters.SRC_COL, currentResults[1]);
                        String docName = currentDocID + "_" + currentResults[9] + ".xml";
                        doc.setName(docName);
                        allDocs.add(doc);
                        //Factory.deleteResource(doc);
                    }
                    docAnnots.clear();
                }
            }
            return allDocs;
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        } catch (ResourceInstantiationException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            hextext.unloadHextext();
        }
        return null;
    }

    private Corpus mkCorpusFromDB2(String sql, boolean annotate, boolean fullDocs, int maxCorpusSize) throws SQLException {
        try {
            Corpus allDocs = Factory.newCorpus("allDocs");
            if (sql != null) {
                System.out.println("making corpus with: ");
                System.out.println(sql);
                resultSet = getResultSet(sql);
            }
            //is this needed?
            //System.out.println("Rowcount is " + getRowCount());
            ArrayList<String[]> docAnnots = new ArrayList<>();
            resultSet.setFetchSize(1000);
            int corpusSize = 0;
            //resultSet.first();
            while (resultSet.next()) {
                //resultSet.next();
                String[] currentResults = new String[11];
                String[] nextResults = new String[2];
                currentResults[0] = resultSet.getString(GlobalParameters.CN_DOC_ID);
                currentResults[1] = resultSet.getString(GlobalParameters.SRC_COL);
                currentResults[2] = resultSet.getString(GlobalParameters.KEYOBSERVATION1);
                currentResults[3] = resultSet.getString(GlobalParameters.KEYOBSERVATION2);
                currentResults[7] = resultSet.getString(GlobalParameters.DOCUMENT_DATE);
                currentResults[6] = resultSet.getString(GlobalParameters.SRC_TABLE);
                currentResults[8] = resultSet.getString(GlobalParameters.BRCID);
                currentResults[9] = resultSet.getString(GlobalParameters.ID);
                currentResults[10] = resultSet.getString(GlobalParameters.KEYPRIORITY1);
                if (fullDocs) {
                    currentResults[4] = resultSet.getString(GlobalParameters.TEXTCONTENT).replace(";;", "\n");
                    currentResults[5] = resultSet.getString(GlobalParameters.ANNOTSTART);
                    currentResults[6] = resultSet.getString(GlobalParameters.ANNOTEND);
                    //test to see if next row is same doc. resultset must be ordered by doc
                    if (!resultSet.isLast()) {
                        resultSet.next();
                        nextResults[0] = resultSet.getString(GlobalParameters.CN_DOC_ID);
                        nextResults[1] = resultSet.getString(GlobalParameters.SRC_COL);
                        resultSet.previous();
                    } else {
                        nextResults[0] = null;
                    }
                    String currentDocID = currentResults[0] + "_" + currentResults[1];
                    String nextDocID = nextResults[0] + "_" + nextResults[1];
                    docAnnots.add(currentResults);
                    //if doc changes, add all annotations to new doc
                    if (!currentDocID.equals(nextDocID) | nextResults[0] == null) {
                        gate.Document doc = Factory.newDocument(currentResults[4]);
                        //add metadata
                        doc.getFeatures().put(GlobalParameters.BRCID, currentResults[8]);
                        doc.getFeatures().put(GlobalParameters.CN_DOC_ID, currentResults[0]);
                        doc.getFeatures().put(GlobalParameters.DOCUMENT_DATE, currentResults[7]);
                        doc.getFeatures().put(GlobalParameters.SRC_TABLE, currentResults[6]);
                        doc.getFeatures().put(GlobalParameters.SRC_COL, currentResults[1]);
                        doc.getFeatures().put(GlobalParameters.ID, currentResults[9]);
                        String docName = currentDocID + ".xml";
                        doc.setName(docName);
                        AnnotationSet as = doc.getAnnotations(GlobalParameters.KEYANNOTSETNAME);
                        if (annotate) {
                            for (String[] annot : docAnnots) {
                                FeatureMap fm = Factory.newFeatureMap();
                                FeatureMap fm2 = Factory.newFeatureMap();
                                fm.put(GlobalParameters.MLFEATURENAME, annot[2]);
                                fm.put(GlobalParameters.OBSERVATION2, annot[3]);
                                fm.put(GlobalParameters.PRIORITY, currentResults[10]);
                                as.add(Long.parseLong(annot[5]), Long.parseLong(annot[6]), GlobalParameters.KEYANNOTSETTYPE, fm);
                                as.add(Long.parseLong(annot[5]), Long.parseLong(annot[6]), GlobalParameters.TARGETKEYWORD, fm2);
                            }
                        } else {
                            for (String[] annot : docAnnots) {
                                FeatureMap fm = Factory.newFeatureMap();
                                as.add(Long.parseLong(annot[5]), Long.parseLong(annot[6]), GlobalParameters.TARGETKEYWORD, fm);
                            }
                        }
                        docAnnots.clear();
                        allDocs.add(doc);
                    }
                } else {
                    String currentDocID = currentResults[0] + "_" + currentResults[1];
                    currentResults[4] = resultSet.getString(GlobalParameters.CONTEXTSTRING);
                    currentResults[5] = String.valueOf(Long.parseLong(resultSet.getString(GlobalParameters.ANNOTSTART)) - Long.parseLong(resultSet.getString(GlobalParameters.CONTEXTSTART)));
                    currentResults[6] = String.valueOf(Long.parseLong(resultSet.getString(GlobalParameters.ANNOTEND)) - Long.parseLong(resultSet.getString(GlobalParameters.CONTEXTSTART)));
                    docAnnots.add(currentResults);
                    for (String[] annot : docAnnots) {
                        gate.Document doc = Factory.newDocument(currentResults[4]);
                        //i++;
                        //add metadata
                        doc.getFeatures().put(GlobalParameters.BRCID, currentResults[8]);
                        doc.getFeatures().put(GlobalParameters.CN_DOC_ID, currentResults[0]);
                        doc.getFeatures().put(GlobalParameters.DOCUMENT_DATE, currentResults[7]);
                        doc.getFeatures().put(GlobalParameters.SRC_TABLE, currentResults[6]);
                        doc.getFeatures().put(GlobalParameters.SRC_COL, currentResults[1]);
                        doc.getFeatures().put(GlobalParameters.ID, currentResults[9]);
                        String docName = currentDocID + "_" + currentResults[9] + ".xml";
                        doc.setName(docName);
                        if (annotate) {
                            AnnotationSet as = doc.getAnnotations(GlobalParameters.KEYANNOTSETNAME);
                            FeatureMap fm = Factory.newFeatureMap();
                            fm.put(GlobalParameters.MLFEATURENAME, annot[2]);
                            fm.put(GlobalParameters.OBSERVATION2, annot[3]);
                            fm.put(GlobalParameters.PRIORITY, currentResults[10]);
                            FeatureMap fm2 = Factory.newFeatureMap();
                            try {
                                as.add(Long.parseLong(annot[5]), Long.parseLong(annot[6]), GlobalParameters.KEYANNOTSETTYPE, fm);
                                as.add(Long.parseLong(annot[5]), Long.parseLong(annot[6]), GlobalParameters.TARGETKEYWORD, fm2);
                            } catch (gate.util.InvalidOffsetException ex) {
                                //change this
                                System.out.println("Error with " + currentResults[0]);
                            }
                        } else {
                            AnnotationSet as = doc.getAnnotations(GlobalParameters.KEYANNOTSETNAME);
                            FeatureMap fm = Factory.newFeatureMap();
                            try {
                                as.add(Long.parseLong(annot[5]), Long.parseLong(annot[6]), GlobalParameters.TARGETKEYWORD, fm);
                            } catch (gate.util.InvalidOffsetException ex) {
                                //change this
                                System.out.println("Error with " + currentResults[0]);
                            }
                        }
                        allDocs.add(doc);
                    }
                    docAnnots.clear();
                }
                //System.out.println("Docs Generated = " + i);
                if (corpusSize == maxCorpusSize) {
                    break;
                }
                corpusSize++;
            }
            return allDocs;
        } catch (ResourceInstantiationException | InvalidOffsetException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void quitProgram() {
        int n;
        try {
            if (projectLoaded) {
                System.out.println("quitting program...");
                Object[] options = {"Yes", "No", "Cancel"};
                n = JOptionPane.showOptionDialog(null,
                        "Do you wish to save your project before exiting?\n"
                        + projectLocation,
                        "Warning",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (n == 0) {
                    saveProject();
                    shutDownProcedure();
                    System.exit(0);
                } else if (n == 1) {
                    shutDownProcedure();
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        } catch (IOException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void shutDownProcedure() {
        if (embeddedMode) {
            Utils.shutdownDerby();
        } else if (!embeddedMode) {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (con != null) {
                    con.close();
                }
                System.exit(0);
            } catch (SQLException ex) {
                infoBox(ex.getMessage(), "error");
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void nextRecord() {
        try {
            updateDatabase();
        } catch (SQLException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Long.parseLong(currentRecordNumberTextField.getText()) < Long.parseLong(totalRecordsInResultSetTextField.getText())) {
            try {
                resultSet.next();
                updateForm();
            } catch (SQLException ex) {
                infoBox(ex.getMessage(), "error");
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void prevRecord() {
        try {
            updateDatabase();
        } catch (SQLException ex) {
            infoBox(ex.getMessage(), "error");
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (Long.parseLong(currentRecordNumberTextField.getText()) > 0) {
            try {
                resultSet.previous();
                updateForm();

            } catch (SQLException ex) {
                infoBox(ex.getMessage(), "error");
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    


    private void jumpToRecord() {
        Integer desiredRowNumber = Integer.parseInt(currentRecordNumberTextField.getText());
        try {
            resultSet.absolute(desiredRowNumber);
            updateForm();
        } catch (SQLException ex) {
            infoBox(ex.getMessage(), "error");
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void firstRecord() {
        try {
            updateDatabase();
            resultSet.first();
            updateForm();
        } catch (SQLException ex) {
            infoBox(ex.getMessage(), "error");
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void updateFormSentenceOnly(gate.Annotation annot) {
        try {
            StringBuilder contextWithMarkup = new StringBuilder(resultSet.getString("contextString"));
            //Integer keywordStart = Integer.parseInt(resultSet.getString("annotStart")) - Integer.parseInt(resultSet.getString("contextStart"));
            //Integer keywordEnd = Integer.parseInt(resultSet.getString("annotEnd")) - Integer.parseInt(resultSet.getString("contextStart"));
            Long SentenceStart = annot.getStartNode().getOffset();
            Long SentenceEnd = annot.getEndNode().getOffset();

            Integer keywordStart = Integer.parseInt(resultSet.getString("annotStart")) - Integer.parseInt(resultSet.getString("contextStart"));
            Integer keywordEnd = Integer.parseInt(resultSet.getString("annotEnd")) - Integer.parseInt(resultSet.getString("contextStart"));


            contextWithMarkup = contextWithMarkup.insert(SentenceEnd.intValue(), "</font>");
            contextWithMarkup = contextWithMarkup.insert(keywordEnd, "</b></font>");
            contextWithMarkup = contextWithMarkup.insert(keywordStart, "<font color=\"blue\"><b>");
            contextWithMarkup = contextWithMarkup.insert(SentenceStart.intValue(), "<font color=\"red\">");

            annotationPane.setText(contextWithMarkup.toString().replace("\n", "<br>"));
            keyObsContextCat.setText(annot.getFeatures().get("priority").toString());
            try {
                annotationPane.setCaretPosition(SentenceEnd.intValue());
            } catch (java.lang.IllegalArgumentException ex) {
                annotationPane.setCaretPosition(0);
            }
        } catch (SQLException ex) {
            infoBox(ex.getMessage(), "error");
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateForm() {
        try {
            getUpdatableResultSet();
            String contextString = resultSet.getString("contextString").replace(";;", "\n");
            hextext.setDoc(contextString);
            ArrayList<Annotation> hexTextList = new ArrayList<>(hextext.run());
            Collections.sort(hexTextList, new HexTextComparator((ArrayList<Annotation>) hexTextList));
            hextextIterator = Iterators.cycle(hexTextList);
            if (updatableResultSet.getString(keyPriority) != null) {
                gate.Annotation annot;
                do {
                    annot = hextextIterator.next();
                    updateFormSentenceOnly(annot);
                } while (!annot.getFeatures().get("priority").toString().equalsIgnoreCase(updatableResultSet.getString(keyPriority)));
            } else {
                updateFormSentenceOnly(hextextIterator.next());
            }
            keyObsEditorPane.setText(updatableResultSet.getString(keyObservation));
            commentsEditorPane.setText(updatableResultSet.getString("comments"));
            MLObsEditorPane.setText(resultSet.getString("mlObservation1"));          
            StringBuilder fullTextWithMarkup = new StringBuilder(resultSet.getString("TextContent"));
            fullTextWithMarkup.insert(Integer.parseInt(resultSet.getString("contextEnd")), "</b></font>");
            fullTextWithMarkup.insert(Integer.parseInt(resultSet.getString("contextStart")), "<font color=\"red\"><b>");
            fullTextPane.setText(fullTextWithMarkup.toString().replace("\n", "<br>"));
            brcIDField.setText(resultSet.getString(GlobalParameters.BRCID));
            cnDocIDField.setText(resultSet.getString(GlobalParameters.CN_DOC_ID));
            srcTableField.setText(resultSet.getString("src_table"));
            probEditorPane.setText(resultSet.getString("prob"));
            ////weird bug with derby!     
            if (embeddedMode) {
                resultSet.next();
                resultSet.previous();
            }
            ///
            currentRecordNumberTextField.setText(new Integer(resultSet.getRow()).toString());
            try {
                fullTextPane.setCaretPosition(Integer.parseInt(resultSet.getString("annotStart")));
            } catch (java.lang.IllegalArgumentException ex) {
                fullTextPane.setCaretPosition(0);
            }

        } catch (SQLException ex) {
            infoBox(ex.getMessage(), "error");
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void updateDatabase() throws SQLException {
//        try {
        String keyObs = keyObsEditorPane.getText();
        String comments = commentsEditorPane.getText();
        String id = resultSet.getString("ID");
        String keypriority = keyObsContextCat.getText();
        svmProgressBar.setIndeterminate(true);
        svmProgressBar.setEnabled(true);
        svmProgressBar.setStringPainted(false);
        svmProgressBar.repaint();
        UpdateDatabaseSW updbsw = new UpdateDatabaseSW(id, keyObs, comments, keypriority);
        updbsw.execute();
    }

    private String checkIfNull(String check) {
        if (check == null) {
            check = "";
            return check;
        } else {
            return check;
        }

    }

    private void setUpActiveLearningTempTable() throws SQLException {

        Statement stmt1 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        try {
            stmt1.execute("DROP TABLE " + targetTableName + "_TEMPAL");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("Drop of temp table failed. It probably doesn't exist yet");
        }
        stmt1.execute("select   t1.id into " + targetTableName + "_TEMPAL   \n "
                + " FROM " + targetTableName + " t1 \n "
                + "  inner join \n "
                + " ( \n "
                + " select distinct contextString, min(id) as id from " + targetTableName + " \n "
                + "  group by contextString \n "
                + " ) as b\n "
                + " on t1.contextString = b.contextString\n "
                + " and t1.id = b.id ");


    }

    private void changeMode() {
        annotatorChange(false);
        this.targetTableName = targetTableTextField.getText();
        this.projectLocation = relFilePath + File.separator + "projects" + File.separator + targetTableName;
        this.appLocation = projectLocation + File.separator + "generic_app";
        this.resultsDir = projectLocation + File.separator + "results";
        this.foldNumber = foldNoTextField.getText();
        this.resultsDirTextField.setText(projectLocation + File.separator + "results" + File.separator);
        this.outputFileChooser.setCurrentDirectory(new File(projectLocation + File.separator + "XML" + File.separator));

        if (embeddedMode) {
            SQLCommands.getInstance().configSQLCommands("derby", targetTableName);
        } else {
            SQLCommands.getInstance().configSQLCommands("sqlserver", targetTableName);
        };

        //manage DB type
        if (connectToExternalDatabaseCheckBox.isSelected()) {
            if (embeddedMode) {
                Utils.shutdownDerby();
            }
            embeddedMode = false;
        } else {
            embeddedMode = true;
        }

        //hideBRC fields
        if (embeddedMode) {
            brcIDField.setVisible(false);
            cnDocIDField.setVisible(false);
            srcTableField.setVisible(false);
            activeLearningRadioButton.setEnabled(false);

        } else {
            brcIDField.setVisible(true);
            cnDocIDField.setVisible(true);
            srcTableField.setVisible(true);
            activeLearningRadioButton.setEnabled(true);
        }

        if (advancedModeRadioButton.isSelected()) {
            this.tab1SQLEditorPane.setEnabled(true);
            this.tab2SQLEditorPane.setEnabled(true);
            this.getInstancesForAnnotationSQL = tab1SQLEditorPane.getText();
            this.getAnnotationsForModelTrainingSQL = tab2SQLEditorPane.getText();
            this.getAnnotationsForModelApplicationSQL = tab2SQLEditorPane.getText();
            this.getAnnotationsXMLOutputSQL = tab2SQLEditorPane.getText();
        } else if (expressModeRadioButton.isSelected()) {
            this.tab1SQLEditorPane.setEnabled(false);
            this.tab2SQLEditorPane.setEnabled(false);
            if (this.activeLearningRadioButton.isSelected()) {
                this.getInstancesForAnnotationSQL = SQLCommands.getInstance().getInstancesForActiveLearningAnnotationSQL();
                this.keyObsEditorPane.setForeground(new Color(0, 0, 0));
            } else if (this.goldStandardRadioButton.isSelected()) {
                this.getInstancesForAnnotationSQL = SQLCommands.getInstance().getGoldStandardForAnnotationSQL();
                this.keyObsEditorPane.setBackground(new Color(153, 153, 0));
            } else if (this.modelSeedRadioButton.isSelected()) {
                if (embeddedMode) {
                    this.getInstancesForAnnotationSQL = SQLCommands.getInstance().getInstancesForModelSeedAnnotationSQLDerby();
                } else {
                    this.getInstancesForAnnotationSQL = SQLCommands.getInstance().getInstancesForModelSeedAnnotationSQLMSSQL();
                }
                this.keyObsEditorPane.setForeground(new Color(0, 0, 0));
            } else if (reviewTrainingRadioButton.isSelected()) {
                this.getInstancesForAnnotationSQL = SQLCommands.getInstance().getAllAnnotatedInstancesForReview();
                this.keyObsEditorPane.setForeground(new Color(0, 0, 0));
            } else if(this.getAnnotationsForIAA.isSelected()){
                this.getInstancesForAnnotationSQL = SQLCommands.getInstance().getInstancesForIAA();                
            }
            this.getAnnotationsForModelTrainingSQL = SQLCommands.getInstance().getAllInstancesForFinalModelTraining();
            //maybe this should be WITH (NOLOCK)            
            SQLCommands.getInstance().setApplicationTableName(tableToWorkOnjTextField.getText());
            this.getAnnotationsForModelApplicationSQL = SQLCommands.getInstance().getAllInstancesForModelApplication();
            this.getAnnotationsXMLOutputSQL = SQLCommands.getInstance().getInstancesForXMLOutput();
            this.tab1SQLEditorPane.setText(getInstancesForAnnotationSQL);
            this.tab2SQLEditorPane.setText(getAnnotationsForModelTrainingSQL);
        }
    }

    private void manageSVMButtons() {
        if (trainAllButton.isEnabled() & applyAllButton.isEnabled() & exportResultsButton.isEnabled()) {
            cancelButton.setEnabled(true);
        } else {
            cancelButton.setEnabled(true);
        }


    }

    private static ArrayList<StringTokenizer> makeArrayListofST(String input) {
        ArrayList<String> tempArray = new ArrayList(Arrays.asList(input.split("\n")));
        ArrayList<StringTokenizer> stArray = new ArrayList<StringTokenizer>();

        for (String wordOrPhrase : tempArray) {
            StringTokenizer st = new StringTokenizer(wordOrPhrase);
            stArray.add(st);
        }
        return stArray;
    }

    private static String[] makeArrayOfStrings(String input) {
        String[] array = input.split("\n");
        return array;

    }

    private static String makeJapePattern(StringTokenizer wordOrPhrase) {

        String finalRule = "";
        while (wordOrPhrase.hasMoreTokens()) {
            String currentWord = wordOrPhrase.nextToken();
            String wordRule = "{Token.string =~\"" + currentWord + "\"}";
            finalRule = finalRule + wordRule;
        }
        return finalRule;
    }

    private class ApplyAllSWMultithreaded2 extends SwingWorker<Void, Integer> {

        long startTimeMs;
        int current = 0;
        boolean queryValid;
        double startT = 0;
        Integer progressSum = 0;
        int prev = 0;
        private Thread committerThread;
        private ArrayList<Double> docsPerMinAR;
        private ExecutorService threads;
        private ThreadSafeResultsMover drop;

        ApplyAllSWMultithreaded2() {
            this.docsPerMinAR = new ArrayList();
            this.startTimeMs = System.currentTimeMillis();
            this.committerThread = null;
            this.queryValid = false;
        }

        @Override
        protected Void doInBackground() {
            workerProgress.set(0);
            String preparationQuerySqlSupplement = "";
            String tableToWorkOn = "";
            try {
                if (con == null) {
                    con = connectToDB();
                }
                tableToWorkOn = tableToWorkOnjTextField.getText();

                if (removePrevCheckBox.isSelected()) {
                    PreparedStatement s = con.prepareStatement("UPDATE " + tableToWorkOn
                            + " SET mlObservation1 = null, mlObservation2 = null, prob = null ,"
                            + " numWords = null, MLPRIORITY = null");

                    s.execute();
                    con.commit();
                    System.out.println(tableToWorkOn + " has been reset ");
                }
                if (isCancelled()) {
                    return null;
                }


                if (removePrevCheckBox.isSelected()) {
                    preparationQuerySqlSupplement = " WHERE contextString is not null order by t1.id";
                } else {
                    preparationQuerySqlSupplement = " WHERE mlObservation1 is null AND contextString is not null order by t1.id";
                }

                System.out.println(getAnnotationsForModelApplicationSQL + preparationQuerySqlSupplement);

                try {
                    resultSet = getResultSet(getAnnotationsForModelApplicationSQL + preparationQuerySqlSupplement);
                    getRowCount();
                    this.queryValid = true;
                } catch (SQLException ex) {
                    return null;
                }
                if (isCancelled()) {
                    return null;
                }
                int processorCount = Integer.parseInt(threadCountTextField.getText());
                System.out.println("number of cores detected: " + processorCount);
                pool = new LinkedBlockingQueue<>();
                for (int i = 0; i <= processorCount - 1; i++) {
                    synchronized (lock) {
                        File tempAppFolder = new File(resultsDirTextField.getText() + File.separator + "temp");
                        if (!tempAppFolder.exists()) {
                            tempAppFolder.mkdir();
                        }
                        Path path = Files.createTempDirectory(tempAppFolder.toPath(), null);
                        File tempDir = path.toFile();
                        //copy files        
                        Utils.copyFolder(new File(appLocation), tempDir);
                        String threadApplicationLocation = path.toString();
                        File threadApplicationGapp = new File(threadApplicationLocation + File.separator + "all_classes.gapp");
                        CorpusController newApp = (CorpusController) PersistenceManager.
                                loadObjectFromFile(threadApplicationGapp);
                        TextHunterMLCCWrapper app = new TextHunterMLCCWrapper(newApp);
                        app.setTrainingMode("APPLICATION_ALL_CLASSES");
                        pool.add(newApp);
                    }
                }
                drop = new ThreadSafeResultsMover();
                DBCommitter committer = new DBCommitter("Classify", tableToWorkOn, con, drop);
                committerThread = new Thread(committer);
                committerThread.start();


                resultSet.beforeFirst();
                int docsProcessed = 0;
                threads = Executors.newFixedThreadPool(processorCount);
                do {
                    //prepare microcorpus
                    Corpus microCorpus;
                    synchronized (lock) {
                        microCorpus = mkCorpusFromDB2(null, true, false, GlobalParameters.MAXMICROCORPUSSIZE);
                    }
                    //see if any controllers are available are available
                    try {
                        LargeCorpusWorker lcw = new LargeCorpusWorker(pool.take(), microCorpus, "Classify", pool, drop, lock);
                        threads.execute(lcw);
//                        Thread t = new Thread(lcw);
//                        threads.add(t);
//                        t.start();
                        docsProcessed = docsProcessed + GlobalParameters.MAXMICROCORPUSSIZE;
                        publish(docsProcessed);
                    } catch (java.lang.InterruptedException ex) {
                        return null;
                    }
                } while (!resultSet.isAfterLast());

            } catch (SQLException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        @Override
        protected void done() {
            //add code to cancel instances

            longRunningProcessProgressBar.setEnabled(true);
            longRunningProcessProgressBar.setIndeterminate(true);
            longRunningProcessProgressBar.setStringPainted(false);
            longRunningProcessProgressBar.repaint();
            longRunningProcessProgressBar.setValue(0);
            if (isCancelled()) {
                longRunningProcessInfoTextPane.setText("Aborting application");
            }
            //cleanup
            try {
                if (threads != null) {
                    threads.shutdown();
                    threads.awaitTermination(2, TimeUnit.MINUTES);
                }
                if (drop != null) {
                    drop.setFinalBatch(true);
                }
                if (committerThread != null) {
                    committerThread.join();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (InterruptedException | SQLException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            Utils.deleteGateResources(lock);
            System.out.println("Clean up complete");
            if (isCancelled()) {
                longRunningProcessInfoTextPane.setText("Application aborted");
            } else {
                long taskTimeMs = System.currentTimeMillis() - startTimeMs;
                longRunningProcessInfoTextPane.setText("Application Complete in " + (taskTimeMs / 1000 / 60) + " mins");
            }

            longRunningProcessProgressBar.setEnabled(false);
            longRunningProcessProgressBar.setIndeterminate(false);
            longRunningProcessProgressBar.setStringPainted(false);
            longRunningProcessProgressBar.repaint();
            longRunningProcessProgressBar.setValue(0);
            disableCancelButton();
            applyAllSWMultithreaded = null;
            longRunningProcessInfoTextPane.setText("");
            longRunningProcessDialog.setVisible(false);
        }

        @Override
        protected void process(List<Integer> count) {

            if (!isCancelled()) {
                longRunningProcessProgressBar.setEnabled(true);
                longRunningProcessProgressBar.setStringPainted(true);
                longRunningProcessProgressBar.setMinimum(0);
                longRunningProcessProgressBar.setMaximum(rowCount);
                longRunningProcessProgressBar.setIndeterminate(false);
                int next = count.get(count.size() - 1);
                if (System.currentTimeMillis() >= startT + 5000) {

                    DecimalFormat twoDForm = new DecimalFormat(".#");

                    int docCountThisPeriod = next - prev;
                    double docsPerMin = (double) docCountThisPeriod * 12;
                    docsPerMin = Double.valueOf(twoDForm.format(docsPerMin));
                    docsPerMinAR.add(docsPerMin);
                    double outputDocsPerMinAR = 0;
                    for (Double number : docsPerMinAR) {
                        outputDocsPerMinAR = outputDocsPerMinAR + number;
                    }
                    outputDocsPerMinAR = outputDocsPerMinAR / docsPerMinAR.size();


                    double minsTillCompletion = (rowCount - next) / outputDocsPerMinAR;
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM 'at' HH:mm");
                    Calendar cal = Calendar.getInstance();
                    cal.getTime();
                    cal.add(Calendar.MINUTE, (int) minsTillCompletion);
                    String newTime = df.format(cal.getTime());

                    DecimalFormat myFormat = new DecimalFormat("00.0");
                    String myDoubleString = myFormat.format(outputDocsPerMinAR);

                    longRunningProcessInfoTextPane.setText(next + " instances of " + rowCount + " annotated\n"
                            + myDoubleString + " docs/min\n"
                            + "estimated completion time " + newTime);
                    longRunningProcessProgressBar.setValue(next);
                    longRunningProcessProgressBar.repaint();
                    current = next;
                    startT = System.currentTimeMillis();
                    prev = next;
                }
            }
        }
    }

    private class LaunchHunterSWMultithreaded2 extends SwingWorker<Void, Integer> {

        long startTimeMs;
        int current = 0;
        boolean queryValid;
        double startT = 0;
        Integer progressSum = 0;
        private Thread committerThread;
        private ArrayList<LargeCorpusWorker> instances = new ArrayList<>();
        private File targetDirectory;
        private int totalDocs;
        private ArrayList<Thread> threads;

        LaunchHunterSWMultithreaded2(File targetDirectory) {
            this.startTimeMs = System.currentTimeMillis();
            this.committerThread = null;
            this.queryValid = false;
            this.targetDirectory = targetDirectory;
        }

        @Override
        protected Void doInBackground() {
            workerProgress.set(0);

//            LinkedList<CorpusController>  ccAL = new LinkedList()   ;                        
            try {
                List<File> temp = Arrays.asList(targetDirectory.listFiles());
                ArrayList<File> files = new ArrayList(temp);
                totalDocs = files.size();

                int processorCount = Integer.parseInt(threadCountTextField.getText());
                int docsPerCore = totalDocs / processorCount;
                System.out.println("number of cores detected: " + processorCount);

                pool = new LinkedBlockingQueue<>();
                for (int i = 0; i <= processorCount - 1; i++) {
                    synchronized (lock) {
                        File tempAppFolder = new File(resultsDirTextField.getText() + File.separator + "temp");
                        if (!tempAppFolder.exists()) {
                            tempAppFolder.mkdir();
                        }
                        Path path = Files.createTempDirectory(tempAppFolder.toPath(), null);
                        File tempDir = path.toFile();
                        //copy files        
                        Utils.copyFolder(new File(appLocation), tempDir);
                        String threadApplicationLocation = path.toString();
                        File threadHunterGapp = new File(threadApplicationLocation + File.separator + "hunter.gapp");
                        CorpusController newApp = (CorpusController) PersistenceManager.
                                loadObjectFromFile(threadHunterGapp);

                        pool.add(newApp);
                    }
                }

                int docsProcessed = 0;

                ThreadSafeResultsMover drop = new ThreadSafeResultsMover();
                if (con == null) {
                    con = connectToDB();
                }
                DBCommitter committer = new DBCommitter("Hunter", targetTableName, con, drop);
                committerThread = new Thread(committer);
                committerThread.start();
                Iterator<File> it = files.iterator();
                threads = new ArrayList();
                while (it.hasNext() & !isCancelled()) {
                    File file = it.next();
                    Corpus microCorpus = Factory.newCorpus("microCorpus");
                    for (int i = 0; i <= GlobalParameters.MAXMICROCORPUSSIZE; i++) {
                        try {
                            Document newDoc = Factory.newDocument(file.toURI().toURL());
                            microCorpus.add(newDoc);
                            docsProcessed++;
                            file = it.next();
                        } catch (NullPointerException | java.util.NoSuchElementException ex) {
                            System.out.println(ex);
                            break;
                        } catch (MalformedURLException | ResourceInstantiationException ex) {
                            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    LargeCorpusWorker lcw = new LargeCorpusWorker(pool.take(), microCorpus, "Hunter", pool, drop, lock);
                    Thread t = new Thread(lcw);
                    t.start();
                    threads.add(t);
                    publish(docsProcessed - GlobalParameters.MAXMICROCORPUSSIZE);
                }
                for (Thread t : threads) {
                    t.join();
                }
                drop.setFinalBatch(true);
                committerThread.join();
            } catch (IOException | SQLException | ResourceInstantiationException | PersistenceException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                System.out.println("Stopping Corpus Workers");
            }
            return null;
        }

        public ArrayList<LargeCorpusWorker> getInstances() {
            return instances;
        }

        @Override
        protected void done() {
            //add code to cancel instances

            longRunningProcessProgressBar.setEnabled(true);
            longRunningProcessProgressBar.setIndeterminate(true);
            longRunningProcessProgressBar.setStringPainted(false);
            longRunningProcessProgressBar.repaint();
            longRunningProcessProgressBar.setValue(0);

            if (isCancelled()) {
                longRunningProcessInfoTextPane.setText("Aborting Hunter");
                //first wait for all LCW threads to stop
                for (Thread t : threads) {
                    try {
                        t.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //then interrupt committer
                committerThread.interrupt();
                longRunningProcessInfoTextPane.setText("Hunter Aborted");
            } else {
                long taskTimeMs = System.currentTimeMillis() - startTimeMs;
                updateInfoTextPane("Application Complete in " + (taskTimeMs / 1000 / 60) + " mins");
            }
            // finally cleanup
            synchronized (lock) {
                for (Resource res : Gate.getCreoleRegister().getPublicPrInstances()) {
                    try {
                        System.out.println("Attempting to delete: " + res.getName() + " " + res.getClass());
                        Factory.deleteResource(res);
                    } catch (java.lang.NullPointerException ex) {
                    }
                }

                for (Resource res : Gate.getCreoleRegister().getPublicLrInstances()) {
                    try {
                        System.out.println("Attempting to delete: " + res.getName() + " " + res.getClass());
                        Factory.deleteResource(res);
                    } catch (java.lang.NullPointerException ex) {
                    }
                }
            }
            longRunningProcessProgressBar.setEnabled(false);
            longRunningProcessProgressBar.setIndeterminate(false);
            longRunningProcessProgressBar.setStringPainted(false);
            longRunningProcessProgressBar.repaint();
            longRunningProcessProgressBar.setValue(0);
            disableCancelButton();
            launchHunterSWMultithreaded2 = null;
            longRunningProcessInfoTextPane.setText("");                  
            longRunningProcessDialog.setVisible(false);
            infoBox("Hunter is finished", "Information");
        }

        @Override
        protected void process(List<Integer> count) {

            if (!isCancelled()) {
                longRunningProcessProgressBar.setEnabled(true);
                longRunningProcessProgressBar.setStringPainted(true);
                longRunningProcessProgressBar.setMinimum(0);
                longRunningProcessProgressBar.setMaximum(totalDocs);
                longRunningProcessProgressBar.setIndeterminate(false);
                int next = count.get(count.size() - 1);
                if (next >= (current + 20)) {
                    DecimalFormat twoDForm = new DecimalFormat(".#");
                    double taskT = (System.currentTimeMillis());
                    double first = (taskT - startT) / 1000.0;
                    double second = 60.0 / first;
                    double third = second * 20.0;
                    double docsPerMin = third;
                    docsPerMin = Double.valueOf(twoDForm.format(docsPerMin));
                    double minsTillCompletion = (totalDocs - next) / docsPerMin;
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM 'at' HH:mm");
                    Calendar cal = Calendar.getInstance();
                    cal.getTime();
                    cal.add(Calendar.MINUTE, (int) minsTillCompletion);
                    String newTime = df.format(cal.getTime());


                    longRunningProcessInfoTextPane.setText(next + " instances of " + totalDocs + " annotated\n"
                            + docsPerMin + " docs/min\n"
                            + "estimated completion time " + newTime);
                    longRunningProcessProgressBar.setValue(next);
                    longRunningProcessProgressBar.repaint();
                    current = next;
                    startT = System.currentTimeMillis();
                }
            }
        }
    }

    private class HexText {

        CorpusController hexTextApp;
        File hexTextAppLoc;
        Corpus corpus;
        gate.Document doc;

        HexText() {
        }

        public void setDoc(String textContent) {
            try {
                if (doc != null) {
                    Factory.deleteResource(doc);
                }
                this.doc = Factory.newDocument(textContent);
                addTargetAnnotation();
            } catch (ResourceInstantiationException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void addTargetAnnotation() {
            try {
                String[] currentResults = new String[2];
                currentResults[0] = String.valueOf(Long.parseLong(resultSet.getString("annotStart")) - Long.parseLong(resultSet.getString("contextStart")));
                currentResults[1] = String.valueOf(Long.parseLong(resultSet.getString("annotEnd")) - Long.parseLong(resultSet.getString("contextStart")));
                AnnotationSet as = doc.getAnnotations("");
                FeatureMap fm = Factory.newFeatureMap();

                as.add(Long.parseLong(currentResults[0]), Long.parseLong(currentResults[1]), GlobalParameters.TARGETKEYWORD, fm);
                Annotation test = gate.Utils.getOnlyAnn(as);
                System.out.println(gate.Utils.cleanStringFor(doc, test));
            } catch (SQLException | InvalidOffsetException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public AnnotationSet run() {
            try {
                corpus.add(doc);
                hexTextApp.execute();
                corpus.remove(doc);
                return doc.getAnnotations("").get(GlobalParameters.CONTEXT);
            } catch (ExecutionException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        public void loadHextext() {
            this.hexTextAppLoc = new File(projectLocation + File.separator + "generic_app" + File.separator + "hextext.gapp");
            try {
                this.hexTextApp =
                        (CorpusController) PersistenceManager.loadObjectFromFile(this.hexTextAppLoc);
                this.corpus = Factory.newCorpus("hexTextCorpus");
            } catch (PersistenceException | IOException | ResourceInstantiationException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.hexTextApp.setCorpus(corpus);
            System.out.println("hextext loaded");
        }

        public void unloadHextext() {
            turnOnIntedeminateProgressBar();
            ArrayList<ProcessingResource> PRsList = new ArrayList(hexTextApp.getPRs());
            for (ProcessingResource pr : PRsList) {
                System.out.println(" attempting to delete: " + pr.getName() + " " + pr.getClass());
                Factory.deleteResource(pr);
                System.out.println("Successful");
            }

            Factory.deleteResource(doc);
            Factory.deleteResource(corpus);
            turnOffIntedeminateProgressBar();
            System.out.println("hextext unloaded");
        }
    }

    private void unloadProject() throws SQLException {
        cancelTasks();
        if (resultSet != null) {
            resultSet.close();
        };
        if (con != null) {
            con.close();
        }
        this.textHunterJTabbedPane.setEnabledAt(4, false);
        disableAnnotationSetupJpane();
        disableAnnotationJPanes();
        disableSVMAndOutputJPanes();
    }

    private boolean loadProject() {
        try {
            unloadProject();
        } catch (SQLException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        targetTableTextField.setText(loadProjectJFileChooser.getSelectedFile().getName());
        changeMode();
        try {
            projectXML = new ProjectXMLhandler(projectLocation + File.separator + targetTableName + ".xml");
            japeKeyPhraseTextArea.setText(projectXML.getWords(true));
            japeOtherPhraseTextArea.setText(projectXML.getWords(false));
            jdbcConnectionTextField.setText(projectXML.getsqlConn());
            connectToExternalDatabaseCheckBox.setSelected(!projectXML.getEmbeddedMode());
            this.embeddedMode = projectXML.getEmbeddedMode();
            updateInfoTextPane(targetTableName + " loaded");
            this.tableToWorkOnjTextField.setText(this.targetTableName);
            File tempDir = new File(projectLocation + File.separator + "results" + File.separator + "temp");

            System.out.println(tempDir.getCanonicalPath());
            if (tempDir.exists()) {
                for (int i = 0; i <= 5; i++) {
                    try {
                        System.out.println("Attempting deletion of temp files - " + (i + 1));
                        FileUtils.deleteDirectory(tempDir);
                        if (!tempDir.exists()) {
                            System.out.println("Successful");
                            break;
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
            con = connectToDB();
            this.projectLoaded = true;
            changeMode();
            return true;
        } catch (java.io.FileNotFoundException ex) {
            AnnotationEditor.infoBox("Project " + ex + " not found", "Error");
        } catch (IOException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    private class XValidateSW extends SwingWorker<Void, Integer> {

        Integer idProgress = 0;
        int totalParams;
        long startTimeMs = System.currentTimeMillis();
        private final Object interruptLock = new Object();
        private int gsCorpusSize = 0;
        private int modelCorpusSize = 0;

        private XValidateSW() {
        }

        @Override
        protected Void doInBackground() {
            try {
                if (ml.isBusy()) {
                    throw new MLModelMakerBusyException("ML Model Maker is busy");
                } else {
                    ml.setBusy(true);
                }
                if (con == null) {
                    con = connectToDB();
                }
                if (embeddedMode) {
                    SQLCommands.getInstance().configSQLCommands("derby", targetTableName);
                } else {
                    SQLCommands.getInstance().configSQLCommands("sqlserver", targetTableName);
                };
                if (isCancelled()) {
                    return null;
                }

                Corpus goldStandardCorpus = null;
                Corpus modelTrainingCorpus = null;
                try {
                    goldStandardCorpus = mkCorpusFromDB2(SQLCommands.getInstance().getGoldStandardForCorpusCreation(), true, false, -1);
                    this.gsCorpusSize = goldStandardCorpus.size();
                    if (isCancelled()) {
                        return null;
                    }
                    modelTrainingCorpus = mkCorpusFromDB2(SQLCommands.getInstance().getModelTrainingDataForCorpusCreation(), true, false, -1);
                    this.modelCorpusSize = modelTrainingCorpus.size();
                } catch (SQLException ex) {
                    Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (this.modelCorpusSize < GlobalParameters.minModelCorpusSize
                        | this.gsCorpusSize < GlobalParameters.minGSCorpusSize) {
                    ml.setBusy(false);
                    return null;
                }


                //Corpus allDocs = mkCorpusFromDB2(getAnnotationsForModelTrainingSQL, true, false);
                boolean xValSpeed = xValQADRadioButton.isSelected() ? true : false;
                boolean resumePrev = resumePreviousCheckBox.isSelected() ? true : false;
                threadCount = Integer.parseInt(threadCountTextField.getText());
                boolean isMultiClass = isMultiClass();
//                this.ml = new MLModelMaker(appLocation,
//                        resultsDirTextField.getText(),
//                        Integer.parseInt(foldNumber),
//                        gateHomeDir,
//                        goldStandardCorpus,
//                        modelTrainingCorpus,
//                        xValSpeed,
//                        resumePrev,
//                        threadCount,
//                        projectXML,
//                        isMultiClass);

                ml.setApplicationLocation(appLocation);
                ml.setResultsFile(resultsDirTextField.getText());
                ml.setFolds(Integer.parseInt(foldNumber));
                ml.setGoldStandardCorpus(goldStandardCorpus);
                ml.setTrainingDocs(modelTrainingCorpus);
                ml.setRoughValidation(xValSpeed);
                ml.setResumeXValidation(resumePrev);
                ml.setThreadCount(threadCount);
                ml.setProjectXML(projectXML);
                ml.setMultiClassMode(isMultiClass);
                ml.prepareForBlastOff();

                if (isCancelled()) {
                    return null;
                }
                longRunningProcessProgressBar.setEnabled(true);
                longRunningProcessProgressBar.setStringPainted(true);
                longRunningProcessProgressBar.setMinimum(0);
                longRunningProcessProgressBar.setMaximum(1);
                longRunningProcessProgressBar.setValue(0);
                //MainFrame.getInstance().setVisible(true);
                ml.executeXVal();
                //need to change this to update properly on resume
                totalParams = ml.getParamsList().size();

                while (!isCancelled() & idProgress != totalParams) {
                    idProgress = ml.getProgressThroughParamList();
                    publish(idProgress);
                }
                if (isCancelled()) {
                    return null;
                }
                long taskTimeMs = System.currentTimeMillis() - startTimeMs;
                longRunningProcessProgressBar.setEnabled(true);
                longRunningProcessProgressBar.setIndeterminate(true);
                longRunningProcessProgressBar.setStringPainted(false);
                longRunningProcessInfoTextPane.setText("Xval Complete in " + (taskTimeMs / 1000 / 60) + " mins. Now testing on unseen data");
                Parameter bestParam = ml.outerValidation();
                projectXML.writeBestParams(bestParam);
                longRunningProcessInfoTextPane.setText("Final validation complete. Training best model");
                ml.executeTrainAll();
            } catch (NumberFormatException | NoSuchFieldException | SQLException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MLModelMakerBusyException ex) {
                infoBox(ex.getMessage(), "error");
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        @Override
        protected void process(List<Integer> count) {
            if (!isCancelled()) {
                Integer current = count.get(count.size() - 1);
                longRunningProcessInfoTextPane.setText(current + " models of " + totalParams + " tested");
                longRunningProcessProgressBar.setIndeterminate(false);
                longRunningProcessProgressBar.setMaximum(totalParams);
                longRunningProcessProgressBar.setValue(current);
                longRunningProcessProgressBar.repaint();
            }
        }

        @Override
        protected void done() {
            try {
                if (ml.isBusy()) {
                    throw new MLModelMakerBusyException("ML Model Maker is busy");
                } else if (this.modelCorpusSize < GlobalParameters.minModelCorpusSize | this.gsCorpusSize < GlobalParameters.minGSCorpusSize) {
                    AnnotationEditor.infoBox("You don't have enough annotations in your gold standard /n"
                            + "and/or model training corpus", "Error");
                    Utils.deleteGateResources(lock);
                    longRunningProcessProgressBar.setEnabled(false);
                    longRunningProcessProgressBar.setIndeterminate(false);
                    longRunningProcessProgressBar.setStringPainted(false);
                    longRunningProcessProgressBar.setValue(0);
                    longRunningProcessProgressBar.repaint();
                    updateInfoTextPane("X val Cancelled");
                } else if (isCancelled()) {
                    longRunningProcessInfoTextPane.setText("Cancelling XValidation");
                    longRunningProcessProgressBar.setEnabled(true);
                    longRunningProcessProgressBar.setIndeterminate(true);
                    longRunningProcessProgressBar.setStringPainted(false);
                    longRunningProcessProgressBar.setValue(0);
                    longRunningProcessProgressBar.repaint();
                    ml.cleanUp();
                    longRunningProcessProgressBar.setEnabled(false);
                    longRunningProcessProgressBar.setIndeterminate(false);
                    longRunningProcessProgressBar.setStringPainted(false);
                    longRunningProcessProgressBar.setValue(0);
                    longRunningProcessProgressBar.repaint();
                    updateInfoTextPane("X val Cancelled");
                } else {
                    longRunningProcessInfoTextPane.setText("performing clean up");
                    longRunningProcessProgressBar.setEnabled(true);
                    longRunningProcessProgressBar.setIndeterminate(true);
                    longRunningProcessProgressBar.setStringPainted(false);
                    longRunningProcessProgressBar.setValue(0);
                    longRunningProcessProgressBar.repaint();
                    ml.cleanUp();
                    longRunningProcessProgressBar.setEnabled(false);
                    longRunningProcessProgressBar.setIndeterminate(false);
                    longRunningProcessProgressBar.setStringPainted(false);
                    longRunningProcessProgressBar.setValue(0);
                    longRunningProcessProgressBar.repaint();
                    updateInfoTextPane("unseen data test complete");
                    saveProject();
                    updateProjectSummary();
                }
                disableCancelButton();
            } catch (MLModelMakerBusyException ex) {
            } catch (IOException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            xValidateSW = null;
            longRunningProcessInfoTextPane.setText("");            
            longRunningProcessDialog.setVisible(false);
        }
    }

    private class TrainAllSW extends SwingWorker<Void, Integer> {

        @Override
        protected Void doInBackground() {
            try {
                if (ml.isBusy()) {
                    throw new MLModelMakerBusyException("ML Model Maker is busy");
                } else {
                    ml.setBusy(true);
                }
                Corpus allDocs = mkCorpusFromDB2(getAnnotationsForModelTrainingSQL, true, false, -1);
                threadCount = Integer.parseInt(threadCountTextField.getText());
                boolean isMultiClass = isMultiClass();
                ml = MLModelMaker.getInstance();
                ml.setApplicationLocation(appLocation);
                ml.setResultsFile(resultsDir);
                ml.setFolds(-1);
                ml.setTrainingDocs(allDocs);
                ml.setRoughValidation(true);
                ml.setResumeXValidation(false);
                ml.setThreadCount(threadCount);
                ml.setProjectXML(projectXML);
                ml.setMultiClassMode(isMultiClass);
                ml.prepareForBlastOff();
                ml.loadApps();
                //set final parameters up
                ml.executeTrainAll();
                ml.cleanUp();
            } catch (SQLException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MLModelMakerBusyException ex) {
                infoBox(ex.getMessage(), "error");
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (ml.isBusy()) {
                    throw new MLModelMakerBusyException("ML Model Maker is busy");
                } else {
                    if (!isCancelled()) {
                        updateInfoTextPane("Training Complete");
                    } else {
                        updateInfoTextPane("Training Cancelled");
                    }
                    longRunningProcessProgressBar.setIndeterminate(false);
                    longRunningProcessProgressBar.setEnabled(false);
                    longRunningProcessProgressBar.setStringPainted(false);
                    longRunningProcessProgressBar.repaint();
                    disableCancelButton();
                }
            } catch (MLModelMakerBusyException ex) {
            }
            trainAllSW = null;
            longRunningProcessInfoTextPane.setText("");      
            longRunningProcessDialog.setVisible(false);
        }
    }

    private class UpdateDatabaseSW extends SwingWorker<Void, Integer> {

        String keyObs;
        String comments;
        String id;
        String priority;

        UpdateDatabaseSW(String id, String keyObs, String comments, String priority) {
            this.keyObs = checkIfNull(keyObs);
            this.comments = checkIfNull(comments);
            this.priority = checkIfNull(priority);
            this.id = id;
        }

        @Override
        protected Void doInBackground() {

            try {
                if (con == null) {
                    con = connectToDB();
                }
                String insertTableSQL;
                String goldStandard;
                if (goldStandardRadioButton.isSelected()) {
                    goldStandard = " GOLDSTANDARD = 'gold',  \n";
                } else if (modelSeedRadioButton.isSelected()) {
                    goldStandard = " GOLDSTANDARD = 'seed',  \n";
                } else if (activeLearningRadioButton.isSelected()) {
                    goldStandard = " GOLDSTANDARD = 'al',  \n";
                } else {
                    goldStandard = "  \n";
                }

                insertTableSQL = "UPDATE " + targetTableName + " "
                        + " Set "+keyObservation+" = ?, \n"
                        + " COMMENTS = ?, \n"
                        + goldStandard
                        + " "+keyPriority+" = ?, \n"
                        + " UPDATETIME = " + SQLCommands.getInstance().getCurrentTimeAsString() + "  \n"
                        + "WHERE ID = " + id;

                PreparedStatement preparedStatement;
                preparedStatement = con.prepareStatement(insertTableSQL);
                if (keyObs.equalsIgnoreCase("")) {
                    preparedStatement.setNull(1, Types.LONGVARCHAR);
                } else {
                    preparedStatement.setString(1, keyObs);
                }
                if (comments.equalsIgnoreCase("")) {
                    preparedStatement.setNull(2, Types.LONGVARCHAR);
                } else {
                    preparedStatement.setString(2, comments);
                }

                preparedStatement.setString(3, priority);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                con.commit();

            } catch (SQLException ex) {
                Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
                AnnotationEditor.infoBox("Error updating database. try again?", "Error");
            }
            return null;
        }

        @Override
        protected void done() {
            svmProgressBar.setIndeterminate(false);
            svmProgressBar.setEnabled(false);
            svmProgressBar.setStringPainted(false);
            svmProgressBar.repaint();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modeChangeButtonGroup = new javax.swing.ButtonGroup();
        contextChangeButtonGroup = new javax.swing.ButtonGroup();
        xmlOutPutButtonGroup = new javax.swing.ButtonGroup();
        fullDocsOrContextOnlyButtonGroup = new javax.swing.ButtonGroup();
        xValTypeButtonGroup = new javax.swing.ButtonGroup();
        getAnnotationsButtonGroup = new javax.swing.ButtonGroup();
        xmlOrTableButtonGroup = new javax.swing.ButtonGroup();
        GATEorKnowtatorbuttonGroup = new javax.swing.ButtonGroup();
        DBConnectjDialog = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jdbcConnectionTextField = new javax.swing.JTextField();
        testDBConnectionButton = new javax.swing.JButton();
        databasePasswordField = new javax.swing.JPasswordField();
        databaseUserNameTextField = new javax.swing.JTextField();
        integratedSecurityCheckBox = new javax.swing.JCheckBox();
        longRunningProcessDialog = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        longRunningProcessProgressBar = new javax.swing.JProgressBar();
        cancelButton = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        longRunningProcessInfoTextPane = new javax.swing.JTextPane();
        iAAbuttonGroup = new javax.swing.ButtonGroup();
        textHunterJTabbedPane = new javax.swing.JTabbedPane();
        createNewProjectJPanel = new javax.swing.JPanel();
        createNewProjectButton = new javax.swing.JButton();
        newProjectJTextField = new javax.swing.JTextField();
        connectToExternalDatabaseCheckBox = new javax.swing.JCheckBox();
        loadExistingProjectJPanel = new javax.swing.JPanel();
        loadProjectJFileChooser = new javax.swing.JFileChooser();
        loadProjectButton = new javax.swing.JButton();
        connectToDBJPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        projectSummaryTextPane = new javax.swing.JTextPane();
        specifyKeywordsJPanel = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        japeKeyPhraseTextArea = new javax.swing.JTextArea();
        jScrollPane11 = new javax.swing.JScrollPane();
        japeOtherPhraseTextArea = new javax.swing.JTextArea();
        HunterJPanel = new javax.swing.JPanel();
        HunterDirJFileChooser = new javax.swing.JFileChooser();
        launchHunterTableButton = new javax.swing.JButton();
        setUpDataForAnnotationJPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tab1SQLEditorPane = new javax.swing.JEditorPane();
        getAnnotationsButton = new javax.swing.JButton();
        modelSeedRadioButton = new javax.swing.JRadioButton();
        goldStandardRadioButton = new javax.swing.JRadioButton();
        activeLearningRadioButton = new javax.swing.JRadioButton();
        reviewTrainingRadioButton = new javax.swing.JRadioButton();
        getAnnotationsForIAA = new javax.swing.JRadioButton();
        annotateJPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        annotationPane = new javax.swing.JEditorPane();
        brcIDField = new javax.swing.JTextField();
        cnDocIDField = new javax.swing.JTextField();
        srcTableField = new javax.swing.JTextField();
        probEditorPane = new javax.swing.JEditorPane();
        keyObsEditorPane = new javax.swing.JEditorPane();
        numpad1Action = new Numpad1Action("positive", new Integer(KeyEvent.VK_NUMPAD1));
        numpad0Action = new Numpad0Action("negative", new Integer(KeyEvent.VK_NUMPAD0));
        numpad2Action = new Numpad2Action("unknown", new Integer(KeyEvent.VK_NUMPAD2));
        numpad3Action = new Numpad3Action("form", new Integer(KeyEvent.VK_NUMPAD3));
        numpadPlusAction = new NumpadPlusAction("contextChange", new Integer(KeyEvent.VK_ADD));
        keyAltToPrevRecordAction = new KeyAltToPrevRecordAction("alt", new Integer(KeyEvent.VK_ALT));
        keyTabToCommentsAction = new KeyTabToCommentsAction("tab", new Integer(KeyEvent.VK_TAB));

        keyObsEditorPane.getActionMap().put("numpadPlus",numpadPlusAction);
        keyObsEditorPane.getActionMap().put("numpad1",numpad1Action);
        keyObsEditorPane.getActionMap().put("numpad0", numpad0Action);
        keyObsEditorPane.getActionMap().put("numpad2", numpad2Action);
        keyObsEditorPane.getActionMap().put("numpad3", numpad3Action);
        keyObsEditorPane.getActionMap().put("tab", keyTabToCommentsAction);
        keyObsEditorPane.getActionMap().put("alt",keyAltToPrevRecordAction);

        keyObsEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD,0),"numpadPlus");
        keyObsEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1,0),"numpad1");
        keyObsEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0,0),"numpad0");
        keyObsEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2,0),"numpad2");
        keyObsEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3,0),"numpad3");
        keyObsEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0),"tab");
        keyObsEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,java.awt.event.InputEvent.SHIFT_DOWN_MASK),"alt");
        MLObsEditorPane = new javax.swing.JEditorPane();
        useNumericKeysCheckBox = new javax.swing.JCheckBox();
        keyObsContextCat = new javax.swing.JTextField();
        commentsEditorPane = new javax.swing.JEditorPane();
        commentsAltToKeyAction = new CommentsAltToKeyAction("comments to key", new Integer(KeyEvent.VK_ALT));
        commentsTabToNextRecordAction = new CommentsTabToNextRecordAction("comments to next record", new Integer(KeyEvent.VK_TAB));
        commentsEditorPane.getActionMap().put("tab",commentsTabToNextRecordAction);
        commentsEditorPane.getActionMap().put("alt",commentsAltToKeyAction);
        commentsEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0),"tab");
        commentsEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,java.awt.event.InputEvent.SHIFT_DOWN_MASK),"alt");
        nextRecordButton = new javax.swing.JButton();
        prevRecordButton = new javax.swing.JButton();
        jumpToRecordButton = new javax.swing.JButton();
        firstRecordButton = new javax.swing.JButton();
        currentRecordNumberTextField = new javax.swing.JTextField();
        totalRecordsInResultSetTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        hextextNextContext = new javax.swing.JButton();
        annotator1RadioButton = new javax.swing.JRadioButton();
        annotator2RadioButton = new javax.swing.JRadioButton();
        viewFullDocJPanel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        fullTextPane = new javax.swing.JEditorPane();
        svmConsoleJPanel = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tab2SQLEditorPane = new javax.swing.JEditorPane();
        crossValidateButton = new javax.swing.JButton();
        resultsDirTextField = new javax.swing.JTextField();
        foldNoTextField = new javax.swing.JTextField();
        trainAllButton = new javax.swing.JButton();
        applyAllButton = new javax.swing.JButton();
        xValQADRadioButton = new javax.swing.JRadioButton();
        xValSACRadioButton = new javax.swing.JRadioButton();
        resumePreviousCheckBox = new javax.swing.JCheckBox();
        removePrevCheckBox = new javax.swing.JCheckBox();
        tableToWorkOnjTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        outputConsoleJPanel = new javax.swing.JPanel();
        exportResultsButton = new javax.swing.JButton();
        withKeyAnnotationsRadioButton = new javax.swing.JRadioButton();
        noKeyAnnotationsRadioButton = new javax.swing.JRadioButton();
        outputContextOnlyRadioButton = new javax.swing.JRadioButton();
        outputFullDocumentsRadioButton = new javax.swing.JRadioButton();
        outputFileChooser = new javax.swing.JFileChooser();
        XMLradioButton = new javax.swing.JRadioButton();
        tableRadioButton = new javax.swing.JRadioButton();
        gateXMLjRadioButton = new javax.swing.JRadioButton();
        knowtatorXMLjRadioButton = new javax.swing.JRadioButton();
        licenceJPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        quitButton = new javax.swing.JButton();
        expressModeRadioButton = new javax.swing.JRadioButton();
        advancedModeRadioButton = new javax.swing.JRadioButton();
        svmProgressBar = new javax.swing.JProgressBar();
        threadCountTextField = new javax.swing.JTextField();
        saveProjectJButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        targetTableTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        infoTextPane = new javax.swing.JTextPane();

        DBConnectjDialog.setMinimumSize(new java.awt.Dimension(605, 257));
        DBConnectjDialog.setModal(true);
        DBConnectjDialog.setName("DB connection"); // NOI18N
        DBConnectjDialog.setResizable(false);
        DBConnectjDialog.setType(java.awt.Window.Type.POPUP);

        jdbcConnectionTextField.setText("jdbc:jtds:sqlserver://<ipaddress>:<port>");
        jdbcConnectionTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "JDBC Connection String"));

        testDBConnectionButton.setText("Test database connection");
        testDBConnectionButton.setActionCommand("connectToDatabase");
        testDBConnectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testDBConnectionButtonActionPerformed(evt);
            }
        });

        databasePasswordField.setBorder(javax.swing.BorderFactory.createTitledBorder("Password"));
        databasePasswordField.setEnabled(false);

        databaseUserNameTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "User Name"));
        databaseUserNameTextField.setEnabled(false);

        integratedSecurityCheckBox.setSelected(true);
        integratedSecurityCheckBox.setText("Use Integrated Security?");
        integratedSecurityCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                integratedSecurityCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(integratedSecurityCheckBox)
                    .addComponent(databaseUserNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(databasePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jdbcConnectionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(testDBConnectionButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jdbcConnectionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(testDBConnectionButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(integratedSecurityCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(databaseUserNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(databasePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout DBConnectjDialogLayout = new javax.swing.GroupLayout(DBConnectjDialog.getContentPane());
        DBConnectjDialog.getContentPane().setLayout(DBConnectjDialogLayout);
        DBConnectjDialogLayout.setHorizontalGroup(
            DBConnectjDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 605, Short.MAX_VALUE)
            .addGroup(DBConnectjDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DBConnectjDialogLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        DBConnectjDialogLayout.setVerticalGroup(
            DBConnectjDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
            .addGroup(DBConnectjDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(DBConnectjDialogLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        longRunningProcessDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        longRunningProcessDialog.setMinimumSize(new java.awt.Dimension(605, 257));
        longRunningProcessDialog.setModal(true);
        longRunningProcessDialog.setName("DB connection"); // NOI18N
        longRunningProcessDialog.setResizable(false);
        longRunningProcessDialog.setType(java.awt.Window.Type.POPUP);

        cancelButton.setText("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jScrollPane5.setViewportView(longRunningProcessInfoTextPane);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5)
                    .addComponent(longRunningProcessProgressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(longRunningProcessProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout longRunningProcessDialogLayout = new javax.swing.GroupLayout(longRunningProcessDialog.getContentPane());
        longRunningProcessDialog.getContentPane().setLayout(longRunningProcessDialogLayout);
        longRunningProcessDialogLayout.setHorizontalGroup(
            longRunningProcessDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(longRunningProcessDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        longRunningProcessDialogLayout.setVerticalGroup(
            longRunningProcessDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, longRunningProcessDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("v1.0.0");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        textHunterJTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                textHunterJTabbedPaneStateChanged(evt);
            }
        });

        createNewProjectButton.setText("Create new project!");
        createNewProjectButton.setActionCommand("createNewProject");
        createNewProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewProjectButtonActionPerformed(evt);
            }
        });

        newProjectJTextField.setToolTipText("");
        newProjectJTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("New Project name"));

        connectToExternalDatabaseCheckBox.setText("Project uses external database?");
        connectToExternalDatabaseCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectToExternalDatabaseCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout createNewProjectJPanelLayout = new javax.swing.GroupLayout(createNewProjectJPanel);
        createNewProjectJPanel.setLayout(createNewProjectJPanelLayout);
        createNewProjectJPanelLayout.setHorizontalGroup(
            createNewProjectJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createNewProjectJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(createNewProjectJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newProjectJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(connectToExternalDatabaseCheckBox)
                    .addComponent(createNewProjectButton))
                .addContainerGap(707, Short.MAX_VALUE))
        );
        createNewProjectJPanelLayout.setVerticalGroup(
            createNewProjectJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createNewProjectJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newProjectJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addGap(83, 83, 83)
                .addComponent(connectToExternalDatabaseCheckBox)
                .addGap(97, 97, 97)
                .addComponent(createNewProjectButton)
                .addGap(370, 370, 370))
        );

        textHunterJTabbedPane.addTab("Create new project", createNewProjectJPanel);

        loadProjectJFileChooser.setControlButtonsAreShown(false);
        loadProjectJFileChooser.setCurrentDirectory(new File(relFilePath + File.separator + "projects"));
        loadProjectJFileChooser.setDialogTitle("");
        loadProjectJFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        loadProjectJFileChooser.setRequestFocusEnabled(false);
        loadProjectJFileChooser.setFileFilter(new DirectoryFilter());

        loadProjectButton.setText("Load Project");
        loadProjectButton.setActionCommand("loadProject");
        loadProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadProjectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout loadExistingProjectJPanelLayout = new javax.swing.GroupLayout(loadExistingProjectJPanel);
        loadExistingProjectJPanel.setLayout(loadExistingProjectJPanelLayout);
        loadExistingProjectJPanelLayout.setHorizontalGroup(
            loadExistingProjectJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loadExistingProjectJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loadExistingProjectJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(loadProjectButton)
                    .addComponent(loadProjectJFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(566, Short.MAX_VALUE))
        );
        loadExistingProjectJPanelLayout.setVerticalGroup(
            loadExistingProjectJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loadExistingProjectJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loadProjectJFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadProjectButton)
                .addContainerGap(308, Short.MAX_VALUE))
        );

        textHunterJTabbedPane.addTab("Load existing project", loadExistingProjectJPanel);

        jScrollPane6.setViewportView(projectSummaryTextPane);

        javax.swing.GroupLayout connectToDBJPanelLayout = new javax.swing.GroupLayout(connectToDBJPanel);
        connectToDBJPanel.setLayout(connectToDBJPanelLayout);
        connectToDBJPanelLayout.setHorizontalGroup(
            connectToDBJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connectToDBJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1115, Short.MAX_VALUE)
                .addContainerGap())
        );
        connectToDBJPanelLayout.setVerticalGroup(
            connectToDBJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connectToDBJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE)
                .addContainerGap())
        );

        textHunterJTabbedPane.addTab("Project Overview", connectToDBJPanel);

        specifyKeywordsJPanel.setEnabled(false);
        specifyKeywordsJPanel.setFocusable(false);

        japeKeyPhraseTextArea.setColumns(20);
        japeKeyPhraseTextArea.setRows(5);
        japeKeyPhraseTextArea.setBorder(javax.swing.BorderFactory.createTitledBorder("Key Phrases/Words"));
        jScrollPane10.setViewportView(japeKeyPhraseTextArea);

        japeOtherPhraseTextArea.setColumns(20);
        japeOtherPhraseTextArea.setRows(5);
        japeOtherPhraseTextArea.setBorder(javax.swing.BorderFactory.createTitledBorder("Other Phrases/Words"));
        jScrollPane11.setViewportView(japeOtherPhraseTextArea);

        javax.swing.GroupLayout specifyKeywordsJPanelLayout = new javax.swing.GroupLayout(specifyKeywordsJPanel);
        specifyKeywordsJPanel.setLayout(specifyKeywordsJPanelLayout);
        specifyKeywordsJPanelLayout.setHorizontalGroup(
            specifyKeywordsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(specifyKeywordsJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(specifyKeywordsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addComponent(jScrollPane11))
                .addContainerGap(337, Short.MAX_VALUE))
        );
        specifyKeywordsJPanelLayout.setVerticalGroup(
            specifyKeywordsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(specifyKeywordsJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(218, Short.MAX_VALUE))
        );

        textHunterJTabbedPane.addTab("Specify Keywords", specifyKeywordsJPanel);

        HunterDirJFileChooser.setControlButtonsAreShown(false);
        HunterDirJFileChooser.setCurrentDirectory(new File(relFilePath + File.separator + "projects"));
        HunterDirJFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        launchHunterTableButton.setText("Launch Context Hunter");
        launchHunterTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                launchHunterTableButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout HunterJPanelLayout = new javax.swing.GroupLayout(HunterJPanel);
        HunterJPanel.setLayout(HunterJPanelLayout);
        HunterJPanelLayout.setHorizontalGroup(
            HunterJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HunterJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HunterJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(launchHunterTableButton)
                    .addComponent(HunterDirJFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(566, Short.MAX_VALUE))
        );
        HunterJPanelLayout.setVerticalGroup(
            HunterJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HunterJPanelLayout.createSequentialGroup()
                .addComponent(HunterDirJFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(launchHunterTableButton)
                .addGap(0, 319, Short.MAX_VALUE))
        );

        textHunterJTabbedPane.addTab("Hunter Launcher", HunterJPanel);

        tab1SQLEditorPane.setBorder(javax.swing.BorderFactory.createTitledBorder("SQL"));
        tab1SQLEditorPane.setText("SELECT  t1.* \n" +
            ",t2.TextContent \n"+
            "FROM " + targetTableName + " t1\n" +
            "join GateDB_Cris.dbo.gate t2 on t1.cn_doc_id = t2.CN_Doc_ID and t1.src_table = t2.src_table and t1.src_col = t2.src_col\n" +
            "order by t1.[id]");
        tab1SQLEditorPane.setEnabled(false);
        tab1SQLEditorPane.setMinimumSize(new java.awt.Dimension(5, 5));
        tab1SQLEditorPane.setPreferredSize(new java.awt.Dimension(5, 5));
        tab1SQLEditorPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tab1SQLEditorPaneFocusLost(evt);
            }
        });
        tab1SQLEditorPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tab1SQLEditorPaneKeyTyped(evt);
            }
        });
        jScrollPane3.setViewportView(tab1SQLEditorPane);

        getAnnotationsButton.setText("Get Annotations");
        getAnnotationsButton.setActionCommand("getAnnotations");
        getAnnotationsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getAnnotationsButtonActionPerformed(evt);
            }
        });

        getAnnotationsButtonGroup.add(modelSeedRadioButton);
        modelSeedRadioButton.setText("Create model seed data");
        modelSeedRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelSeedRadioButtonActionPerformed(evt);
            }
        });

        getAnnotationsButtonGroup.add(goldStandardRadioButton);
        goldStandardRadioButton.setSelected(true);
        goldStandardRadioButton.setText("Create gold standard");
        goldStandardRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goldStandardRadioButtonActionPerformed(evt);
            }
        });

        getAnnotationsButtonGroup.add(activeLearningRadioButton);
        activeLearningRadioButton.setText("Create active learning data");
        activeLearningRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                activeLearningRadioButtonActionPerformed(evt);
            }
        });

        getAnnotationsButtonGroup.add(reviewTrainingRadioButton);
        reviewTrainingRadioButton.setText("Review all training data");
        reviewTrainingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reviewTrainingRadioButtonActionPerformed(evt);
            }
        });

        getAnnotationsButtonGroup.add(getAnnotationsForIAA);
        getAnnotationsForIAA.setText("Get Annotations for IAA");

        javax.swing.GroupLayout setUpDataForAnnotationJPanelLayout = new javax.swing.GroupLayout(setUpDataForAnnotationJPanel);
        setUpDataForAnnotationJPanel.setLayout(setUpDataForAnnotationJPanelLayout);
        setUpDataForAnnotationJPanelLayout.setHorizontalGroup(
            setUpDataForAnnotationJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, setUpDataForAnnotationJPanelLayout.createSequentialGroup()
                .addGroup(setUpDataForAnnotationJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(setUpDataForAnnotationJPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(getAnnotationsButton))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(setUpDataForAnnotationJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(activeLearningRadioButton)
                    .addComponent(goldStandardRadioButton)
                    .addComponent(modelSeedRadioButton)
                    .addComponent(reviewTrainingRadioButton)
                    .addComponent(getAnnotationsForIAA))
                .addGap(351, 351, 351))
        );
        setUpDataForAnnotationJPanelLayout.setVerticalGroup(
            setUpDataForAnnotationJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setUpDataForAnnotationJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(setUpDataForAnnotationJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(getAnnotationsButton)
                    .addComponent(goldStandardRadioButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modelSeedRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(activeLearningRadioButton)
                .addGroup(setUpDataForAnnotationJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(setUpDataForAnnotationJPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(setUpDataForAnnotationJPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reviewTrainingRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getAnnotationsForIAA)))
                .addContainerGap(406, Short.MAX_VALUE))
        );

        textHunterJTabbedPane.addTab("Set Up Data For Annotation", setUpDataForAnnotationJPanel);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        annotationPane.setEditable(false);
        annotationPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Annotation"));
        annotationPane.setContentType("text/html"); // NOI18N
        annotationPane.setFocusable(false);
        annotationPane.setMinimumSize(new java.awt.Dimension(90, 40));
        annotationPane.setPreferredSize(new java.awt.Dimension(90, 40));
        jScrollPane1.setViewportView(annotationPane);

        brcIDField.setEditable(false);
        brcIDField.setBorder(javax.swing.BorderFactory.createTitledBorder("BrcId"));
        brcIDField.setFocusable(false);

        cnDocIDField.setEditable(false);
        cnDocIDField.setBorder(javax.swing.BorderFactory.createTitledBorder("CN_Doc_ID"));
        cnDocIDField.setFocusable(false);
        cnDocIDField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cnDocIDFieldActionPerformed(evt);
            }
        });

        srcTableField.setEditable(false);
        srcTableField.setBorder(javax.swing.BorderFactory.createTitledBorder("src_table"));
        srcTableField.setFocusable(false);

        probEditorPane.setEditable(false);
        probEditorPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Probability"));
        probEditorPane.setFocusable(false);

        keyObsEditorPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Key Observation"));
        keyObsEditorPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                keyObsEditorPaneFocusGained(evt);
            }
        });
        keyObsEditorPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                keyObsEditorPaneKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                keyObsEditorPaneKeyTyped(evt);
            }
        });

        MLObsEditorPane.setEditable(false);
        MLObsEditorPane.setBorder(javax.swing.BorderFactory.createTitledBorder("ML Observation"));
        MLObsEditorPane.setFocusable(false);

        useNumericKeysCheckBox.setSelected(true);
        useNumericKeysCheckBox.setText("Use numeric\n keys for\n classes");

        keyObsContextCat.setEditable(false);
        keyObsContextCat.setBorder(javax.swing.BorderFactory.createTitledBorder("Context"));

        commentsEditorPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Comments"));

        nextRecordButton.setText("Next Record (tab)");
        nextRecordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextRecordButtonActionPerformed(evt);
            }
        });

        prevRecordButton.setText("Previous Record\n(shift + tab)");
        prevRecordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevRecordButtonActionPerformed(evt);
            }
        });

        jumpToRecordButton.setText("Jump to Record");
        jumpToRecordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jumpToRecordButtonActionPerformed(evt);
            }
        });

        firstRecordButton.setText("First Record");
        firstRecordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstRecordButtonActionPerformed(evt);
            }
        });

        totalRecordsInResultSetTextField.setEditable(false);
        totalRecordsInResultSetTextField.setText("0");

        jLabel2.setText("of");

        hextextNextContext.setText("nextContext");
        hextextNextContext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hextextNextContextActionPerformed(evt);
            }
        });

        iAAbuttonGroup.add(annotator1RadioButton);
        annotator1RadioButton.setSelected(true);
        annotator1RadioButton.setText("Annotator 1");
        annotator1RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annotator1RadioButtonActionPerformed(evt);
            }
        });

        iAAbuttonGroup.add(annotator2RadioButton);
        annotator2RadioButton.setText("Annotator 2");
        annotator2RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annotator2RadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout annotateJPanelLayout = new javax.swing.GroupLayout(annotateJPanel);
        annotateJPanel.setLayout(annotateJPanelLayout);
        annotateJPanelLayout.setHorizontalGroup(
            annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(annotateJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(annotateJPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(annotateJPanelLayout.createSequentialGroup()
                        .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(brcIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cnDocIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(srcTableField, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(MLObsEditorPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(probEditorPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(keyObsContextCat, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyObsEditorPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(commentsEditorPane, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(annotateJPanelLayout.createSequentialGroup()
                                .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(useNumericKeysCheckBox)
                                    .addComponent(prevRecordButton))
                                .addContainerGap(277, Short.MAX_VALUE))
                            .addGroup(annotateJPanelLayout.createSequentialGroup()
                                .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(annotateJPanelLayout.createSequentialGroup()
                                        .addComponent(nextRecordButton)
                                        .addGap(95, 95, 95)
                                        .addComponent(hextextNextContext))
                                    .addGroup(annotateJPanelLayout.createSequentialGroup()
                                        .addComponent(firstRecordButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jumpToRecordButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(currentRecordNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(totalRecordsInResultSetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(annotateJPanelLayout.createSequentialGroup()
                                        .addComponent(annotator1RadioButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(annotator2RadioButton)))
                                .addGap(0, 53, Short.MAX_VALUE))))))
        );
        annotateJPanelLayout.setVerticalGroup(
            annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(annotateJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(annotateJPanelLayout.createSequentialGroup()
                        .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(keyObsEditorPane, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(MLObsEditorPane, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(brcIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(keyObsContextCat, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(probEditorPane, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(annotateJPanelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(cnDocIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(srcTableField, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, annotateJPanelLayout.createSequentialGroup()
                            .addComponent(useNumericKeysCheckBox)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(nextRecordButton)
                                .addComponent(hextextNextContext))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(prevRecordButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(firstRecordButton)
                                .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(currentRecordNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalRecordsInResultSetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addComponent(jumpToRecordButton))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(annotateJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(annotator1RadioButton)
                                .addComponent(annotator2RadioButton)))
                        .addComponent(commentsEditorPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        textHunterJTabbedPane.addTab("Annotate", annotateJPanel);

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setPreferredSize(new java.awt.Dimension(0, 0));

        fullTextPane.setEditable(false);
        fullTextPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Full Text"));
        fullTextPane.setContentType("text/html"); // NOI18N
        fullTextPane.setToolTipText("");
        fullTextPane.setFocusable(false);
        fullTextPane.setMinimumSize(new java.awt.Dimension(90, 40));
        jScrollPane7.setViewportView(fullTextPane);

        javax.swing.GroupLayout viewFullDocJPanelLayout = new javax.swing.GroupLayout(viewFullDocJPanel);
        viewFullDocJPanel.setLayout(viewFullDocJPanelLayout);
        viewFullDocJPanelLayout.setHorizontalGroup(
            viewFullDocJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewFullDocJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1115, Short.MAX_VALUE)
                .addContainerGap())
        );
        viewFullDocJPanelLayout.setVerticalGroup(
            viewFullDocJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewFullDocJPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE)
                .addContainerGap())
        );

        textHunterJTabbedPane.addTab("View Full Doc", viewFullDocJPanel);

        tab2SQLEditorPane.setBorder(javax.swing.BorderFactory.createTitledBorder("SQL"));
        tab2SQLEditorPane.setText("SELECT  *\n"+
            "FROM "+targetTableName+ "\n"+
            "where keyObservation1 is not null");
        tab2SQLEditorPane.setEnabled(false);
        tab2SQLEditorPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tab2SQLEditorPaneFocusLost(evt);
            }
        });
        jScrollPane8.setViewportView(tab2SQLEditorPane);

        crossValidateButton.setText("Build Models");
        crossValidateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crossValidateButtonActionPerformed(evt);
            }
        });

        resultsDirTextField.setText(relFilePath + File.separator + targetTableName + File.separator + "results" + File.separator);
        resultsDirTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("Results Directory Location"));

        foldNoTextField.setText("10");
        foldNoTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("Fold Number"));

        trainAllButton.setText("Train With All Data");
        trainAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainAllButtonActionPerformed(evt);
            }
        });

        applyAllButton.setText("Apply Best Model to All Instances");
        applyAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyAllButtonActionPerformed(evt);
            }
        });

        xValTypeButtonGroup.add(xValQADRadioButton);
        xValQADRadioButton.setSelected(true);
        xValQADRadioButton.setText("Quick and Dirty");

        xValTypeButtonGroup.add(xValSACRadioButton);
        xValSACRadioButton.setText("Slow and Clean");

        resumePreviousCheckBox.setText("Resume previous X validation?");
        resumePreviousCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumePreviousCheckBoxActionPerformed(evt);
            }
        });

        removePrevCheckBox.setText("Remove prev?");

        jLabel3.setText("Table to work on");

        javax.swing.GroupLayout svmConsoleJPanelLayout = new javax.swing.GroupLayout(svmConsoleJPanel);
        svmConsoleJPanel.setLayout(svmConsoleJPanelLayout);
        svmConsoleJPanelLayout.setHorizontalGroup(
            svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(resultsDirTextField)
                    .addComponent(jScrollPane8)
                    .addComponent(foldNoTextField, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 380, Short.MAX_VALUE)
                .addComponent(crossValidateButton)
                .addGap(18, 18, 18)
                .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resumePreviousCheckBox)
                    .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                        .addComponent(xValQADRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xValSACRadioButton)))
                .addGap(320, 320, 320))
            .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(trainAllButton))
                    .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                                .addComponent(applyAllButton)
                                .addGap(11, 11, 11)
                                .addComponent(removePrevCheckBox))
                            .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                                .addGap(239, 239, 239)
                                .addComponent(tableToWorkOnjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        svmConsoleJPanelLayout.setVerticalGroup(
            svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76)
                .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                        .addComponent(resultsDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(foldNoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                                .addComponent(trainAllButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(applyAllButton)
                                    .addComponent(removePrevCheckBox))
                                .addGap(61, 61, 61))
                            .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tableToWorkOnjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))))
                    .addGroup(svmConsoleJPanelLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(crossValidateButton)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, svmConsoleJPanelLayout.createSequentialGroup()
                                .addComponent(resumePreviousCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(svmConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(xValQADRadioButton)
                                    .addComponent(xValSACRadioButton))))))
                .addContainerGap(195, Short.MAX_VALUE))
        );

        textHunterJTabbedPane.addTab("SVM console", svmConsoleJPanel);

        exportResultsButton.setText("Export Results");
        exportResultsButton.setActionCommand("outputXML");
        exportResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportResultsButtonActionPerformed(evt);
            }
        });

        xmlOutPutButtonGroup.add(withKeyAnnotationsRadioButton);
        withKeyAnnotationsRadioButton.setSelected(true);
        withKeyAnnotationsRadioButton.setText("With Key Annotations");
        withKeyAnnotationsRadioButton.setEnabled(false);

        xmlOutPutButtonGroup.add(noKeyAnnotationsRadioButton);
        noKeyAnnotationsRadioButton.setText("No Key Annotations");
        noKeyAnnotationsRadioButton.setEnabled(false);

        fullDocsOrContextOnlyButtonGroup.add(outputContextOnlyRadioButton);
        outputContextOnlyRadioButton.setSelected(true);
        outputContextOnlyRadioButton.setText("Context Only");
        outputContextOnlyRadioButton.setEnabled(false);

        fullDocsOrContextOnlyButtonGroup.add(outputFullDocumentsRadioButton);
        outputFullDocumentsRadioButton.setText("Full Documents");
        outputFullDocumentsRadioButton.setEnabled(false);

        outputFileChooser.setControlButtonsAreShown(false);
        outputFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        xmlOrTableButtonGroup.add(XMLradioButton);
        XMLradioButton.setText("XML");
        XMLradioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XMLradioButtonActionPerformed(evt);
            }
        });

        xmlOrTableButtonGroup.add(tableRadioButton);
        tableRadioButton.setSelected(true);
        tableRadioButton.setText("CSV table");

        GATEorKnowtatorbuttonGroup.add(gateXMLjRadioButton);
        gateXMLjRadioButton.setSelected(true);
        gateXMLjRadioButton.setText("GATE XML");
        gateXMLjRadioButton.setEnabled(false);

        GATEorKnowtatorbuttonGroup.add(knowtatorXMLjRadioButton);
        knowtatorXMLjRadioButton.setText("Knowtator XML");
        knowtatorXMLjRadioButton.setEnabled(false);

        javax.swing.GroupLayout outputConsoleJPanelLayout = new javax.swing.GroupLayout(outputConsoleJPanel);
        outputConsoleJPanel.setLayout(outputConsoleJPanelLayout);
        outputConsoleJPanelLayout.setHorizontalGroup(
            outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputConsoleJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(outputFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(outputConsoleJPanelLayout.createSequentialGroup()
                        .addGap(169, 169, 169)
                        .addComponent(XMLradioButton)
                        .addGap(169, 169, 169)
                        .addComponent(tableRadioButton))
                    .addGroup(outputConsoleJPanelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(withKeyAnnotationsRadioButton)
                            .addComponent(outputFullDocumentsRadioButton)
                            .addComponent(gateXMLjRadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(outputConsoleJPanelLayout.createSequentialGroup()
                                .addGroup(outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(outputContextOnlyRadioButton)
                                    .addComponent(noKeyAnnotationsRadioButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(exportResultsButton))
                            .addComponent(knowtatorXMLjRadioButton))))
                .addContainerGap(566, Short.MAX_VALUE))
        );
        outputConsoleJPanelLayout.setVerticalGroup(
            outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputConsoleJPanelLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(outputFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(outputConsoleJPanelLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(tableRadioButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, outputConsoleJPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(XMLradioButton)))
                .addGroup(outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(outputConsoleJPanelLayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(exportResultsButton))
                    .addGroup(outputConsoleJPanelLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(gateXMLjRadioButton)
                            .addComponent(knowtatorXMLjRadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(withKeyAnnotationsRadioButton)
                            .addComponent(noKeyAnnotationsRadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(outputConsoleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(outputFullDocumentsRadioButton)
                            .addComponent(outputContextOnlyRadioButton))))
                .addContainerGap(138, Short.MAX_VALUE))
        );

        textHunterJTabbedPane.addTab("Output console", outputConsoleJPanel);

        jTextPane1.setEditable(false);
        jTextPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.setText("<html>\r\n  <head>\r\n\r\n  </head>\r\n  <body>\r\n    <p style=\"margin: 10px 5px\";align=\"justify\">\r\n<br><br>Copyright (C) 2014  Richard Jackson (richgjackson@yahoo.co.uk)\n<br><br>This program is free software: you can redistribute it and/or modify\nit under the terms of the GNU General Public License as published by\nthe Free Software Foundation, either version 3 of the License, or\n(at your option) any later version.\n<br><br>This program is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU General Public License for more details.\n<br><br>You should have received a copy of the GNU General Public License\nalong with this program.  If not, see <a href=\"url\">http:www.gnu.org/licenses/</a>. \r\n    </p>\r\n  </body>\r\n</html>\r\n");
        jScrollPane12.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 795, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout licenceJPanelLayout = new javax.swing.GroupLayout(licenceJPanel);
        licenceJPanel.setLayout(licenceJPanelLayout);
        licenceJPanelLayout.setHorizontalGroup(
            licenceJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(licenceJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(310, Short.MAX_VALUE))
        );
        licenceJPanelLayout.setVerticalGroup(
            licenceJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(licenceJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(322, Short.MAX_VALUE))
        );

        textHunterJTabbedPane.addTab("Licence", licenceJPanel);

        getContentPane().add(textHunterJTabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 121, 1140, 750));
        textHunterJTabbedPane.getAccessibleContext().setAccessibleName("Annotate");

        quitButton.setText("Quit");
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });
        getContentPane().add(quitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 10, -1, -1));

        modeChangeButtonGroup.add(expressModeRadioButton);
        expressModeRadioButton.setSelected(true);
        expressModeRadioButton.setText("Express Mode");
        expressModeRadioButton.setFocusable(false);
        expressModeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expressModeRadioButtonActionPerformed(evt);
            }
        });
        getContentPane().add(expressModeRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, -1, -1));

        modeChangeButtonGroup.add(advancedModeRadioButton);
        advancedModeRadioButton.setText("Advanced Mode");
        advancedModeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedModeRadioButtonActionPerformed(evt);
            }
        });
        getContentPane().add(advancedModeRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, -1));

        svmProgressBar.setEnabled(false);
        svmProgressBar.setFocusable(false);
        getContentPane().add(svmProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 10, 150, 23));

        threadCountTextField.setText(Integer.toString(Runtime.getRuntime().availableProcessors()));
        threadCountTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Cores used"));
        getContentPane().add(threadCountTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 80, 86));

        saveProjectJButton.setText("Save Project");
        saveProjectJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectJButtonActionPerformed(evt);
            }
        });
        getContentPane().add(saveProjectJButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 10, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/ac/kcl/texthunter/resources/textHunter.jpg"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        targetTableTextField.setEditable(false);
        targetTableTextField.setText("(no project loaded)");
        targetTableTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("")), "Current Project"));
        getContentPane().add(targetTableTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 40, 320, 60));

        jScrollPane2.setViewportView(infoTextPane);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 300, 80));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cleanUpResultSet() {
        try {
            if (resultSet != null) {
                resultSet.close();

            }
        } catch (SQLException ex) {
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
        quitProgram();
    }//GEN-LAST:event_quitButtonActionPerformed

    private void expressModeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expressModeRadioButtonActionPerformed
        // TODO add your handling code here:
        changeMode();
    }//GEN-LAST:event_expressModeRadioButtonActionPerformed

    private void advancedModeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedModeRadioButtonActionPerformed
        if(advancedModeRadioButton.isSelected()){
            infoBox("Advanced mode allows you to write custom SQL to retrieve/annotate"
                    + "\nspecific subsets of annotations. However, you may need to understand"
                    + "\nsome of the internal workings of TextGunter to prevent unintended consequences!", "Warning");
        }
        changeMode();
    }//GEN-LAST:event_advancedModeRadioButtonActionPerformed

    private void connectToExternalDatabaseCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectToExternalDatabaseCheckBoxActionPerformed
        if (connectToExternalDatabaseCheckBox.isSelected()) {
            infoBox("TextHunter can connect to MS SQL Server 2008 R2 and later.\n"
                    + "This tends to be a more efficient method of managing very large corpora \n"
                    + "(>50 000 documents. However, it requires a different mode of operation that\n"
                    + "isn't well documented. If you are interested in getting this feature implemented,\n"
                    + "give me a shout! richard.g.jackson@slam.nhs.uk", "Warning");
            if (embeddedMode) {
                Utils.shutdownDerby();
            }
        }
        changeMode();
    }//GEN-LAST:event_connectToExternalDatabaseCheckBoxActionPerformed

    private void createNewProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewProjectButtonActionPerformed

//        projectLoaded = createNewProject();
//        if (projectLoaded) {
//            if (embeddedMode) {
//                textHunterJTabbedPane.setSelectedIndex(3);
//            } else {
//                textHunterJTabbedPane.setSelectedIndex(2);
//            }
//        }

        String actionPerformed = evt.getActionCommand();
        GenericSW genericSW = new GenericSW(actionPerformed);
        genericSW.execute();
    }//GEN-LAST:event_createNewProjectButtonActionPerformed

    private void loadProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadProjectButtonActionPerformed
//        if (loadProject()) {
//            if (embeddedMode) {
//                textHunterJTabbedPane.setSelectedIndex(3);
//            } else {
//                textHunterJTabbedPane.setSelectedIndex(2);
//            }
//        }

        String actionPerformed = evt.getActionCommand();
        GenericSW genericSW = new GenericSW(actionPerformed);
        genericSW.execute();
    }//GEN-LAST:event_loadProjectButtonActionPerformed

    private void resumePreviousCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumePreviousCheckBoxActionPerformed
        // TODO add your handling code here:
        if (resumePreviousCheckBox.isSelected()) {
            foldNoTextField.setEnabled(false);
            xValQADRadioButton.setEnabled(false);
            xValSACRadioButton.setEnabled(false);
        } else {
            foldNoTextField.setEnabled(true);
            xValQADRadioButton.setEnabled(true);
            xValSACRadioButton.setEnabled(true);
        }
    }//GEN-LAST:event_resumePreviousCheckBoxActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancelTasks();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void applyAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyAllButtonActionPerformed
        try {
            if (applyAllSWMultithreaded != null) {
                throw new MLModelMakerBusyException("ML Model Maker is busy");
            } else if (checkbestParamsPupulated()) {
                enableCancelButton();
                Utils.checkGateInit(relFilePath + File.separator + "resources" + File.separator, GlobalParameters.debugOn);
                manageSVMButtons();
                modelSeedRadioButton.setSelected(true);
                changeMode();
                applyAllSWMultithreaded = new ApplyAllSWMultithreaded2();
                applyAllSWMultithreaded.execute();
                this.longRunningProcessDialog.setVisible(true);
                longRunningProcessInfoTextPane.setText("Setting up classifier");
            } else {
                AnnotationEditor.infoBox("Cannot apply best model - xvalidation not completed", "Error");
            }
        } catch (MLModelMakerBusyException ex) {
            infoBox(ex.getMessage(), "Error");
        }
    }//GEN-LAST:event_applyAllButtonActionPerformed

    private void trainAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainAllButtonActionPerformed
        try {
            if (trainAllSW != null) {
                throw new MLModelMakerBusyException("ML Model Maker is busy");
            } else if (checkbestParamsPupulated()) {
                enableCancelButton();

                Utils.checkGateInit(relFilePath + File.separator + "resources" + File.separator, GlobalParameters.debugOn);
                manageSVMButtons();
                changeMode();
                longRunningProcessProgressBar.setEnabled(true);
                longRunningProcessProgressBar.setIndeterminate(true);
                longRunningProcessProgressBar.setStringPainted(false);
                longRunningProcessProgressBar.repaint();
                longRunningProcessInfoTextPane.setText("Training started...");
                trainAllSW = new TrainAllSW();
                trainAllSW.execute();
                this.longRunningProcessDialog.setVisible(true);
            } else {
                AnnotationEditor.infoBox("Cannot train best model - xvalidation not completed", "Error");
            }
        } catch (MLModelMakerBusyException ex) {
            infoBox(ex.getMessage(), "Error");
        }
    }//GEN-LAST:event_trainAllButtonActionPerformed

    private void exportResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportResultsButtonActionPerformed

        String actionPerformed = evt.getActionCommand();
        GenericSW genericSW = new GenericSW(actionPerformed);
        genericSW.execute();

    }//GEN-LAST:event_exportResultsButtonActionPerformed

    private void crossValidateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crossValidateButtonActionPerformed
        try {
            longRunningProcessProgressBar.setEnabled(true);
            longRunningProcessProgressBar.setIndeterminate(true);
            longRunningProcessProgressBar.setStringPainted(false);
            if (xValidateSW != null) {
                throw new MLModelMakerBusyException("ML Model Maker is busy");
            } else {
                enableCancelButton();
                Utils.checkGateInit(relFilePath + File.separator + "resources" + File.separator, GlobalParameters.debugOn);
                changeMode();
                longRunningProcessInfoTextPane.setText("Setting up model building environment");
                xValidateSW = new XValidateSW();
                xValidateSW.execute();
                this.longRunningProcessDialog.setVisible(true);
            }
        } catch (MLModelMakerBusyException ex) {
            infoBox(ex.getMessage(), "error");
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_crossValidateButtonActionPerformed

    private void tab2SQLEditorPaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tab2SQLEditorPaneFocusLost
        getAnnotationsForModelTrainingSQL = tab2SQLEditorPane.getText();
    }//GEN-LAST:event_tab2SQLEditorPaneFocusLost

    private void getAnnotationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getAnnotationsButtonActionPerformed
        getAnnotations();
    }//GEN-LAST:event_getAnnotationsButtonActionPerformed

    private void tab1SQLEditorPaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tab1SQLEditorPaneFocusLost
        changeMode();
    }//GEN-LAST:event_tab1SQLEditorPaneFocusLost

    private void saveProjectJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProjectJButtonActionPerformed
        try {
            saveProject();
        } catch (IOException ex) {
            infoBox(ex.getMessage(), "error");
        }
    }//GEN-LAST:event_saveProjectJButtonActionPerformed

    private void launchHunterTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_launchHunterTableButtonActionPerformed

        try {
            if (launchHunterSWMultithreaded2 != null) {
                throw new MLModelMakerBusyException("Hunter is busy");
            } else {
                enableCancelButton();
                launchHunter();
            }
        } catch (MLModelMakerBusyException ex) {
            infoBox(ex.getMessage(), "error");
            Logger.getLogger(AnnotationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_launchHunterTableButtonActionPerformed

    private void textHunterJTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_textHunterJTabbedPaneStateChanged

        System.out.println(textHunterJTabbedPane.getSelectedIndex());
        if (projectLoaded) {
            if (resultSet != null) {
                if ((textHunterJTabbedPane.getSelectedIndex() == 6 | textHunterJTabbedPane.getSelectedIndex() == 7) & !hextextActivated) {
                    Utils.checkGateInit(relFilePath + File.separator + "resources" + File.separator, GlobalParameters.debugOn);
                    hextext.loadHextext();
                    hextextActivated = true;
                } else if ((textHunterJTabbedPane.getSelectedIndex() != 6 & textHunterJTabbedPane.getSelectedIndex() != 7) & hextextActivated) {
                    hextext.unloadHextext();
                    hextextActivated = false;
                }
            } else if (textHunterJTabbedPane.getSelectedIndex() == 2) {
                updateProjectSummary();
            }
        }
    }//GEN-LAST:event_textHunterJTabbedPaneStateChanged

    private void hextextNextContextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hextextNextContextActionPerformed
        updateFormSentenceOnly(hextextIterator.next());
    }//GEN-LAST:event_hextextNextContextActionPerformed

    private void keyObsEditorPaneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyObsEditorPaneKeyTyped
        if (useNumericKeysCheckBox.isSelected()) {
            evt.consume();
        }
    }//GEN-LAST:event_keyObsEditorPaneKeyTyped

    private void keyObsEditorPaneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyObsEditorPaneKeyReleased
        if (useNumericKeysCheckBox.isSelected()) {
            evt.consume();
        }
    }//GEN-LAST:event_keyObsEditorPaneKeyReleased

    private void keyObsEditorPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_keyObsEditorPaneFocusGained
        keyObsEditorPane.selectAll();
    }//GEN-LAST:event_keyObsEditorPaneFocusGained

    private void jumpToRecordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jumpToRecordButtonActionPerformed
        jumpToRecord();
    }//GEN-LAST:event_jumpToRecordButtonActionPerformed

    private void nextRecordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextRecordButtonActionPerformed
        // TODO add your handling code here:
        nextRecord();
    }//GEN-LAST:event_nextRecordButtonActionPerformed

    private void prevRecordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevRecordButtonActionPerformed
        prevRecord();
    }//GEN-LAST:event_prevRecordButtonActionPerformed

    private void firstRecordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstRecordButtonActionPerformed
        firstRecord();
    }//GEN-LAST:event_firstRecordButtonActionPerformed

    private void tab1SQLEditorPaneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tab1SQLEditorPaneKeyTyped
        changeMode();
    }//GEN-LAST:event_tab1SQLEditorPaneKeyTyped

    private void XMLradioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XMLradioButtonActionPerformed
        outputModeChanged();
    }//GEN-LAST:event_XMLradioButtonActionPerformed

    private void integratedSecurityCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_integratedSecurityCheckBoxActionPerformed
        if (integratedSecurityCheckBox.isSelected()) {
            databasePasswordField.setEnabled(false);
            databaseUserNameTextField.setEnabled(false);
        } else {
            databasePasswordField.setEnabled(true);
            databaseUserNameTextField.setEnabled(true);
        }

    }//GEN-LAST:event_integratedSecurityCheckBoxActionPerformed

    private void testDBConnectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testDBConnectionButtonActionPerformed
        String actionPerformed = evt.getActionCommand();
        GenericSW genericSW = new GenericSW(actionPerformed);
        genericSW.execute();
    }//GEN-LAST:event_testDBConnectionButtonActionPerformed

    private void goldStandardRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goldStandardRadioButtonActionPerformed
        changeMode();
    }//GEN-LAST:event_goldStandardRadioButtonActionPerformed

    private void modelSeedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelSeedRadioButtonActionPerformed
        changeMode();
    }//GEN-LAST:event_modelSeedRadioButtonActionPerformed

    private void activeLearningRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activeLearningRadioButtonActionPerformed
        changeMode();
    }//GEN-LAST:event_activeLearningRadioButtonActionPerformed

    private void reviewTrainingRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reviewTrainingRadioButtonActionPerformed
        changeMode();
    }//GEN-LAST:event_reviewTrainingRadioButtonActionPerformed

    private void cnDocIDFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cnDocIDFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cnDocIDFieldActionPerformed

    private void annotator1RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annotator1RadioButtonActionPerformed
        annotatorChange(true);

    }//GEN-LAST:event_annotator1RadioButtonActionPerformed

    private void annotator2RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annotator2RadioButtonActionPerformed
        annotatorChange(true);
    }//GEN-LAST:event_annotator2RadioButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnnotationEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AnnotationEditor().setVisible(true);
            }
        });

    }
    private boolean projectLoaded;
    private String directoryForContextHunterTable;
    private Iterator<Annotation> hextextIterator;
    private HexText hextext;
    private boolean hextextActivated;
    private XValidateSW xValidateSW;
    private TrainAllSW trainAllSW;
    private ApplyAllSWMultithreaded2 applyAllSWMultithreaded;
    ;
    int rowCount;
    volatile AtomicInteger workerProgress;
    private String relFilePath;
    private Connection con;
    private ResultSet resultSet;
    private ResultSet updatableResultSet;
    private Statement stmt;
    private String targetTableName;
    private String getInstancesForAnnotationSQL;
    private String getAnnotationsForModelTrainingSQL;
    private String getAnnotationsXMLOutputSQL;
    private String getAnnotationsForModelApplicationSQL;
    private boolean embeddedMode;
    private String appLocation;
    private String projectLocation;
    private String resultsDir;
    private String gateHomeDir;
    private String foldNumber;
    private String xmlOutputDir;
    protected Action numpad0Action;
    protected Action numpad1Action;
    protected Action numpad2Action;
    protected Action numpad3Action;
    protected Action numpadPlusAction;
    protected Action keyAltToPrevRecordAction;
    protected Action keyTabToCommentsAction;
    protected Action commentsAltToKeyAction;
    protected Action commentsTabToNextRecordAction;
    private ProjectXMLhandler projectXML;
    private int threadCount;
    private BlockingQueue<CorpusController> pool;
    private final static Object lock = new Object();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog DBConnectjDialog;
    private javax.swing.ButtonGroup GATEorKnowtatorbuttonGroup;
    private javax.swing.JFileChooser HunterDirJFileChooser;
    private javax.swing.JPanel HunterJPanel;
    private javax.swing.JEditorPane MLObsEditorPane;
    private javax.swing.JRadioButton XMLradioButton;
    private javax.swing.JRadioButton activeLearningRadioButton;
    private javax.swing.JRadioButton advancedModeRadioButton;
    private javax.swing.JPanel annotateJPanel;
    private javax.swing.JEditorPane annotationPane;
    private javax.swing.JRadioButton annotator1RadioButton;
    private javax.swing.JRadioButton annotator2RadioButton;
    private javax.swing.JButton applyAllButton;
    private javax.swing.JTextField brcIDField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField cnDocIDField;
    private javax.swing.JEditorPane commentsEditorPane;
    private javax.swing.JPanel connectToDBJPanel;
    private javax.swing.JCheckBox connectToExternalDatabaseCheckBox;
    private javax.swing.ButtonGroup contextChangeButtonGroup;
    private javax.swing.JButton createNewProjectButton;
    private javax.swing.JPanel createNewProjectJPanel;
    private javax.swing.JButton crossValidateButton;
    private javax.swing.JTextField currentRecordNumberTextField;
    private javax.swing.JPasswordField databasePasswordField;
    private javax.swing.JTextField databaseUserNameTextField;
    private javax.swing.JButton exportResultsButton;
    private javax.swing.JRadioButton expressModeRadioButton;
    private javax.swing.JButton firstRecordButton;
    private javax.swing.JTextField foldNoTextField;
    private javax.swing.ButtonGroup fullDocsOrContextOnlyButtonGroup;
    private javax.swing.JEditorPane fullTextPane;
    private javax.swing.JRadioButton gateXMLjRadioButton;
    private javax.swing.JButton getAnnotationsButton;
    private javax.swing.ButtonGroup getAnnotationsButtonGroup;
    private javax.swing.JRadioButton getAnnotationsForIAA;
    private javax.swing.JRadioButton goldStandardRadioButton;
    private javax.swing.JButton hextextNextContext;
    private javax.swing.ButtonGroup iAAbuttonGroup;
    private javax.swing.JTextPane infoTextPane;
    private javax.swing.JCheckBox integratedSecurityCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextArea japeKeyPhraseTextArea;
    private javax.swing.JTextArea japeOtherPhraseTextArea;
    private javax.swing.JTextField jdbcConnectionTextField;
    private javax.swing.JButton jumpToRecordButton;
    private javax.swing.JTextField keyObsContextCat;
    private javax.swing.JEditorPane keyObsEditorPane;
    private javax.swing.JRadioButton knowtatorXMLjRadioButton;
    private javax.swing.JButton launchHunterTableButton;
    private javax.swing.JPanel licenceJPanel;
    private javax.swing.JPanel loadExistingProjectJPanel;
    private javax.swing.JButton loadProjectButton;
    private javax.swing.JFileChooser loadProjectJFileChooser;
    private javax.swing.JDialog longRunningProcessDialog;
    private javax.swing.JTextPane longRunningProcessInfoTextPane;
    private javax.swing.JProgressBar longRunningProcessProgressBar;
    private javax.swing.ButtonGroup modeChangeButtonGroup;
    private javax.swing.JRadioButton modelSeedRadioButton;
    private javax.swing.JTextField newProjectJTextField;
    private javax.swing.JButton nextRecordButton;
    private javax.swing.JRadioButton noKeyAnnotationsRadioButton;
    private javax.swing.JPanel outputConsoleJPanel;
    private javax.swing.JRadioButton outputContextOnlyRadioButton;
    private javax.swing.JFileChooser outputFileChooser;
    private javax.swing.JRadioButton outputFullDocumentsRadioButton;
    private javax.swing.JButton prevRecordButton;
    private javax.swing.JEditorPane probEditorPane;
    private javax.swing.JTextPane projectSummaryTextPane;
    private javax.swing.JButton quitButton;
    private javax.swing.JCheckBox removePrevCheckBox;
    private javax.swing.JTextField resultsDirTextField;
    private javax.swing.JCheckBox resumePreviousCheckBox;
    private javax.swing.JRadioButton reviewTrainingRadioButton;
    private javax.swing.JButton saveProjectJButton;
    private javax.swing.JPanel setUpDataForAnnotationJPanel;
    private javax.swing.JPanel specifyKeywordsJPanel;
    private javax.swing.JTextField srcTableField;
    private javax.swing.JPanel svmConsoleJPanel;
    private javax.swing.JProgressBar svmProgressBar;
    private javax.swing.JEditorPane tab1SQLEditorPane;
    private javax.swing.JEditorPane tab2SQLEditorPane;
    private javax.swing.JRadioButton tableRadioButton;
    private javax.swing.JTextField tableToWorkOnjTextField;
    private javax.swing.JTextField targetTableTextField;
    private javax.swing.JButton testDBConnectionButton;
    private javax.swing.JTabbedPane textHunterJTabbedPane;
    private javax.swing.JTextField threadCountTextField;
    private javax.swing.JTextField totalRecordsInResultSetTextField;
    private javax.swing.JButton trainAllButton;
    private javax.swing.JCheckBox useNumericKeysCheckBox;
    private javax.swing.JPanel viewFullDocJPanel;
    private javax.swing.JRadioButton withKeyAnnotationsRadioButton;
    private javax.swing.JRadioButton xValQADRadioButton;
    private javax.swing.JRadioButton xValSACRadioButton;
    private javax.swing.ButtonGroup xValTypeButtonGroup;
    private javax.swing.ButtonGroup xmlOrTableButtonGroup;
    private javax.swing.ButtonGroup xmlOutPutButtonGroup;
    // End of variables declaration//GEN-END:variables
}
