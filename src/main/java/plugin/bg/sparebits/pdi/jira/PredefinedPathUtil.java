/*
 * PredefinedPathUtil.java
 * Created on 5.03.2015 г. 5:59:02 
 */
package plugin.bg.sparebits.pdi.jira;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import bg.sparebits.pdi.domain.PredefinedPath;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * @author Neyko Neykov 2015
 */
public class PredefinedPathUtil {

    public static Map<String, List<PredefinedPath>> load(InputStream is) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(is), new TypeToken<Map<String, List<PredefinedPath>>>() {
        }.getType());
    }

    public static Map<String, List<PredefinedPath>> load(String resource) {
        return load(PredefinedPathUtil.class.getResourceAsStream(resource));
    }
}
