package io.github.kloping.autoReply.sp;

import org.jetbrains.annotations.Nullable;

/**
 * @author github.kloping
 */
public class RequestData {
    @Nullable
    private String key, value;
    private Integer index, type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
