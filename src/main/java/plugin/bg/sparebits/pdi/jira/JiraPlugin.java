/*
 * JiraPlugin.java
 * Created on 14.09.2013 15:41:37
 */
package plugin.bg.sparebits.pdi.jira;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.jayway.jsonpath.JsonPath;


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
            connection = new JiraConnection(new URL(meta.getConnectionUrl()), meta.getUsername(), meta.getPassword());
            rowMeta = new RowMeta();
            meta.getFields(rowMeta, getStepname(), null, getStepMeta(), getParentVariableSpace());
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
            int start = 0, max = 50;
            String jql = getTransMeta().environmentSubstitute(meta.getJql());
            String result = "";
            while (true) {
                result = connection.get(String.format("/search?jql=%s&startAt=%d&maxResults=%d",
                        URLEncoder.encode(jql, "utf-8"), start, max));
                List<Object> issues = JsonPath.read(result, "$.issues[*]");
                if (issues.isEmpty()) {
                    break;
                }
                putRow(rowMeta, new Object[] {
                    result
                });
                start += max;
            }
            setOutputDone();
        } catch (Exception e) {
            logError("Failed to execute JQL", e);
            return false;
        }
        return super.processRow(smi, sdi);
    }

    @Override
    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
        super.dispose(smi, sdi);
    }

}
