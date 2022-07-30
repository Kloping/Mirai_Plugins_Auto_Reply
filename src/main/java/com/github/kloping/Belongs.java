package com.github.kloping;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author github.kloping
 */
public class Belongs {
    public Map<String, List<Long>> relation = new TreeMap<String, List<Long>>();

    public void load() {
    }

    public boolean isBelong(String key, Long id) {

        return false;
    }
}
