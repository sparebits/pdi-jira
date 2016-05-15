/*
 * 
 */
package plugin.bg.sparebits.pdi.jira;

/**
 * @author nneikov
 * 
 */
public class JiraPluginException extends Exception {

    private static final long serialVersionUID = 2824483547385222367L;

    private String messageKey;

    /**
     * 
     * @param messageKey
     */
    public JiraPluginException(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * @return the messageKey
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * @param messageKey the messageKey to set
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }

}
