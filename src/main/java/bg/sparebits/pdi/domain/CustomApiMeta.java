/*
 * CustomApiMeta.java
 * Created on 19.03.2015 г. 10:43:38 
 */
package bg.sparebits.pdi.domain;

import java.io.Serializable;


/**
 * @author Neyko Neykov 2015
 */
public class CustomApiMeta implements Serializable {

    private static final long serialVersionUID = -8235289120131686826L;

    private String uri;

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

}
