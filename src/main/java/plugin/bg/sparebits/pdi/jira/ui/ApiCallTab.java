/*
 * JqlTab.java
 * Created on 08.10.2013 16:46:39 
 */
package plugin.bg.sparebits.pdi.jira.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugin.bg.sparebits.pdi.jira.JiraPluginMeta;
import plugin.bg.sparebits.pdi.jira.PredefinedPathUtil;
import bg.sparebits.pdi.domain.Api;
import bg.sparebits.pdi.domain.CustomApiMeta;
import bg.sparebits.pdi.domain.IssueApiMeta;
import bg.sparebits.pdi.domain.PredefinedPath;
import bg.sparebits.pdi.domain.ProjectApiMeta;
import bg.sparebits.pdi.domain.SearchApiMeta;


/**
 * @author Neyko Neykov 2013
 */
public class ApiCallTab extends PdiJiraComposite {

    private PropsUI props = PropsUI.getInstance();
    private JiraPluginMeta meta;
    private Logger log = LoggerFactory.getLogger(getClass());

    private CCombo apiSelect;
    private TextVar jqlField;
    private Text outputField;
    private Composite container;
    private PdiJiraComposite searchPanel;
    private PdiJiraComposite issuePanel;
    private PdiJiraComposite projectPanel;
    private PdiJiraComposite customPanel;

    private List<PredefinedPathListener> predefinedPathListeners = new ArrayList<PredefinedPathListener>();
    private Map<String, List<PredefinedPath>> predefinedPaths;

    {
        predefinedPaths = PredefinedPathUtil.load("/predefined.json");
    }

    /**
     * ApiPanel is abstraction which defines some common for the API panels
     * functionality. Mainly the predefined fields factoring
     * 
     * @author Neyko Neykov 2015
     */
    private abstract class ApiPanel extends PdiJiraComposite {

        /**
         * @param parent
         */
        ApiPanel(Composite parent) {
            super(parent);

            FormLayout fl = new FormLayout();
            fl.marginWidth = 0;
            fl.marginHeight = 0;
            setLayout(fl);

            FormData fd = new FormData();
            fd.left = new FormAttachment(0, 0);
            fd.top = new FormAttachment(0, 0);
            fd.right = new FormAttachment(100, 0);
            fd.width = 100;
            this.setLayoutData(fd);
        }

    }

    /**
     * 
     * @author nneikov 2015
     */
    private class SearchApiPanel extends ApiPanel {

        private SearchApiMeta searchMeta;

        /**
         * 
         */
        SearchApiPanel(Composite parent) {
            super(parent);
            this.searchMeta = (SearchApiMeta) meta.getApiConfiguration(Api.search);
            if (this.searchMeta == null) {
                this.searchMeta = new SearchApiMeta();
            }
            createJql();
        }

        /**
         * 
         * @param parent
         */
        private void createJql() {
            createLabel(this, null, "Jira.Label.JQL");

            jqlField = new TextVar(Variables.getADefaultVariableSpace(), this, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
            jqlField.setText(searchMeta.getJql() != null ? searchMeta.getJql() : "");
            jqlField.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent arg0) {
                    searchMeta.setJql(jqlField.getText());
                    meta.setChanged();
                    meta.setApiConfiguration(searchMeta);
                }
            });
            props.setLook(jqlField);

