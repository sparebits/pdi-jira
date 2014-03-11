/*
 * Utils.java
 * Created on 25.09.2013 06:20:38
 */
package plugin.bg.sparebits.pdi.jira.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;


/**
 * @author Neyko Neykov, 2013
 */
public class Utils {

    private static Class<?> PKG = Utils.class;

    public static Label createLabel(Composite parent, Control up, String key) {
        Label l = new Label(parent, SWT.RIGHT);
        l.setText(BaseMessages.getString(PKG, key));
        FormData fd = new FormData();
        fd.top = new FormAttachment(up, Const.MARGIN);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(PropsUI.getInstance().getMiddlePct(), -Const.MARGIN);
        PropsUI.getInstance().setLook(l);
        l.setLayoutData(fd);
        return l;
    }

}
