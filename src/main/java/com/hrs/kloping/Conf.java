package com.hrs.kloping;


import io.github.kloping.initialize.FileInitializeValue;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

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

    private Conf() {
    }

    public static final Conf getInstance(String root) {
        Conf conf = new Conf();
        conf.root = root;
        conf.dataPath = new File(root, conf.dataPath).getAbsolutePath();
        try {
            conf = FileInitializeValue.getValue(new File(conf.root, "conf/auto_reply/conf.json").getAbsolutePath(), conf, true);
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

    public Conf addF(long q) {
        followers.add(q);
        return this;
    }

    public Conf addC(long q) {
        deletes.add(q);
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

    public void setCd(float cd) {
        this.cd = cd;
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

    public void setPrivateK(boolean privateK) {
        this.privateK = privateK;
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
