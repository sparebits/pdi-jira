/*! ***************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package plugin.bg.sparebits.pdi.jira;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;


/**
 * @author nneikov 2013
 */
public class Util {

    /**
     * Recursively lists the JQL result fields
     * @param prefix
     * @param json
     * @return
     */
    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    public static Map<String, ValueMetaInterface> getFields(String prefix, Map<String, Object> json) {
        Map<String, ValueMetaInterface> fields = new TreeMap<String, ValueMetaInterface>();
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value instanceof ArrayList) {
                if (!((List) value).isEmpty() && ((List) value).get(0) instanceof Map) {
                    fields.putAll(getFields(prefix + key + "_", (Map<String, Object>) ((List) value).get(0)));
                }
            } else if (value instanceof Map) {
                if (!((Map) value).isEmpty()) {
                    fields.putAll(getFields(prefix + key + "_", (Map<String, Object>) value));
                }
            } else {
                ValueMetaInterface field;
                try {
                    field = ValueMetaFactory.createValueMeta(prefix + key, getType(value));
                } catch ( KettlePluginException e ) {
                    throw new RuntimeException( e );
                }
                fields.put(prefix + key, field);
            }
        }
        return fields;
    }

    private static int getType(Object value) {
        if (value instanceof Double) {
            return ValueMetaInterface.TYPE_NUMBER;
        } else if (value instanceof String) {
            return ValueMetaInterface.TYPE_STRING;
        } else if (value instanceof Boolean) {
            return ValueMetaInterface.TYPE_BOOLEAN;
        } else if (value != null) {
            System.out.println(value.getClass().getName());
        }
        return ValueMetaInterface.TYPE_NONE;
    }

    @SuppressWarnings({
        "unchecked"
    })
    public static List<Object[]> getRows(String prefix, Map<String, Object> json, RowMetaInterface rowMeta,
            List<Object[]> rows) {
        if (json.keySet().size() > 0) {
            Object[] row = new Object[rowMeta.size()];
            for (String key : json.keySet()) {
                Object value = json.get(key);
                if (value instanceof ArrayList) {
                    List<Object> valueList = (List<Object>) value;
                    if (!valueList.isEmpty() && valueList.get(0) instanceof Map) {
                        for (int i = 0; i < valueList.size(); i++) {
                            rows.addAll(getRows(prefix + key + "_", (Map<String, Object>) valueList.get(i), rowMeta,
                                    rows));
                        }
                    }
                } else if (value instanceof Map) {
                    Map<String, Object> valueMap = (Map<String, Object>) value;
                    if (!valueMap.isEmpty()) {
                        for (String mapKey : valueMap.keySet()) {
                            if (valueMap.get(mapKey) instanceof Map) {
                            } else {
                                int idx = getFieldPosition(prefix + key + "_" + mapKey, rowMeta);
                                if (idx >= 0) {
                                    row[getFieldPosition(prefix + key + "_" + mapKey, rowMeta)] = valueMap.get(mapKey);
                                }
                            }
                        }
                        rows.add(row);
                    }
                } else {
                    row[getFieldPosition(prefix + key, rowMeta)] = value;
                }
            }
            rows.add(row);
        }
        return rows;
    }

    public static int getFieldPosition(String fieldName, RowMetaInterface rowMeta) {
        for (int i = 0; i < rowMeta.size(); i++) {
            if (rowMeta.getFieldNames()[i].equals(fieldName)) {
                return i;
            }
        }
        return -1;
    }

}
