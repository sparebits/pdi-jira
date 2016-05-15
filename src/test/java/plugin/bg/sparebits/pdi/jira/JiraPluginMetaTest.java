/*
 * JiraPluginMetaTest.java
 * Created on 20.03.2015 г. 6:43:10 
 */
package plugin.bg.sparebits.pdi.jira;

import org.junit.Assert;
import org.junit.Test;

import bg.sparebits.pdi.domain.Api;
import bg.sparebits.pdi.domain.SearchApiMeta;


/**
 * @author Neyko Neykov 2015
 */
public class JiraPluginMetaTest {

    @Test
    public void empty() {
        JiraPluginMeta meta = new JiraPluginMeta();
        meta.setApiConfiguration("{\"key\":\"pjp-35\"}");
        SearchApiMeta searchMeta = (SearchApiMeta) meta.getApiConfiguration(Api.search);
        Assert.assertNotNull(searchMeta);
    }

}
