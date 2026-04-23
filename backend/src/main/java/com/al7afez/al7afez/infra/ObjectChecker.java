package com.al7afez.al7afez.infra;

import java.util.Collection;
import java.util.Map;

public class ObjectChecker {
    public static boolean isEmptyOrNull(Object obj) {
        if (obj == null) return true;
        else if (obj instanceof String s && s.isEmpty()) return true;
        else if (obj instanceof Collection<?> c && c.isEmpty()) return true;
        else return obj instanceof Map<?, ?> m && m.isEmpty();
    }

    public static boolean isNotEmptyOrNull(Object obj) {
        return !isEmptyOrNull(obj);
    }
}
