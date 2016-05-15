/*
 * PredefinedPathUtilTest.java
 * Created on 5.03.2015 г. 6:07:12 
 */
package plugin.bg.sparebits.pdi.jira;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bg.sparebits.pdi.domain.PredefinedPath;


/**
 * @author Neyko Neykov 2015
 */
public class PredefinedPathUtilTest {

    private static Logger log = LoggerFactory.getLogger(PredefinedPathUtilTest.class);

    @Test
    public void load() {
        Map<String, List<PredefinedPath>> paths = PredefinedPathUtil.load("/predefined.json");
        Assert.assertNotNull(paths);
        log.info(paths.toString());
    }

}
