package com.hrs.kloping;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

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
                if (line.startsWith("#")) continue;
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
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
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

}
