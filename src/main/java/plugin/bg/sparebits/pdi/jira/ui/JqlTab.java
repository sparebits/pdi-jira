/*
 * JqlTab.java
 * Created on 08.10.2013 16:46:39 
 */
package plugin.bg.sparebits.pdi.jira.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.ui.core.PropsUI;

import plugin.bg.sparebits.pdi.jira.JiraPluginMeta;


/**
 * @author nneikov 2013
 */
public class JqlTab extends Composite {

    private PropsUI props = PropsUI.getInstance();
    private JiraPluginMeta meta;

    private Text jqlField;
    private Text maxResultsField;
    private Text startPageField;

    public JqlTab(Composite parent, JiraPluginMeta meta) {
        super(parent, SWT.NONE);
        this.meta = meta;

        FormLayout fl = new FormLayout();
        fl.marginWidth = Const.FORM_MARGIN;
        fl.marginHeight = Const.FORM_MARGIN;
        setLayout(fl);
        props.setLook(this);

        createJql(this);
    }

    private void createJql(Composite parent) {
        Utils.createLabel(parent, null, "Jira.Label.JQL");

        jqlField = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        jqlField.setText(meta.getJql() != null ? meta.getJql() : "");
        props.setLook(jqlField);

        jqlField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                meta.setChanged();
            }
        });
        FormData fd = new FormData();
        fd.left = new FormAttachment(props.getMiddlePct(), 0);
        fd.top = new FormAttachment(0, Const.MARGIN);
        fd.right = new FormAttachment(100, -Const.MARGIN);
        jqlField.setLayoutData(fd);
    }

    public Text getJqlField() {
        return jqlField;
    }

}
