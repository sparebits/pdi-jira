/*
 * GeneralPanel.java
 * Created on 26.10.2009 06:29:26
 */
package plugin.bg.sparebits.pdi.jira.ui;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.ui.core.PropsUI;

import plugin.bg.sparebits.pdi.jira.JiraConnection;
import plugin.bg.sparebits.pdi.jira.JiraPluginMeta;


/**
 * General panel contains common controls used by all sections of the plug-in
 * @author Neyko Neykov, 2009
 */
public class ConnectionTab extends Composite {

    private PropsUI props = PropsUI.getInstance();
    private JiraPluginMeta meta;

    private Text urlField;
    private Text usernameField;
    private Text passwordField;

    public ConnectionTab(Composite parent, JiraPluginMeta meta) {
        super(parent, SWT.NONE);
        this.meta = meta;

        FormLayout fl = new FormLayout();
        fl.marginWidth = Const.FORM_MARGIN;
        fl.marginHeight = Const.FORM_MARGIN;
        setLayout(fl);
        props.setLook(this);

        createConnectionUrl(this);
        createUsername(this, urlField);
        createPassword(this, usernameField);
        createTestButton(this, passwordField);
    }

    /**
     * @return the urlField
     */
    public Text getUrlField() {
        return urlField;
    }

    /**
     * @return the usernameField
     */
    public Text getUsernameField() {
        return usernameField;
    }

    /**
     * @return the passwordField
     */
    public Text getPasswordField() {
        return passwordField;
    }

    /**
     * Creates field for editing the URL to connect to JIRA instance
     * @param parent
     */
    private void createConnectionUrl(Composite parent) {
        Utils.createLabel(parent, null, "Jira.Label.Connection");

        urlField = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        urlField.setText(meta.getConnectionUrl() != null ? meta.getConnectionUrl() : "");
        props.setLook(urlField);

        // TODO Add URL validation to this field
        urlField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                meta.setChanged();
            }
        });
        FormData fd = new FormData();
        fd.left = new FormAttachment(props.getMiddlePct(), 0);
        fd.top = new FormAttachment(0, Const.MARGIN);
        fd.right = new FormAttachment(100, -Const.MARGIN);
        urlField.setLayoutData(fd);
    }

    /**
     * Creates field to edit the user name to login the JIRA instance
     * @param parent
     */
    private void createUsername(Composite parent, Control up) {
        Utils.createLabel(parent, up, "Jira.Label.Username");

        usernameField = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        usernameField.setText(meta.getUsername() != null ? meta.getUsername() : "");
        // usernameField.setEchoChar('*');
        props.setLook(usernameField);
        usernameField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                meta.setChanged();
            }
        });
        FormData fd = new FormData();
        fd.left = new FormAttachment(props.getMiddlePct(), 0);
        fd.top = new FormAttachment(up, Const.MARGIN);
        fd.right = new FormAttachment(100, -Const.MARGIN);
        usernameField.setLayoutData(fd);
    }

    /**
     * Create field to edit the password for login the Jira instance
     * @param parent
     * @param up
     */
    private void createPassword(Composite parent, Control up) {
        Utils.createLabel(parent, up, "Jira.Label.Password");

        passwordField = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        passwordField.setText(meta.getPassword() != null ? meta.getPassword() : "");
        passwordField.setEchoChar('*');
        props.setLook(passwordField);
        passwordField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                meta.setChanged();
            }
        });
        FormData fd = new FormData();
        fd.left = new FormAttachment(props.getMiddlePct(), 0);
        fd.top = new FormAttachment(up, Const.MARGIN);
        fd.right = new FormAttachment(100, -Const.MARGIN);
        passwordField.setLayoutData(fd);
    }

    /**
     * @param connectionTab
     * @param passwordField2
     */
    private void createTestButton(Composite parent, Control up) {
        Button b = new Button(parent, SWT.CENTER);
        b.setText(Messages.getString("Jira.Button.Test"));
        b.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                try {
                    JiraConnection connection = new JiraConnection(new URL(urlField.getText()),
                            usernameField.getText(), passwordField.getText());
                    connection.connect();
                    connection.get("/");
                    MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
                    msgBox.setMessage(Messages.getString("Jira.Message.Success"));
                    msgBox.open();
                } catch (Exception e) {
                    MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
                    msgBox.setMessage(Messages.getString("Jira.Message.Fail"));
                    msgBox.open();
                }
            }
        });
        FormData fd = new FormData();
        fd.top = new FormAttachment(up, Const.MARGIN);
        fd.right = new FormAttachment(100, -Const.MARGIN);
        props.setLook(b);
        b.setLayoutData(fd);
    }

}
