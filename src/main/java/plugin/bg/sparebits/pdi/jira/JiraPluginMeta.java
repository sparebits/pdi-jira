/*! ***************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package plugin.bg.sparebits.pdi.jira;

import java.io.Serializable;
import java.util.List;

import org.pentaho.di.core.CheckResultInterface;
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
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import plugin.bg.sparebits.pdi.jira.ui.JiraPluginDialog;
import bg.sparebits.pdi.domain.Api;
import bg.sparebits.pdi.domain.CustomApiMeta;
import bg.sparebits.pdi.domain.IssueApiMeta;
import bg.sparebits.pdi.domain.ProjectApiMeta;
import bg.sparebits.pdi.domain.SearchApiMeta;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * API configurations are persisted in JSON format as this detaches this class
 * from extending the API configurations Yo can add new fields to specific API
 * configuration classes without changing anything here and it will work.
 * 
 * @author Neyko Neykov, 2013
 */
@Step(categoryDescription = "Input", image = "jira.png", name = "Jira Plugin", description = "Regular expression",
        id = "JiraPlugin", documentationUrl = "http://pdijira.e-helix.com/help.html")
public class JiraPluginMeta extends BaseStepMeta implements StepMetaInterface {

    public static final String CONNECTION_URL = "connectionUrl";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String JQL = "jql";
    public static final String MAX_RESULTS = "maxResults";
    public static final String START_PAGE = "startPage";
    public static final String PAGES = "startPage";
    public static final String OUTPUT_FIELD = "outputField";
    public static final String OUTPUT_FIELDS = "outputFields";
    public static final String FIELD_NAME = "fieldName";
    public static final String FIELD_TYPE = "fieldType";
    public static final String FIELD_EXPRESSION = "fieldExpression";
    public static final String API = "api";
    public static final String API_CONFIGURATION = "apiConfiguration";
    public static final String ISSUE = "issue";

    private Gson gson = new Gson();

    private String connectionUrl;
    private String username;
    private String password;
    private String jql;
    private int maxResults;
    private int startPage;
    private int pages;
    private String outputField;
    private String[] fieldNames;
    private int[] fieldTypes;
    private String[] fieldExpressions;
    private String api;
    private String issue;
    private String apiConfiguration;

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

