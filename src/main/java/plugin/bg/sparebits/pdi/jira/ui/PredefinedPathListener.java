/*
 * PredefinedPathListener.java
 * Created on 18.03.2015 г. 5:58:59 
 */
package plugin.bg.sparebits.pdi.jira.ui;

import java.util.List;

import bg.sparebits.pdi.domain.PredefinedPath;


/**
 * Instead of notifying selected Jira API, classes that implement this interface
 * will be notified about the patterns set so they can get it directly without
 * knowing what is the selected API. This is helpful when sub-call within API is
 * selected, and there are more than one level of API calls
 * 
 * @author Neyko Neykov 2015
 */
public interface PredefinedPathListener {

    void onPredefinedSet(List<PredefinedPath> patterns);

}
