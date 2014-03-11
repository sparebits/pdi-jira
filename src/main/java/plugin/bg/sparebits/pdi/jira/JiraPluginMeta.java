/*
 * JiraPluginMeta.java
 * Created on 14.09.2013 15:39:40
 */
package plugin.bg.sparebits.pdi.jira;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

import plugin.bg.sparebits.pdi.jira.ui.JiraPluginDialog;


/**
 * @author Neyko Neykov, 2013
 */
@Step(categoryDescription = "Input", image = "jira.png", name = "Jira Plugin", description = "Regular expression",
        id = "JiraPlugin")
public class JiraPluginMeta extends BaseStepMeta implements StepMetaInterface {

    public static final String CONNECTION_URL = "connectionUrl";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String JQL = "jql";
    public static final String MAX_RESULTS = "maxResults";
    public static final String START_PAGE = "startPage";
    public static final String PAGES = "startPage";
    public static final String OUTPUT_FIELD = "outputField";

    private String connectionUrl;
    private String username;
    private String password;
    private String jql;
    private int maxResults;
    private int startPage;
    private int pages;
    private String outputField;

    public void check(List<CheckResultInterface> arg0, TransMeta arg1, StepMeta arg2, RowMetaInterface arg3,
            String[] arg4, String[] arg5, RowMetaInterface arg6) {
    }

    public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
            TransMeta transMeta, Trans trans) {
        return new JiraPlugin(stepMeta, stepDataInterface, copyNr, transMeta, trans);
    }

    public StepDataInterface getStepData() {
        return new JiraPluginData();
    }

    public void loadXML(Node stepnode, List<DatabaseMeta> arg1, Map<String, Counter> arg2) throws KettleXMLException {
        connectionUrl = XMLHandler.getTagValue(stepnode, CONNECTION_URL);
        username = XMLHandler.getTagValue(stepnode, USERNAME);
        password = Encr.decryptPassword(XMLHandler.getTagValue(stepnode, PASSWORD));
        jql = XMLHandler.getTagValue(stepnode, JQL);
        String value;
        if ((value = XMLHandler.getTagValue(stepnode, MAX_RESULTS)) != null) {
            maxResults = Integer.parseInt(value);
        }
        if ((value = XMLHandler.getTagValue(stepnode, START_PAGE)) != null) {
            startPage = Integer.parseInt(value);
        }
        outputField = XMLHandler.getTagValue(stepnode, OUTPUT_FIELD);
    }

    @Override
    public String getXML() {
        StringBuilder sb = new StringBuilder();
        sb.append(XMLHandler.addTagValue(CONNECTION_URL, connectionUrl));
        sb.append(XMLHandler.addTagValue(USERNAME, username));
        sb.append(XMLHandler.addTagValue(PASSWORD, Encr.encryptPassword(password)));
        sb.append(XMLHandler.addTagValue(JQL, jql));
        sb.append(XMLHandler.addTagValue(MAX_RESULTS, maxResults));
        sb.append(XMLHandler.addTagValue(START_PAGE, startPage));
        sb.append(XMLHandler.addTagValue(OUTPUT_FIELD, outputField));
        return sb.toString();
    }

    public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> arg2, Map<String, Counter> arg3)
            throws KettleException {
        connectionUrl = rep.getStepAttributeString(idStep, CONNECTION_URL);
        username = rep.getStepAttributeString(idStep, USERNAME);
        password = Encr.decryptPassword(rep.getStepAttributeString(idStep, PASSWORD));
        jql = rep.getStepAttributeString(idStep, JQL);
        maxResults = (int) rep.getStepAttributeInteger(idStep, MAX_RESULTS);
        startPage = (int) rep.getStepAttributeInteger(idStep, START_PAGE);
        outputField = rep.getStepAttributeString(idStep, OUTPUT_FIELD);
    }

    public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
        rep.saveStepAttribute(idTransformation, idStep, CONNECTION_URL, connectionUrl);
        rep.saveStepAttribute(idTransformation, idStep, USERNAME, username);
        rep.saveStepAttribute(idTransformation, idStep, PASSWORD, Encr.encryptPassword(password));
        rep.saveStepAttribute(idTransformation, idStep, JQL, jql);
        rep.saveStepAttribute(idTransformation, idStep, MAX_RESULTS, maxResults);
        rep.saveStepAttribute(idTransformation, idStep, START_PAGE, startPage);
        rep.saveStepAttribute(idTransformation, idStep, OUTPUT_FIELD, outputField);
    }

    public void setDefault() {
    }

    @Override
    public String getDialogClassName() {
        return JiraPluginDialog.class.getName();
    }

    @Override
    public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
            VariableSpace space) throws KettleStepException {
        super.getFields(inputRowMeta, name, info, nextStep, space);
        inputRowMeta.addValueMeta(new ValueMeta(outputField, ValueMetaInterface.TYPE_STRING));
    }

    /**
     * @return the connectionUrl
     */
    public String getConnectionUrl() {
        return connectionUrl;
    }

    /**
     * @param connectionUrl the connectionUrl to set
     */
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the jql
     */
    public String getJql() {
        return jql;
    }

    /**
     * @param jql the jql to set
     */
    public void setJql(String jql) {
        this.jql = jql;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getOutputField() {
        return outputField;
    }

    public void setOutputField(String outputField) {
        this.outputField = outputField;
    }

}
