/*
 * FieldsTab.java
 * Created on 17.03.2014 06:15:00 
 */
package plugin.bg.sparebits.pdi.jira.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugin.bg.sparebits.pdi.jira.JiraPluginMeta;
import bg.sparebits.pdi.domain.PredefinedPath;


/**
 * @author Neyko Neykov, 2014
 * 
 */
public class FieldsTab extends PdiJiraComposite implements PredefinedPathListener {

    private PropsUI props = PropsUI.getInstance();
    private JiraPluginMeta meta;

    private TableView fieldsTable;
    private ColumnInfo fieldsColumn;
    private ColumnInfo patternsColumn;

    private List<String> predefinedFields = new ArrayList<String>();
    private List<String> predefinedExpressions = new ArrayList<String>();
    private List<String> predefinedTypes = new ArrayList<String>();
    private List<PredefinedPath> predefinedPaths;

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 
     * @param parent
     * @param meta
     */
    public FieldsTab(Composite parent, JiraPluginMeta meta) {
        super(parent);
        this.meta = meta;
        loadPredefinedValues();
        createFieldsTable(this);
    }

    private void loadPredefinedValues() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/fields")));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                predefinedFields.add(tokens[0]);
                predefinedExpressions.add(tokens[1]);
                predefinedTypes.add(tokens[2]);
            }
        } catch (IOException e) {
            log.error("Failed to load predefined values", e);
        }
    }

    /**
     * 
     * @param parent
     */
    private void createFieldsTable(Composite parent) {
        Label label = createLabel(parent, null, "Output Fields");
        ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.BORDER);
        fieldsTable = new TableView(null, scroll, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI,//@formatter:off 
                new ColumnInfo[] {
                    fieldsColumn = new ColumnInfo("Field", ColumnInfo.COLUMN_TYPE_CCOMBO, predefinedFields.toArray(new String[] {}), false),
                    patternsColumn = new ColumnInfo("JSON Path", ColumnInfo.COLUMN_TYPE_CCOMBO, predefinedExpressions.toArray(new String[] {}), false),
                    new ColumnInfo("Type", ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMetaInterface.typeCodes, true)
                }, meta.getFieldNames() != null ? meta.getFieldNames().length : 0, null, props); //@formatter:on
        if (meta.getFieldNames() != null) {
            for (int i = 0; i < meta.getFieldNames().length; i++) {
                TableItem item = fieldsTable.table.getItem(i);
                item.setText(1, meta.getFieldNames()[i] != null ? meta.getFieldNames()[i] : "");
                item.setText(2, meta.getFieldExpressions()[i] != null ? meta.getFieldExpressions()[i] : "");
                item.setText(3, ValueMetaInterface.typeCodes[meta.getFieldTypes()[i]]);
            }
        }
        patternsColumn.setSelectionAdapter(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                CCombo combo = (CCombo) event.getSource();
                int selectedPattern = combo.getSelectionIndex();
                TableItem item = fieldsTable.table.getItem(fieldsTable.getSelectionIndex());
                item.setText(1, predefinedPaths.get(selectedPattern).getName());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        fieldsColumn.setSelectionAdapter(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                CCombo combo = (CCombo) event.getSource();
                int selectedPattern = combo.getSelectionIndex();
                TableItem item = fieldsTable.table.getItem(fieldsTable.getSelectionIndex());
                item.setText(2, predefinedPaths.get(selectedPattern).getPattern());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        fieldsTable.setRowNums();
        fieldsTable.optWidth(true);
        fieldsTable.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent evt) {
                meta.setChanged();
                fieldsTable.getRowWithoutValues();
            }
        });

        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        FormData fd = new FormData();
        fd.left = new FormAttachment(0, 0);
        fd.top = new FormAttachment(label, Const.MARGIN);
        fd.right = new FormAttachment(100, 0);
        fd.height = 250;
        scroll.setLayoutData(fd);
        scroll.setContent(fieldsTable);

    }

    private String[] getColumn(int column) {
        int n = fieldsTable.nrNonEmpty();
        String[] cells = new String[n];
        for (int i = 0; i < cells.length; i++) {
            TableItem item = fieldsTable.getNonEmpty(i);
            cells[i] = item.getText(column);
        }
        return cells;
    }

    public String[] getPredefinedFields() {
        return getColumn(1);
    }

    public String[] getPredefinedExpressions() {
        return getColumn(2);
    }

    public int[] getPredefinedTypes() {
        String[] selectedTypes = getColumn(3);
        int[] types = new int[selectedTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = getTypePosition(selectedTypes[i]);
        }
        return types;
    }

    private int getTypePosition(String type) {
        for (int i = 0; i < ValueMetaInterface.typeCodes.length; i++) {
            if (ValueMetaInterface.typeCodes[i].equals(type)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Handles the API selection and sets the appropriate predefined patterns
     * set
     */
    @Override
    public void onPredefinedSet(List<PredefinedPath> predefined) {
        predefinedPaths = predefined;
        patternsColumn.setComboValues(getPatterns(predefinedPaths));
        fieldsColumn.setComboValues(getFields(predefinedPaths));
    }

    private String[] getPatterns(List<PredefinedPath> paths) {
        if (paths == null) {
            return new String[] {};
        }
        String[] patterns = new String[paths.size()];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = paths.get(i).getPattern();
        }
        return patterns;
    }

    private String[] getFields(List<PredefinedPath> paths) {
        if (paths == null) {
            return new String[] {};
        }
        String[] fields = new String[paths.size()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = paths.get(i).getName();
        }
        return fields;
    }

}
