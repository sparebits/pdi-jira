/*
 * LabelTemplate.java
 * Created on 5.02.2015 16:57:40 
 */
package bg.sparebits.pdi.domain;

/**
 * @author nneikov 2015
 */
public class LabelTemplate extends WidgetTemplate {

    private static final long serialVersionUID = -218351834809052784L;

    // FIXME it's possible other widgets to have same property too
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
