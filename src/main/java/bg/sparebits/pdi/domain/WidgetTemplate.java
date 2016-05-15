/*
 * WidgetTemplate.java
 * Created on 5.02.2015 16:55:16 
 */
package bg.sparebits.pdi.domain;

import java.io.Serializable;


/**
 * Base class for all templates in domain
 * @author nneikov 2015
 */
public class WidgetTemplate implements Serializable {

    private static final long serialVersionUID = 1869877242393536003L;

    private String name;

    /**
     * Name of the widget. Doesn't need to be unique
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
