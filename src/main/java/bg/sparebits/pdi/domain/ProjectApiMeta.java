/*
 * ProjectApiMeta.java
 * Created on 19.03.2015 г. 10:43:04 
 */
package bg.sparebits.pdi.domain;

import java.io.Serializable;


/**
 * @author Neyko Neykov 2015
 */
public class ProjectApiMeta implements Serializable {

    private static final long serialVersionUID = -7640354777037817039L;

    private String projectKey;

    /**
     * @return the key
     */
    public String getProjectKey() {
        return projectKey;
    }

    /**
     * @param key the key to set
     */
    public void setProjectKey(String key) {
        this.projectKey = key;
    }

}
