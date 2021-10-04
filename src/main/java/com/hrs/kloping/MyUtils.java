package com.hrs.kloping;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MyUtils {
    public static synchronized final String getStringFromFile(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (line.contains("#") || line.trim().isEmpty()) continue;
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized final String[] getStringsFromFile(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                return null;
            }
            List<String> list = new LinkedList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.contains("#")) continue;
                list.add(line);
            }
            br.close();
            return list.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized final boolean appendStringInFile(String filepath, String line, boolean newline) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"), true);
            if (newline)
                pw.println();
            pw.println(line);
            pw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized final boolean putStringInFile(String filepath, String... lines) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"), true);
            for (String line : lines)
                pw.println(line);
            pw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final Map<String, String> his_temp = new ConcurrentHashMap<>();

    public static final <T extends String> String mather(String key, Set<T> arr) {
        if (arr.contains(key)) return key;
        else {
            if (his_temp.containsKey(key) && arr.contains(his_temp.get(key)))
                return his_temp.get(key);
            for (String s1 : arr) {
                try {
                    if (key.matches(s1)) {
                        his_temp.put(key, s1);
                        return s1;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return null;
    }
}
