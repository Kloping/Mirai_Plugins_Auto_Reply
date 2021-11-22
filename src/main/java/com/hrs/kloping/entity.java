package com.hrs.kloping;

import net.mamoe.mirai.message.data.MessageChain;

public class entity {
    private Number q = -1;
    private String k = null;
    private MessageChain v = null;

    public entity(Number q) {
        this.q = q;
    }

    public boolean isOk() {
        if (k != null && v != null) return true;
        else return false;
    }

    public Number getQ() {
        return q;
    }

    public void setQ(Number q) {
        this.q = q;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k.replaceAll("%\\?", ".{0,}").replaceAll("%\\+", ".+").replaceAll("%", ".{1,1}");
    }

    public MessageChain getV() {
        return v;
    }

    public void setV(MessageChain v) {
        this.v = v;
    }
}
