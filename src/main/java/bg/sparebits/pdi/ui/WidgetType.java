/*
 * WidgetType.java
 * Created on 4.02.2015 17:49:59 
 */
package bg.sparebits.pdi.ui;

/**
 * @author nneikov 2015
 */
public enum WidgetType {

    label("label"),
    text("text"),
    button("button");

    @SuppressWarnings("unused")
    private String type;

    private WidgetType(String type) {
        this.type = type;
    }

}
