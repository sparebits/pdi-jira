/*
 * Messages.java
 * Created on 23.03.2009 15:37:36
 */
package plugin.bg.sparebits.pdi.jira.ui;

import org.pentaho.di.i18n.BaseMessages;


/**
 * @author Neyko Neykov, 2009
 */
public class Messages {

    public static final String PACKAGE_NAME = Messages.class.getPackage().getName();

    public static String getString(String key) {
        return BaseMessages.getString(PACKAGE_NAME, key);
    }

}
