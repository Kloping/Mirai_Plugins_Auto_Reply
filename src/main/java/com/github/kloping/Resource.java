package com.github.kloping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.kloping.sp.Client;
import io.github.kloping.initialize.FileInitializeValue;
import io.github.kloping.judge.Judge;
import net.mamoe.mirai.console.MiraiConsoleImplementation;
import net.mamoe.mirai.message.data.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.*;

import static com.github.kloping.MyUtils.tempMap;
import static io.github.kloping.file.FileUtils.testFile;
import static io.github.kloping.judge.Judge.isNotNull;

/**
 * @author github-kloping
 */
public class Resource {
    public static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(10, 10, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
    public static String rootPath = MiraiConsoleImplementation.getInstance().getRootPath().toFile().getAbsolutePath();
    public static Conf conf = Conf.getInstance(rootPath);
    public static Map<String, Object> entityMap = new ConcurrentHashMap<>();
    public static Set<String> illegalKeys = new CopyOnWriteArraySet<>();

    static {
        loadData(conf.getDataPath());
        loadIllegals();
        initUuid();
        startHtml();
    }

    public static void loadIllegals() {
        illegalKeys.clear();
        String ss = getStringFromFile(new File(conf.getRoot(), "conf/auto_reply/illegalKeys").getAbsolutePath());
        String[] sss = ss.split("\\s+");
        for (String s : sss) {
            if (s.trim().isEmpty()) {
                continue;
            }
            illegalKeys.add(s);
        }
        System.out.println(illegalKeys);
    }

    public static String getStringFromFile(String path) {
        try {
            if (!Judge.isNotNull(path)) return null;
            testFile(path);
            FileInputStream fis = new FileInputStream(path);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isIllegal(String v) {
        for (String illegalKey : illegalKeys) {
            if (v.contains(illegalKey)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIllegal(MessageChain c) {
        String v = getString(c);
        for (String illegalKey : illegalKeys) {
            if (v.contains(illegalKey))
                return true;
        }
        return false;
    }

    public static String getString(MessageChain c) {
        StringBuilder sb = new StringBuilder();
        for (SingleMessage datum : c) {
            if (datum instanceof PlainText) {
                sb.append(((PlainText) datum).getContent());
            } else if (datum instanceof Image) {
                sb.append("[图片]");
            } else if (datum instanceof At) {
                sb.append("[At:").append(((At) datum).getTarget()).append("]");
            } else {
                sb.append("[其他类型消息]");
            }
        }
        return sb.toString();
    }

    public static String uuid;

    public static void initUuid() {
        uuid = conf.getPassword().trim().isEmpty() ? UUID.randomUUID().toString() : conf.getPassword();
    }

    public static void startHtml() {
        try {
            Plugin0AutoReply.INSTANCE.getLogger().info("AutoReply 服务启动成功 address: http://localhost:" + conf.getPort() + "?key=" + uuid);
            Client.main(new String[]{});
        } catch (Exception e) {
            System.err.println("AutoReply 服务启动 异常 请检查 端口是否被占用");
            e.printStackTrace();
        }
    }

    public static void loadData(String dataPath) {
        entityMap = FileInitializeValue.getValue(dataPath, entityMap, true);
        Map<String, Entity> map = new ConcurrentHashMap<>();
        for (String k : entityMap.keySet()) {
            try {
                JSONObject v = (JSONObject) entityMap.get(k);
                Entity entity = v.toJavaObject(Entity.class);
                map.put(k, entity.deApply());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        entityMap.clear();
        entityMap.putAll(map);
    }

    public static void sourceMap() {
        indexed = false;
        tempMap.clear();
        FileInitializeValue.putValues(conf.getDataPath(), entityMap, true);
    }

    public static boolean modifyKey(String key, Integer index, String v) {
        Entity entity = (Entity) entityMap.get(key);
        entity.setTouchKey(v);
        entityMap.remove(key);
        entityMap.put(v, entity);
        sourceMap();
        return true;
    }

    public static boolean modifyWeight(String key, Integer index, Integer v) {
        Entity entity = (Entity) entityMap.get(key);
        int i = 0;
        Entity.Response0 response0 = null;
        for (Entity.Response0 r0 : entity.getVss()) {
            response0 = r0;
            if (i++ == index) break;
        }
        response0.setWeight(v);
        entity.deApply();
        return true;
    }

    public static boolean modifyState(String key, Integer index, Integer v) {
        Entity entity = (Entity) entityMap.get(key);
        int i = 0;
        Entity.Response0 response0 = null;
        for (Entity.Response0 r0 : entity.getVss()) {
            response0 = r0;
            if (i++ == index) break;
        }
        response0.setState(v);
        entity.deApply();
        return true;
    }

    public static boolean modifyData(String key, int index, String value) {
        Entity entity = (Entity) entityMap.get(key);
        int i = 0;
        Entity.Response0 response0 = null;
        for (Entity.Response0 r0 : entity.getVss()) {
            response0 = r0;
            if (i++ == index) break;
        }
        response0.setData(value);
        entity.deApply();
        return true;
    }

    public static boolean indexed = false;

    public static String trySearch(String v) throws Exception {
        if (!indexed) makeIndex();
        Map<String, Entity> em = new HashMap<>();
        for (char c : v.toCharArray()) {
            for (Entity entity : indexMap.get(c)) {
                em.put(entity.getTouchKey(), entity);
            }
        }
        return em.isEmpty() ? "{}" : JSON.toJSONString(em);
    }

    public static boolean deleteM(String key, String v) {
        if (entityMap.containsKey(key))
            entityMap.remove(key);
        return true;
    }

    public static boolean deleteData(String key, Integer index, String v) {
        Entity entity = (Entity) entityMap.get(key);
        int i = 0;
        Entity.Response0 response0 = null;
        for (Entity.Response0 r0 : entity.getVss()) {
            response0 = r0;
            if (i++ == index) break;
        }
        entity.getVss().remove(response0);
        entity.deApply();
        return true;
    }

    public static final Map<Character, Set<Entity>> indexMap = new HashMap<>();

    public synchronized static void makeIndex() {
        indexMap.clear();
        entityMap.forEach((k, v) -> {
            Entity entity = (Entity) v;
            for (char c : k.toCharArray()) {
                append(indexMap, c, entity);
            }
            for (Entity.Response r0 : entity.getVs()) {
                for (char c : r0.toString().toCharArray()) {
                    append(indexMap, c, entity);
                }
            }
        });
    }

    public static <K, V> void append(Map<K, Set<V>> map, K k, V v) {
        if (!isNotNull(map, k, v)) return;
        Set<V> list = map.get(k);
        if (list == null) list = new LinkedHashSet<>();
        list.add(v);
        map.put(k, list);
    }


    public static String append(String key, String value) {
        return OnCommand.ss(key, value, null);
    }
}
