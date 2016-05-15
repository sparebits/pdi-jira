/*
 * SearchApiMeta.java
 * Created on 19.03.2015 г. 10:41:35 
 */
package bg.sparebits.pdi.domain;

import java.io.Serializable;


/**
 * @author Neyko Neykov 2015
 */
public class SearchApiMeta implements Serializable {

    private static final long serialVersionUID = 3133976423025545280L;

    private String jql;

    /**
     * @return the jql
     */
    public String getJql() {
        return jql;
    }

    /**
     * @param hql the jql to set
     */
    public void setJql(String hql) {
        this.jql = hql;
    }

}
