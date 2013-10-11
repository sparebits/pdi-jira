/*
 * JiraPlugin.java
 * Created on 14.09.2013 15:41:37
 */
package plugin.bg.sparebits.pdi.jira;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;


/**
 * @author Neyko Neykov, 2013
 */
public class JiraPlugin extends BaseStep implements StepInterface {

    private JiraPluginMeta meta;
    private JiraConnection connection;

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
            return super.init(smi, sdi);
        } catch (MalformedURLException e) {
            getLogChannel().logError("Connection URL error", e);
            return false;
        }
    }

    @Override
    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
        getLogChannel().logBasic("processing JQL: " + meta.getJql());
        connection.connect();
        try {
            String result = connection.get("/search?jql=" + URLEncoder.encode(meta.getJql(), "utf-8"));
            getLogChannel().logBasic("result: " + result);
        } catch (Exception e) {
            getLogChannel().logError("Failed to execute JQL", e);
            return false;
        }
        return super.processRow(smi, sdi);
    }

    @Override
    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
        super.dispose(smi, sdi);
    }

}
