package com.github.kloping.cron;

import com.alibaba.fastjson.JSONObject;
import com.github.kloping.Resource;
import io.github.kloping.date.CronUtils;
import io.github.kloping.initialize.FileInitializeValue;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.code.MiraiCode;

import java.io.File;
import java.util.*;

/**
 * @author github.kloping
 */
public class Work {

    public static Work work = new Work();

    private List<CronEntity> entities = new ArrayList<>();

    private Map<String, Integer> cron2id = new HashMap<>();

    private File file;

    private void start() {
        String p0 = (file = new File(new File(Resource.conf.getDataPath()).getParentFile().getAbsolutePath(), "cron.json")).getAbsolutePath();
        for (Object o : FileInitializeValue.getValue(p0, entities, true)) {
            CronEntity ac = null;
            if (o instanceof CronEntity) {
                ac = (CronEntity) o;
            } else if (o instanceof JSONObject) {
                ac = ((JSONObject) o).toJavaObject(CronEntity.class);
            }
            if (ac != null)
                entities.add(ac);
        }

        entities.forEach((v) -> {
            int id = CronUtils.INSTANCE.addCronJob(v.getCron(), (c) -> {
                if (v.getBotId() > 0) {
                    if (Bot.getInstances().contains(v.getBotId())) {
                        send(Bot.getInstance(v.getBotId()), v);
                    }
                } else {
                    if (Bot.getInstances().size() > 0) {
                        send(Bot.getInstances().iterator().next(), v);
                    }
                }
            });
            cron2id.put(v.getCron(), id);
        });
    }

    private void send(Bot bot, CronEntity c0) {
        switch (c0.getType()) {
            case "g":
                bot.getGroup(c0.getTargetId()).sendMessage(MiraiCode.deserializeMiraiCode(c0.getCode()));
                break;
            case "u":
                bot.getFriend(c0.getTargetId()).sendMessage(MiraiCode.deserializeMiraiCode(c0.getCode()));
                break;
            default:
                break;
        }
    }

    public static void work() {
        work.start();
    }

    public static String delete(Integer st) {
        try {
            String cron = work.entities.get(st - 1).getCron();
            cron = cron.trim();
            if (work.cron2id.containsKey(cron)) {
                CronUtils.INSTANCE.stop(work.cron2id.get(cron));
            }
            Iterator<CronEntity> entityIterator = work.entities.iterator();
            while (entityIterator.hasNext()) {
                CronEntity entity = entityIterator.next();
                entity.getCron().equals(cron);
                entityIterator.remove();
            }
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String add(String cron, String targetId, String code) {
        try {
            String type = targetId.substring(0, 1);
            String id = targetId.substring(1);
            CronEntity entity = new CronEntity();
            entity.setCode(code).setType(type).setTargetId(Long.valueOf(id)).setCron(cron);
            work.entities.add(entity);
            work.toStart(entity);
            FileInitializeValue.putValues(work.file.getAbsolutePath(), work.entities, true);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private void toStart(CronEntity v) {
        int id = CronUtils.INSTANCE.addCronJob(v.getCron(), (c) -> {
            if (v.getBotId() > 0) {
                if (Bot.getInstances().contains(v.getBotId())) {
                    send(Bot.getInstance(v.getBotId()), v);
                }
            } else {
                if (Bot.getInstances().size() > 0) {
                    send(Bot.getInstances().iterator().next(), v);
                }
            }
        });
    }

    public static String list() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (CronEntity entity : work.entities) {
            sb.append(i++).append(".").append(entity.getCron()).append(" to ").append(entity.getType()).append(entity.getTargetId()).append(" ").append(entity.getCode()).append("\r\n");
        }
        return sb.length() == 0 ? "空" : sb.toString();
    }
}
