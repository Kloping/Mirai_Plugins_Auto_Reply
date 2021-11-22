package com.hrs.kloping;


import io.github.kloping.initialize.FileInitializeValue;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hrs.kloping.HPlugin_AutoReply.thisPath;

public class Conf {
    public String key = "开始添加";
    public String selectKey = "查询词";
    public String deleteKey = "删除词";
    public Long host = -1L;
    public List<Long> followers = new LinkedList<>();
    public String splitK = ":==>";
    public Map<Number, entity> list2e = new ConcurrentHashMap<>();
    public String OneComAddStr = "/添加";
    public String OneComAddSplit = " ";
    public boolean openPrivate = false;
    public float cd = 0;

    public Conf() {
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

    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }

    public List<Long> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Long> followers) {
        this.followers = followers;
    }

    public String getSplitK() {
        return splitK;
    }

    public Map<Number, entity> getList2e() {
        return list2e;
    }

    public String getOneComAddStr() {
        return OneComAddStr;
    }

    public void setOneComAddStr(String oneComAddStr) {
        OneComAddStr = oneComAddStr;
    }

    public String getOneComAddSplit() {
        return OneComAddSplit;
    }

    public void setOneComAddSplit(String oneComAddSplit) {
        OneComAddSplit = oneComAddSplit;
    }

    public boolean isOpenPrivate() {
        return openPrivate;
    }

    public void setOpenPrivate(boolean openPrivate) {
        this.openPrivate = openPrivate;
    }

    public float getCd() {
        return cd;
    }

    public void setCd(float cd) {
        this.cd = cd;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void apply() {
        FileInitializeValue.putValues(thisPath + "/conf/auto_reply/conf.json", this);
    }
}
