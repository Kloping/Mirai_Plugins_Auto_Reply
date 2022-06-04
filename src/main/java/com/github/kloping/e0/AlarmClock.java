package com.github.kloping.e0;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.kloping.MyUtils;
import io.github.kloping.MySpringTool.h1.impl.component.PackageScannerImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;

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
    private Integer[] weeks = new Integer[]{0, 1, 2, 3, 4, 5, 6};

    @JSONField(deserialize = false)
    public String getHourStr() {
        return toStr(2, hour);
    }

    @JSONField(deserialize = false)
    public String getMinutesStr() {
        return toStr(2, minutes);
    }

    public boolean enableToday() {
        return Arrays.asList(weeks).contains(MyUtils.getWeekOfDateSt());
    }
}