            FormData fd = new FormData();
            fd.left = new FormAttachment(props.getMiddlePct(), 0);
            fd.top = new FormAttachment(0, Const.MARGIN);
            fd.right = new FormAttachment(100, -Const.MARGIN);
            jqlField.setLayoutData(fd);
        }

    }

    private enum IssueSubcall {
        comment,
        worklog
    }

    /**
     * 
     * @author nneikov 2015
     */
    private class IssueApiPanel extends ApiPanel {

        private TextVar issueField;
        private CCombo subcallSelect;
        private IssueApiMeta issueMeta;

        /**
         * 
         */
        IssueApiPanel(Composite parent) {
            super(parent);
            this.issueMeta = (IssueApiMeta) meta.getApiConfiguration(Api.issue);
            if (this.issueMeta == null) {
                this.issueMeta = new IssueApiMeta();
            }
            createIssueField();
            createSubApi();
        }

        /**
         * @param parent
         */
        private void createIssueField() {
            createLabel(this, null, "Jira.Label.Issue");

            String issue = issueMeta.getKey();
            issueField = new TextVar(Variables.getADefaultVariableSpace(), this, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
            issueField.setText(issue != null ? issue : "");
            issueField.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent arg0) {
                    synchronized (meta) {
                        issueMeta.setKey(issueField.getText());
                        meta.setChanged();
                        meta.setApiConfiguration(issueMeta);
                    }
                }
            });
            props.setLook(issueField);

            FormData fd = new FormData();
            fd.left = new FormAttachment(props.getMiddlePct(), 0);
            fd.top = new FormAttachment(0, Const.MARGIN);
            fd.right = new FormAttachment(100, -Const.MARGIN);
            issueField.setLayoutData(fd);
        }

        /**
         * 
         */
        private void createSubApi() {
            createLabel(this, issueField, "Jira.Label.Issue.Subcall");

            subcallSelect = new CCombo(this, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
            subcallSelect.setItems(new String[] {
                "", "Comments", "Worklog"
            });
            subcallSelect.addSelectionListener(subcallSelectionListener);
            String subcall = this.issueMeta.getSubCall();
            subcallSelect.select(subcall != null && !"".equals(subcall) ? IssueSubcall.valueOf(subcall).ordinal() + 1
                    : 0);
            props.setLook(subcallSelect);

            FormData fd = new FormData();
            fd.left = new FormAttachment(props.getMiddlePct(), 0);
            fd.top = new FormAttachment(issueField, Const.MARGIN);
            subcallSelect.setLayoutData(fd);
        }

        /**
         * 
         */
        private SelectionListener subcallSelectionListener = new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                synchronized (meta) {
                    switch (subcallSelect.getSelectionIndex()) {
                    case 0:
                    default:
                        issueMeta.setSubCall("");
                        notifyApiSelection("issue");
                        break;
                    case 1:
                        issueMeta.setSubCall(IssueSubcall.comment.name());
                        notifyApiSelection("issue-comments");
                        break;
                    case 2:
                        issueMeta.setSubCall(IssueSubcall.worklog.name());
                        notifyApiSelection("issue-worklogs");
                        break;
                    }
                    meta.setApiConfiguration(issueMeta);
                    meta.setChanged();
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        };

    }

    /**
     * 
     * @author nneikov 2015
     */
    private class ProjectApiPanel extends ApiPanel {

        private ProjectApiMeta projectMeta;
        private TextVar projectField;

        /**
         * 
         */
        ProjectApiPanel(Composite parent) {
            super(parent);
            this.projectMeta = (ProjectApiMeta) meta.getApiConfiguration(Api.project);
            if (this.projectMeta == null) {
                this.projectMeta = new ProjectApiMeta();
            }
            createProjectField();
        }

        /**
         * @param parent
         */
        private void createProjectField() {
            createLabel(this, null, "Jira.Label.Project");

            String project = projectMeta.getProjectKey();
            projectField = new TextVar(Variables.getADefaultVariableSpace(), this, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
            projectField.setText(project != null ? project : "");
            projectField.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent arg0) {
                    synchronized (meta) {
                        projectMeta.setProjectKey(projectField.getText());
                        meta.setChanged();
                        meta.setApiConfiguration(projectMeta);
                    }
                }
            });
            props.setLook(projectField);

            FormData fd = new FormData();
            fd.left = new FormAttachment(props.getMiddlePct(), 0);
            fd.top = new FormAttachment(0, Const.MARGIN);
            fd.right = new FormAttachment(100, -Const.MARGIN);
            projectField.setLayoutData(fd);
        }

    }

    /**
     * 
     * @author nneikov 2015
     */
    private class CustomApiPanel extends ApiPanel {

        private CustomApiMeta customMeta;
        private TextVar uriField;

        /**
         * 
         */
        CustomApiPanel(Composite parent) {
            super(parent);
            this.customMeta = (CustomApiMeta) meta.getApiConfiguration(Api.custom);
            if (this.customMeta == null) {
                this.customMeta = new CustomApiMeta();
            }
            createCustomURI();
        }

        /**
         * 
         * @param parent
         */
        private void createCustomURI() {
            createLabel(this, null, "Jira.Label.URI");

            uriField = new TextVar(Variables.getADefaultVariableSpace(), this, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
            uriField.setText(customMeta.getUri() != null ? customMeta.getUri() : "");
            uriField.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent arg0) {
                    customMeta.setUri(uriField.getText());
                    meta.setChanged();
                    meta.setApiConfiguration(customMeta);
                }
            });
            props.setLook(uriField);

            FormData fd = new FormData();
            fd.left = new FormAttachment(props.getMiddlePct(), 0);
            fd.top = new FormAttachment(0, Const.MARGIN);
            fd.right = new FormAttachment(100, -Const.MARGIN);
            uriField.setLayoutData(fd);
        }

    }

    /**
     * Listener for API selection events. This will change the UI relevant to
     * API selected and provide user with control to enter appropriate API
     * information
     */
    private SelectionListener apiSelectionListener = new SelectionListener() {

        @Override
        public void widgetSelected(SelectionEvent arg0) {
            synchronized (meta) {
                showSelectedApi();
                meta.setChanged();
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent arg0) {
            log.info("widgetDefaultSelected...");
        }
    };

    @SuppressWarnings("unused")
    private Text maxResultsField;
    @SuppressWarnings("unused")
    private Text startPageField;

    /**
     * 
     * @param parent
     * @param meta
     */
    public ApiCallTab(Composite parent, JiraPluginMeta meta) {
        super(parent);
        this.meta = meta;
        createApiSelect(this);
        createOutput(this, container);
    }

    /**
     * Create combo box to select the API for the call
     * @param parent
     */
    private void createApiSelect(Composite parent) {
        createLabel(parent, null, "Jira.Label.ApiSelect");

        Api api = meta.getApi() != null ? Api.valueOf(meta.getApi()) : Api.search;
        apiSelect = new CCombo(parent, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        apiSelect.setItems(new String[] {
            "Search", "Issue", "Project", "Custom"
        });
        apiSelect.addSelectionListener(apiSelectionListener);
        apiSelect.select(api != null ? api.ordinal() : 0);
        props.setLook(apiSelect);

        FormData fd = new FormData();
        fd.left = new FormAttachment(props.getMiddlePct(), 0);
        fd.top = new FormAttachment(0, Const.MARGIN);
        fd.right = new FormAttachment(100, -Const.MARGIN);
        apiSelect.setLayoutData(fd);

        container = new Composite(parent, SWT.NONE);
        props.setLook(container);

        FormLayout fl = new FormLayout();
        fl.marginWidth = 0;
        fl.marginHeight = 0;
        container.setLayout(fl);

        fd = new FormData();
        fd.left = new FormAttachment(0, 0);
        fd.top = new FormAttachment(apiSelect, 0);
        fd.right = new FormAttachment(100, 0);
        fd.width = 100;
        container.setLayoutData(fd);

        searchPanel = new SearchApiPanel(container);
        issuePanel = new IssueApiPanel(container);
        projectPanel = new ProjectApiPanel(container);
        customPanel = new CustomApiPanel(container);

        // showSelectedApi();
        parent.pack();
    }

    private void hideApiPanels() {
        searchPanel.setVisible(false);
        issuePanel.setVisible(false);
        projectPanel.setVisible(false);
        customPanel.setVisible(false);
    }

    public void showSelectedApi() {
        Api api = Api.values()[apiSelect.getSelectionIndex()];
        meta.setApi(api.name());
        notifyApiSelection(api.name());
        hideApiPanels();
        switch (api) {
        case search:
            searchPanel.setVisible(true);
            break;
        case issue:
            issuePanel.setVisible(true);
            break;
        case project:
            projectPanel.setVisible(true);
            break;
        case custom:
            customPanel.setVisible(true);
            break;
        default:
            break;
        }
    }

    /**
     * 
     * @param parent
     * @param top
     */
    private void createOutput(Composite parent, Control top) {
        createLabel(parent, top, "Jira.Label.Output");

        outputField = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        outputField.setText(meta.getOutputField() != null ? meta.getOutputField() : "result");
        props.setLook(outputField);

        outputField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                meta.setChanged();
            }
        });
        FormData fd = new FormData();
        fd.left = new FormAttachment(props.getMiddlePct(), 0);
        fd.top = new FormAttachment(top, Const.MARGIN);
        fd.right = new FormAttachment(100, -Const.MARGIN);
        outputField.setLayoutData(fd);
    }

    public TextVar getJqlField() {
        return jqlField;
    }

    public Text getOutputField() {
        return outputField;
    }

    public void addPredefinedPathListener(PredefinedPathListener l) {
        predefinedPathListeners.add(l);
    }

    public void removeApiSelectListener(PredefinedPathListener l) {
        predefinedPathListeners.remove(l);
    }

    public void notifyApiSelection(String api) {
        for (PredefinedPathListener l : predefinedPathListeners) {
            l.onPredefinedSet(predefinedPaths.get(api));
        }
    }

}
