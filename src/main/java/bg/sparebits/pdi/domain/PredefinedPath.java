/*
 * PredefinedPath.java
 * Created on 4.03.2015 г. 6:28:33 
 */
package bg.sparebits.pdi.domain;

import java.io.Serializable;


/**
 * @author Neyko Neykov 2015
 */
public class PredefinedPath implements Serializable {

    private static final long serialVersionUID = 6778837019238189802L;

    private String name;
    private String pattern;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }
    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(name).append(":").append(pattern).toString();
    }

}
