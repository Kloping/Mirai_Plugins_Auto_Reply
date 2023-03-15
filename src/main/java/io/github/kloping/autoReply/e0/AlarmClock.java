package io.github.kloping.autoReply.e0;

import com.alibaba.fastjson.annotation.JSONField;
import io.github.kloping.autoReply.MyUtils;

import java.util.Arrays;


/**
 * @author github.kloping
 */
public class AlarmClock {
    private int hour;
    private String uuid;
    private int minutes;
    private String content;
    private long targetId;
    private String type;
    private boolean enable = true;
    private long botId = -1;
    private Integer[] weeks = new Integer[]{0, 1, 2, 3, 4, 5, 6};

    @JSONField(deserialize = false)
    public String getHourStr() {
        return MyUtils.toStr(2, hour);
    }

    @JSONField(deserialize = false)
    public String getMinutesStr() {
        return MyUtils.toStr(2, minutes);
    }

    public boolean enableToday() {
        return Arrays.asList(weeks).contains(MyUtils.getWeekOfDateSt());
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public long getBotId() {
        return botId;
    }

    public void setBotId(long botId) {
        this.botId = botId;
    }

    public Integer[] getWeeks() {
        return weeks;
    }

    public void setWeeks(Integer[] weeks) {
        this.weeks = weeks;
    }
}
