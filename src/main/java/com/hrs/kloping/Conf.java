package com.hrs.kloping;


import com.alibaba.fastjson.annotation.JSONField;
import io.github.kloping.initialize.FileInitializeValue;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.hrs.kloping.HPlugin_AutoReply.thisPath;

public class Conf {
    public String key = "开始添加";
    public String selectKey = "查询词";
    public String deleteKey = "删除词";
    public Long host = -1L;
    public Set<Long> followers = new LinkedHashSet<>();
    //兼容旧的
    public String splitK = ":==>";
    @JSONField(serialize = false)
    public Map<Number, entity> list2e = new ConcurrentHashMap<>();
    public String oneComAddStr = "/添加";
    public String oneComAddSplit = " ";
    public boolean openPrivate = false;
    public Set<Long> canDeletes = new LinkedHashSet<>();
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

    public Set<Long> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<Long> followers) {
        this.followers = followers;
    }

    public void setSplitK(String splitK) {
        this.splitK = splitK;
    }

    public void setList2e(Map<Number, entity> list2e) {
        this.list2e = list2e;
    }

    public Set<Long> getCanDeletes() {
        return canDeletes;
    }

    public void setCanDeletes(Set<Long> canDeletes) {
        this.canDeletes = canDeletes;
    }

    public String getSplitK() {
        return splitK;
    }

    public Map<Number, entity> getList2e() {
        return list2e;
    }

    public String getOneComAddStr() {
        return oneComAddStr;
    }

    public void setOneComAddStr(String oneComAddStr) {
        this.oneComAddStr = oneComAddStr;
    }

    public String getOneComAddSplit() {
        return oneComAddSplit;
    }

    public void setOneComAddSplit(String oneComAddSplit) {
        this.oneComAddSplit = oneComAddSplit;
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
