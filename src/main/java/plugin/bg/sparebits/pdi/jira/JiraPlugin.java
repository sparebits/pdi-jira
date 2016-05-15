/*
 * JiraPlugin.java
 * Created on 14.09.2013 15:41:37
 */
package plugin.bg.sparebits.pdi.jira;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.Assert;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import bg.sparebits.pdi.domain.Api;
import bg.sparebits.pdi.domain.CustomApiMeta;
import bg.sparebits.pdi.domain.IssueApiMeta;
import bg.sparebits.pdi.domain.ProjectApiMeta;


/**
 * @author Neyko Neykov, 2013
 */
public class JiraPlugin extends BaseStep implements StepInterface {

    private JiraPluginMeta meta;
    private JiraConnection connection;
    private RowMetaInterface rowMeta;

    /**
     * @param stepMeta
     * @param stepDataInterface
     * @param copyNr
     * @param transMeta
     * @param trans
     */
    public JiraPlugin(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
            Trans trans) {
        super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        this.meta = (JiraPluginMeta) stepMeta.getStepMetaInterface();
    }

    @Override
    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        try {

            String url = getTransMeta().environmentSubstitute(meta.getConnectionUrl());
            String username = getTransMeta().environmentSubstitute(meta.getUsername());
            String password = getTransMeta().environmentSubstitute(meta.getPassword());

            connection = new JiraConnection(new URL(url), username, password);
            rowMeta = new RowMeta();
            meta.getFields(rowMeta, getStepname(), null, getStepMeta(), getParentVariableSpace(), this.repository,
                    this.metaStore);
            Configuration.setDefaults(new Configuration.Defaults() {
                private final JsonProvider jsonProvider = new GsonJsonProvider();
                private final MappingProvider mappingProvider = new GsonMappingProvider();

                @Override
                public JsonProvider jsonProvider() {
                    return jsonProvider;
                }

                @Override
                public MappingProvider mappingProvider() {
                    return mappingProvider;
                }

                @Override
                public Set<Option> options() {
                    return EnumSet.noneOf(Option.class);
                }
            });
            return super.init(smi, sdi);
        } catch (Exception e) {
            getLogChannel().logError("Failed to initialize", e);
            return false;
        }
    }

    @Override
    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
        logBasic("processing JQL: " + meta.getJql());
        try {
            connection.connect();
            Api api = meta.getApi() != null ? Api.valueOf(meta.getApi()) : Api.search;
            switch (api) {
            case search:
                search();
                break;
            case issue:
                issue();
                break;
            case project:
                project();
                break;
            case custom:
                custom();
                break;
            default:
                break;
            }
        } catch (Exception e) {
            setErrors(1);
            logError("Failed to execute JQL", e);
            return false;
        } finally {
            setOutputDone();
        }
        return false;
    }

    /**
     * 
     * @throws ClientProtocolException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws JiraPluginException
     * @throws KettleStepException
     */
    private void search() throws ClientProtocolException, UnsupportedEncodingException, IOException,
            JiraPluginException, KettleStepException {
        int start = 0, max = 50;
        String jql = getTransMeta().environmentSubstitute(meta.getJql());
        String result = "";
        while (true) {
            result = connection.get("api", String.format("/search?jql=%s&startAt=%d&maxResults=%d",
                    URLEncoder.encode(jql, "utf-8"), start, max));
            List<Object> issues = JsonPath.parse(result).read("$.issues[*]", new TypeRef<List<Object>>() {
            });
            if (issues.isEmpty()) {
                break;
            }
            doOutput(result);
            start += max;
        }
    }

    private void doOutput(String result) throws KettleStepException {
        if (meta.getFieldNames() == null || meta.getFieldNames().length == 0) {
            putRow(rowMeta, new Object[] {
                result
            });
        } else {
            Map<String, List<Object>> columns = new HashMap<String, List<Object>>();
            int size = 0;

            for (int i = 0; i < meta.getFieldNames().length; i++) {
                String pattern = meta.getFieldExpressions()[i];
                int type = rowMeta.getValueMeta(i).getType();
                ReadContext ctx = JsonPath.parse(result);
                Object value = mapValue(pattern, ctx, type);

                List<Object> values = new ArrayList<Object>();
                if (value instanceof List) {
                    values.addAll((List<?>) value);
                } else {
                    values.add((String) value);
                }
                columns.put(meta.getFieldNames()[i], values);
                if (values.size() > size) {
                    size = values.size();
                }
            }

            for (int i = 0; i < size; i++) {
                Object[] row = new Object[meta.getFieldNames().length];
                for (int j = 0; j < meta.getFieldNames().length; j++) {
                    String fieldName = meta.getFieldNames()[j];
                    List<Object> values = columns.get(fieldName);
                    if (values.size() >= size) {
                        row[j] = values.get(i);
                    } else if (values.size() == 1) {
                        row[j] = values.get(0);
                    }
                }
                putRow(rowMeta, row);
            }
        }
    }

    private Object mapValue(String pattern, ReadContext ctx, int type) {
        if (pattern.contains("*")) {
            switch (type) {
            case ValueMetaInterface.TYPE_INTEGER:
                return ctx.read(pattern, new TypeRef<List<Long>>() {
                });
            case ValueMetaInterface.TYPE_NUMBER:
                return ctx.read(pattern, new TypeRef<List<Double>>() {
                });
            case ValueMetaInterface.TYPE_BIGNUMBER:
                return ctx.read(pattern, new TypeRef<List<BigDecimal>>() {
                });
            case ValueMetaInterface.TYPE_STRING:
            default:
                return ctx.read(pattern, new TypeRef<List<String>>() {
                });
            }
        } else {
            switch (type) {
            case ValueMetaInterface.TYPE_INTEGER:
                return ctx.read(pattern, new TypeRef<Long>() {
                });
            case ValueMetaInterface.TYPE_NUMBER:
                return ctx.read(pattern, new TypeRef<Double>() {
                });
            case ValueMetaInterface.TYPE_BIGNUMBER:
                return ctx.read(pattern, new TypeRef<BigDecimal>() {
                });
            case ValueMetaInterface.TYPE_STRING:
            default:
                return ctx.read(pattern, new TypeRef<String>() {
                });
            }
        }
    }

    /**
     * 
     * @throws ClientProtocolException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws JiraPluginException
     * @throws KettleStepException
     */
    private void issue() throws ClientProtocolException, UnsupportedEncodingException, IOException, JiraPluginException,
            KettleStepException {
        IssueApiMeta issueMeta = (IssueApiMeta) meta.getApiConfiguration(Api.issue);
        Assert.assertNotNull(issueMeta);
        String issue = getTransMeta().environmentSubstitute(issueMeta.getKey());
        Assert.assertNotEmpty(issue);

        String call = String.format("/issue/%s", URLEncoder.encode(issue.toUpperCase(), "utf-8"));
        String subCall = issueMeta.getSubCall();
        if (subCall != null && !"".equals(subCall)) {
            call += "/" + subCall;
        }

        String result = connection.get("api", call);
        doOutput(result);
    }

    /**
     * 
     * @throws ClientProtocolException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws JiraPluginException
     * @throws KettleStepException
     */
    private void project() throws ClientProtocolException, UnsupportedEncodingException, IOException,
            JiraPluginException, KettleStepException {
        ProjectApiMeta projectMeta = (ProjectApiMeta) meta.getApiConfiguration(Api.project);
        Assert.assertNotNull(projectMeta);
        String project = getTransMeta().environmentSubstitute(projectMeta.getProjectKey());
        Assert.assertNotEmpty(project);

        String call = String.format("/project/%s", URLEncoder.encode(project.toUpperCase(), "utf-8"));
        String result = connection.get("api", call);
        doOutput(result);
    }

    /**
     * @throws JiraPluginException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws KettleStepException
     * 
     */
    private void custom() throws ClientProtocolException, IOException, JiraPluginException, KettleStepException {
        CustomApiMeta customMeta = (CustomApiMeta) meta.getApiConfiguration(Api.custom);
        Assert.assertNotNull(customMeta);
        String uri = getTransMeta().environmentSubstitute(customMeta.getUri());
        Assert.assertNotEmpty(uri);

        String result = connection.get("api", uri.trim());
        doOutput(result);
    }

    @Override
    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
        super.dispose(smi, sdi);
    }

}
