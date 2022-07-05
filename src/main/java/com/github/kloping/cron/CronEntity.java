package com.github.kloping.cron;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author github.kloping
 */
@Data
@Setter
@Getter
@Accessors(chain = true)
@EqualsAndHashCode
public class CronEntity {
    private String cron;
    private String code;
    private Long targetId;
    private Long botId = -1L;
    private String type;
}
