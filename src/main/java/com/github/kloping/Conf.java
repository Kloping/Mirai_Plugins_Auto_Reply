package com.github.kloping;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.kloping.file.FileUtils;
import io.github.kloping.initialize.FileInitializeValue;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author github-kloping
 */
public class Conf {
    private long host = -1;
    private Set<Long> followers = new LinkedHashSet<>();
    private Set<Long> deletes = new LinkedHashSet<>();
    private String insertKey = "开始添加";
    private String selectKey = "查询词";
    private String deleteKey = "删除词";
    private float cd = 0f;
    private String oneComSplit = " ";
    private String oneComInsert = "/添加";
    private boolean privateK = false;
    private int port = 20044;
    private String root = ".";
    private String dataPath = "conf/auto_reply/data.json";
    private String password = "";
    private Map<String, Boolean> map = new TreeMap<>();

    private Conf() {
    }

    public static final Conf getInstance(String root) {
        Conf conf = new Conf();
        conf.root = root;
        conf.dataPath = new File(root, conf.dataPath).getAbsolutePath();
        try {
            String p0 = new File(conf.root, "conf/auto_reply/conf.json").getAbsolutePath();
            JSONObject jo = JSON.parseObject(FileUtils.getStringFromFile(p0));
            if (jo.containsKey("map")){
                JSONObject j1 = jo.getJSONObject("map");
                String s1 = j1.keySet().iterator().next();
                try {
                    Long l=  Long.parseLong(s1);
                    jo.remove("map");
                } catch (NumberFormatException e) {
                }
            }
            FileUtils.putStringInFile(jo.toJSONString(), new File(p0));
            conf = FileInitializeValue.getValue(p0, conf, true);
            return conf;
        } catch (Exception e) {
            e.printStackTrace();
            return conf;
        }
    }

    public static Conf reload(Conf conf) {
        try {
            conf = FileInitializeValue.getValue(new File(conf.root, "conf/auto_reply/conf.json").getAbsolutePath(), conf, true);
            return conf;
        } catch (Exception e) {
            e.printStackTrace();
            return conf;
        }
    }

    public Conf apply() {
        return FileInitializeValue.putValues(new File(this.root, "conf/auto_reply/conf.json").getAbsolutePath(), this, true);
    }

    public Map<String, Boolean> getMap() {
        return map;
    }

    public void setMap(Map<String, Boolean> map) {
        this.map = map;
    }

    public Conf addF(long q) {
        followers.add(q);
        return this;
    }

    public Conf addC(long q) {
        deletes.add(q);
        return this;
    }

    public Conf removeF(long q) {
        followers.remove(q);
        return this;
    }

    public Conf removeC(long q) {
        deletes.remove(q);
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getHost() {
        return host;
    }

    public Conf setHost(long host) {
        this.host = host;
        return this;
    }

    public Set<Long> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<Long> followers) {
        this.followers = followers;
    }

    public Set<Long> getDeletes() {
        return deletes;
    }

    public void setDeletes(Set<Long> deletes) {
        this.deletes = deletes;
    }

    public String getInsertKey() {
        return insertKey;
    }

    public void setInsertKey(String insertKey) {
        this.insertKey = insertKey;
    }

    public String getSelectKey() {
        return selectKey;
    }

    public void setSelectKey(String selectKey) {
        this.selectKey = selectKey;
    }

    public String getDeleteKey() {
        return deleteKey;
    }

    public void setDeleteKey(String deleteKey) {
        this.deleteKey = deleteKey;
    }

    public float getCd() {
        return cd;
    }

    public Conf setCd(float cd) {
        this.cd = cd;
        return this;
    }

    public String getOneComSplit() {
        return oneComSplit;
    }

    public void setOneComSplit(String oneComSplit) {
        this.oneComSplit = oneComSplit;
    }

    public String getOneComInsert() {
        return oneComInsert;
    }

    public void setOneComInsert(String oneComInsert) {
        this.oneComInsert = oneComInsert;
    }

    public boolean isPrivateK() {
        return privateK;
    }

    public Conf setPrivateK(boolean privateK) {
        this.privateK = privateK;
        return this;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
}