    public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
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
        Node outputFields = XMLHandler.getSubNode(stepnode, OUTPUT_FIELDS);
        int number = XMLHandler.countNodes(outputFields, OUTPUT_FIELD);
        fieldNames = new String[number];
        fieldTypes = new int[number];
        fieldExpressions = new String[number];
        for (int i = 0; i < number; i++) {
            Node fieldNode = XMLHandler.getSubNodeByNr(outputFields, OUTPUT_FIELD, i);
            fieldNames[i] = XMLHandler.getTagValue(fieldNode, FIELD_NAME);
            fieldTypes[i] = Integer.parseInt(XMLHandler.getTagValue(fieldNode, FIELD_TYPE));
            fieldExpressions[i] = XMLHandler.getTagValue(fieldNode, FIELD_EXPRESSION);
        }
        api = XMLHandler.getTagValue(stepnode, API);
        apiConfiguration = XMLHandler.getTagValue(stepnode, API_CONFIGURATION);
        issue = XMLHandler.getTagValue(stepnode, ISSUE);
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
        sb.append(XMLHandler.openTag(OUTPUT_FIELDS));
        if (fieldNames != null) {
            for (int i = 0; i < fieldNames.length; i++) {
                sb.append(XMLHandler.openTag(OUTPUT_FIELD));
                sb.append(XMLHandler.addTagValue(FIELD_NAME, fieldNames[i]));
                sb.append(XMLHandler.addTagValue(FIELD_TYPE, fieldTypes[i]));
                sb.append(XMLHandler.addTagValue(FIELD_EXPRESSION, fieldExpressions[i]));
                sb.append(XMLHandler.closeTag(OUTPUT_FIELD));
            }
        }
        sb.append(XMLHandler.closeTag(OUTPUT_FIELDS));
        sb.append(XMLHandler.addTagValue(API, api));
        sb.append(XMLHandler.addTagValue(API_CONFIGURATION, apiConfiguration));
        sb.append(XMLHandler.addTagValue(ISSUE, issue));
        return sb.toString();
    }

    public void readRep(Repository rep, IMetaStore metaStore, ObjectId idStep, List<DatabaseMeta> databases)
            throws KettleException {
        connectionUrl = rep.getStepAttributeString(idStep, CONNECTION_URL);
        username = rep.getStepAttributeString(idStep, USERNAME);
        password = Encr.decryptPassword(rep.getStepAttributeString(idStep, PASSWORD));
        jql = rep.getStepAttributeString(idStep, JQL);
        maxResults = (int) rep.getStepAttributeInteger(idStep, MAX_RESULTS);
        startPage = (int) rep.getStepAttributeInteger(idStep, START_PAGE);
        outputField = rep.getStepAttributeString(idStep, OUTPUT_FIELD);
        readOutputFields(rep, idStep);
        api = rep.getStepAttributeString(idStep, API);
        apiConfiguration = rep.getStepAttributeString(idStep, API_CONFIGURATION);
        issue = rep.getStepAttributeString(idStep, ISSUE);
    }

    private void readOutputFields(Repository rep, ObjectId idStep) throws KettleException {
        int number = rep.countNrStepAttributes(idStep, FIELD_NAME);
        fieldNames = new String[number];
        fieldExpressions = new String[number];
        for (int i = 0; i < number; i++) {
            fieldNames[i] = rep.getStepAttributeString(idStep, i, FIELD_NAME);
            fieldTypes[i] = (int) rep.getStepAttributeInteger(idStep, i, FIELD_TYPE);
            fieldExpressions[i] = rep.getStepAttributeString(idStep, i, FIELD_EXPRESSION);
        }
    }

    public void saveRep(Repository rep, IMetaStore metaStore, ObjectId idTransformation, ObjectId idStep) throws KettleException {
        rep.saveStepAttribute(idTransformation, idStep, CONNECTION_URL, connectionUrl);
        rep.saveStepAttribute(idTransformation, idStep, USERNAME, username);
        rep.saveStepAttribute(idTransformation, idStep, PASSWORD, Encr.encryptPassword(password));
        rep.saveStepAttribute(idTransformation, idStep, JQL, jql);
        rep.saveStepAttribute(idTransformation, idStep, MAX_RESULTS, maxResults);
        rep.saveStepAttribute(idTransformation, idStep, START_PAGE, startPage);
        rep.saveStepAttribute(idTransformation, idStep, OUTPUT_FIELD, outputField);
        if (fieldNames != null) {
            for (int i = 0; i < fieldNames.length; i++) {
                rep.saveStepAttribute(idTransformation, idStep, i, FIELD_NAME, fieldNames[i]);
                rep.saveStepAttribute(idTransformation, idStep, i, FIELD_TYPE, fieldTypes[i]);
                rep.saveStepAttribute(idTransformation, idStep, i, FIELD_EXPRESSION, fieldExpressions[i]);
            }
        }
        rep.saveStepAttribute(idTransformation, idStep, API, api);
        rep.saveStepAttribute(idTransformation, idStep, API_CONFIGURATION, apiConfiguration);
        rep.saveStepAttribute(idTransformation, idStep, ISSUE, issue);
    }

    public void setDefault() {
    }

    @Override
    public String getDialogClassName() {
        return JiraPluginDialog.class.getName();
    }

    @Override
    public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
            VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
        super.getFields(inputRowMeta, name, info, nextStep, space, repository, metaStore);
        if (fieldNames == null || fieldNames.length == 0) {
            inputRowMeta.addValueMeta(new ValueMeta(outputField, ValueMetaInterface.TYPE_STRING,
                    ValueMetaInterface.STORAGE_TYPE_NORMAL));
        } else {
            for (int i = 0; i < fieldNames.length; i++) {
                inputRowMeta.addValueMeta(new ValueMeta(fieldNames[i], fieldTypes[i],
                        ValueMetaInterface.STORAGE_TYPE_NORMAL));
            }
        }
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

    /**
     * @return the outputFields
     */
    public String[] getFieldNames() {
        return fieldNames;
    }

    /**
     * @param fieldNames the outputFields to set
     */
    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    /**
     * @return the outputExpressions
     */
    public String[] getFieldExpressions() {
        return fieldExpressions;
    }

    /**
     * @param fieldExpressions the outputExpressions to set
     */
    public void setFieldExpressions(String[] fieldExpressions) {
        this.fieldExpressions = fieldExpressions;
    }

    /**
     * @return the fieldTypes
     */
    public int[] getFieldTypes() {
        return fieldTypes;
    }

    /**
     * @param fieldTypes the fieldTypes to set
     */
    public void setFieldTypes(int[] fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    /**
     * @return the api
     */
    public String getApi() {
        return api;
    }

    /**
     * @param api the api to set
     */
    public void setApi(String api) {
        this.api = api;
    }

    /**
     * Retrieve the issue key when Issue API is used
     * @return the issue
     */
    public String getIssue() {
        return issue;
    }

    /**
     * Set the issue key when the Issue API is used
     * @param issue the issue to set
     */
    public void setIssue(String issue) {
        this.issue = issue;
    }

    /**
     * Retrieves the API configuration in JSON format
     * @return the apiConfiguration
     */
    public String getApiConfiguration() {
        return apiConfiguration;
    }
    public Serializable getApiConfiguration(Api api) {
        switch (api) {
        case search:
            return gson.fromJson(apiConfiguration, new TypeToken<SearchApiMeta>() {
            }.getType());
        case issue:
            return gson.fromJson(apiConfiguration, IssueApiMeta.class);
        case project:
            return gson.fromJson(apiConfiguration, ProjectApiMeta.class);
        case custom:
        default:
            return gson.fromJson(apiConfiguration, CustomApiMeta.class);
        }
    }

    /**
     * Sets the configuration for the selected API in JSON format
     * @param apiConfiguration the apiConfiguration to set
     */
    public void setApiConfiguration(String apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }
    public void setApiConfiguration(Serializable configuration) {
        this.apiConfiguration = gson.toJson(configuration);
    }

}
