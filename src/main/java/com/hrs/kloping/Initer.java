package com.hrs.kloping;

import io.github.kloping.initialize.FileInitializeValue;
import io.github.kloping.map.MapUtils;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.File;
import java.util.Arrays;

import static com.hrs.kloping.HPlugin_AutoReply.*;

public class Initer {
    public static boolean inited = false;

    public static final synchronized void Init() {
        if (!inited) {
            inited = true;
            conf = FileInitializeValue.getValue(thisPath + "/conf/auto_reply/conf.json", conf, true);
            if (new File(thisPath + "/conf/auto_reply/data.data").exists()) {
                String[] sss = MyUtils.getStringsFromFile(thisPath + "/conf/auto_reply/data.data");
                if (sss != null)
                    for (String ss : sss) {
                        try {
                            String[] ss2 = ss.split(conf.getSplitK());
                            if (ss2[0].trim().isEmpty()) continue;
                            if (ss2[1].trim().isEmpty()) continue;
                            MapUtils.append(k2vs, ss2[0], ss2[1]);
                        } catch (Exception e) {
                            continue;
                        }
                    }
                new File(thisPath + "/conf/auto_reply/data.data").delete();
            }
            k2vs.putAll(FileInitializeValue.getValue(thisPath + "/conf/auto_reply/data.json", k2vs, true));
            resourceMap();
            k2vs.forEach((k, v) -> {
                for (String m1 : v) {
                    MessageChain chain = MiraiCode.deserializeMiraiCode(m1);
                    MapUtils.append(k2v, k, chain);
                }
            });
            String lines = conf.getSplitK();
            lines = init("#在这里写入敏感词 以空格分割", "illegalKeys", conf.getKey());
            String[] ss = lines.trim().split("\\s+");
            illegalKeys.addAll(Arrays.asList(ss));
        }
    }

    private static synchronized String init(String tips, String fileName, String defaultStr) {
        try {
            String str = MyUtils.getStringFromFile(thisPath + "/conf/auto_reply/" + fileName);
            if (str == null || str.trim().isEmpty()) {
                MyUtils.putStringInFile(thisPath + "/conf/auto_reply/" + fileName, tips);
                return defaultStr;
            } else return str.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return defaultStr;
        }
    }

    private static synchronized <T> T init(String tips, String fileName, T defaultValue, Class<T> clas) {
        try {
            String str = MyUtils.getStringFromFile(thisPath + "/conf/auto_reply/" + fileName);
            if (str == null || str.trim().isEmpty()) {
                MyUtils.putStringInFile(thisPath + "/conf/auto_reply/" + fileName, tips);
            } else {
                if (clas == Long.class)
                    return (T) Long.valueOf(str.trim());
                else if (clas == boolean.class || clas == Boolean.class)
                    return (T) Boolean.valueOf(str.trim());
                else if (clas == float.class || clas == Float.class)
                    return (T) Float.valueOf(str.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }
}
