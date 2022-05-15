package com.github.kloping.e0;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.github.kloping.MyUtils.toStr;


/**
 * @author github.kloping
 */
@Data
@Setter
@Getter
@Accessors(chain = true)
@EqualsAndHashCode
public class AlarmClock {
    private int hour;
    private String uuid;
    private int minutes;
    private String content;
    private long targetId;
    private String type;
    private boolean enable = true;
    private long botId = -1;

    public String getHourStr() {
        return toStr(2, hour);
    }

    public String getMinutesStr() {
        return toStr(2, minutes);
    }
}
