/*
 * IssueApiMeta.java
 * Created on 17.03.2015 г. 6:28:41 
 */
package bg.sparebits.pdi.domain;

import java.io.Serializable;


/**
 * Instance is serialized to meta configuration to ease and structure the XML
 * and meta configuration persistance
 * @author Neyko Neykov 2015
 */
public class IssueApiMeta implements Serializable {

    private static final long serialVersionUID = -6357040016349140459L;

    private String key;
    private String subCall;

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }
    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the subCall
     */
    public String getSubCall() {
        return subCall;
    }
    /**
     * @param subCall the subCall to set
     */
    public void setSubCall(String subCall) {
        this.subCall = subCall;
    }

}
