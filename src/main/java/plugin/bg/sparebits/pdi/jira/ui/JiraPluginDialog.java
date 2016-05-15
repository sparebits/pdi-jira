/*
 * JiraPluginDialog.java
 * Created on 16.09.2013 17:46:09 
 */
package plugin.bg.sparebits.pdi.jira.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import plugin.bg.sparebits.pdi.jira.JiraPluginMeta;


/**
 * @author nneikov 2013
 */
public class JiraPluginDialog extends BaseStepDialog implements StepDialogInterface {

    private JiraPluginMeta meta;
    private CTabFolder tabFolder;
    private ConnectionTab connectionTab;
    private ApiCallTab apiTab;
    private FieldsTab fieldsTab;

    public JiraPluginDialog(Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname) {
        super(parent, (BaseStepMeta) baseStepMeta, transMeta, stepname);
        meta = (JiraPluginMeta) baseStepMeta;
    }

    public String open() {
        Shell parent = getParent();
        Display display = parent.getDisplay();
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;
        shell.setLayout(formLayout);
        shell.setText("Jira Plugin");
        props.setLook(shell);
        setShellImage(shell, meta);

        createButtons(shell);
        createStepName(shell);
        createTabFolder(shell);
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        return stepname;
    }

    /**
     * @param shell
     */
    private void createButtons(Shell shell) {
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(" &OK ");
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(" &Cancel ");
        fdOK = new FormData();
        setButtonPositions(new Button[] {
            wOK, wCancel
        }, Const.MARGIN, null);
        lsCancel = new Listener() {
            public void handleEvent(Event e) {
                onCancel();
            }
        };
        lsOK = new Listener() {
            public void handleEvent(Event e) {
                onOK();
            }
        };
        wCancel.addListener(SWT.Selection, lsCancel);
        wOK.addListener(SWT.Selection, lsOK);
        lsDef = new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
                onOK();
            }
        };
    }

    private void onCancel() {
        meta.setChanged(false);
        dispose();
    }

    private void onOK() {
        stepname = wStepname.getText();
        props.saveProps();
        meta.setConnectionUrl(connectionTab.getUrlField().getText());
        meta.setUsername(connectionTab.getUsernameField().getText());
        meta.setPassword(connectionTab.getPasswordField().getText());
        meta.setJql(apiTab.getJqlField().getText());
        meta.setOutputField(apiTab.getOutputField().getText());
        meta.setFieldNames(fieldsTab.getPredefinedFields());
        meta.setFieldExpressions(fieldsTab.getPredefinedExpressions());
        meta.setFieldTypes(fieldsTab.getPredefinedTypes());
        // meta.setApi(apiTab.getApi());
        dispose();
    }

    /**
     * @param shell
     */
    private void createTabFolder(Shell parent) {
        tabFolder = new CTabFolder(parent, SWT.BORDER);
        FormData fd = new FormData();
        fd.top = new FormAttachment(wStepname, Const.MARGIN);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        fd.bottom = new FormAttachment(wOK, -2 * Const.MARGIN);
        fd.width = 600;
        fd.height = 350;
        props.setLook(tabFolder);
        tabFolder.setLayoutData(fd);

        CTabItem ti = new CTabItem(tabFolder, SWT.NONE);
        ti.setText("Connection");
        ti.setControl(connectionTab = new ConnectionTab(tabFolder, meta));

        CTabItem jqlTabItem = new CTabItem(tabFolder, SWT.NONE);
        jqlTabItem.setText("API");
        jqlTabItem.setControl(apiTab = new ApiCallTab(tabFolder, meta));

        CTabItem fieldsTabItem = new CTabItem(tabFolder, SWT.NONE);
        fieldsTabItem.setText("Output Fields");
        fieldsTabItem.setControl(fieldsTab = new FieldsTab(tabFolder, meta));
        apiTab.addPredefinedPathListener(fieldsTab);
        apiTab.showSelectedApi(); // this will also notify listeners

        tabFolder.setSelection(ti);
    }

    private void createStepName(Composite shell) {
        wlStepname = new Label(shell, SWT.RIGHT);
        wlStepname.setText("Step name:");
        props.setLook(wlStepname);
        fdlStepname = new FormData();
        fdlStepname.left = new FormAttachment(0, 0);
        fdlStepname.right = new FormAttachment(props.getMiddlePct(), -Const.MARGIN);
        fdlStepname.top = new FormAttachment(0, Const.MARGIN);
        wlStepname.setLayoutData(fdlStepname);
        wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wStepname.setText(stepname);
        props.setLook(wStepname);
        wStepname.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                meta.setChanged();
            }
        });
        fdStepname = new FormData();
        fdStepname.left = new FormAttachment(props.getMiddlePct(), 0);
        fdStepname.top = new FormAttachment(0, Const.MARGIN);
        fdStepname.right = new FormAttachment(100, 0);
        wStepname.setLayoutData(fdStepname);
    }

}
