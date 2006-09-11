package com.muddyhorse.table;

import java.util.HashMap;
import java.util.Map;

/**
 * A place to store row-based calculated data
 * @author pforhan
 *
 * @param <E>The type of row data object
 * TODO this could be refactored into a more general data context...
 */
public class TableModelContext<E>
{
    private Map<E, Map<String, Object>> dataStore = new HashMap<E, Map<String,Object>>();

    public boolean containsKey(E data, String key) {
        boolean rv;
        Map<String, Object> map = getMap(data, false);

        if (map != null) {
            rv = map.containsKey(key);

        } else {
            rv = false;
        } // endif

        return rv;
    }

    public Object get(E data, String key) {
        Object rv;
        Map<String, Object> map = getMap(data, false);

        if (map != null) {
            rv = map.containsKey(key);

        } else {
            rv = null;
        } // endif

        return rv;
    }

    public Object put(E data, String key, Object value) {
        return getMap(data, true).put(key, value);
    }

    private Map<String, Object> getMap(E data, boolean create) {
        Map<String, Object> rv = dataStore.get(data);
        if (rv == null
                && create) {
            rv = new HashMap<String, Object>();
            dataStore.put(data, rv);
        } // endif

        return rv;
    }
}
