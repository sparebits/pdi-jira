/*
 * PdiJiraComposite.java
 * Created on 13.02.2015 13:38:42 
 */
package plugin.bg.sparebits.pdi.jira.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;


/**
 * Most of the used composite panels are initialized in a same way. This is
 * common base class for those composites
 * @author nneikov 2015
 */
public class PdiJiraComposite extends Composite {

    private PropsUI props = PropsUI.getInstance();

    /**
     * 
     */
    PdiJiraComposite(Composite parent) {
        super(parent, SWT.NONE);

        FormLayout fl = new FormLayout();
        fl.marginWidth = Const.FORM_MARGIN;
        fl.marginHeight = Const.FORM_MARGIN;
        setLayout(fl);
        props.setLook(this);
    }

    /**
     * Helper method for creating labels
     * @param parent
     * @param up
     * @param key
     * @return
     */
    protected Label createLabel(Composite parent, Control up, String key) {

        Label l = new Label(parent, SWT.RIGHT);
        l.setText(BaseMessages.getString(PdiJiraComposite.class, key));

        FormData fd = new FormData();
        fd.top = new FormAttachment(up, Const.MARGIN);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(PropsUI.getInstance().getMiddlePct(), -Const.MARGIN);
        PropsUI.getInstance().setLook(l);

        l.setLayoutData(fd);
        return l;
    }

}
