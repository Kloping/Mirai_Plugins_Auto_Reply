package io.github.kloping.autoReply.cron;

/**
 * @author github.kloping
 */
public class CronEntity {
    private String cron;
    private String code;
    private Long targetId;
    private Long botId = -1L;
    private String type;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getBotId() {
        return botId;
    }

    public void setBotId(Long botId) {
        this.botId = botId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
