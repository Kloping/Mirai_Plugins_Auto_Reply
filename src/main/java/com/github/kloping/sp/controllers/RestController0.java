package com.github.kloping.sp.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.kloping.Plugin0AutoReply;
import com.github.kloping.Resource;
import com.github.kloping.e0.AlarmClock;
import com.github.kloping.sp.RequestData;
import io.github.kloping.little_web.annotations.RequestBody;
import io.github.kloping.little_web.annotations.RequestMethod;
import io.github.kloping.little_web.annotations.RequestParm;
import io.github.kloping.little_web.annotations.WebRestController;
import net.mamoe.mirai.Bot;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.kloping.Resource.*;

/**
 * @author github.kloping
 */
@WebRestController
public class RestController0 {
    private boolean verify(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return false;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("key".equals(cookie.getName())) {
                if (cookie.getValue().equals(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequestMethod("/modify")
    public Map<String, Object> modify(
            @RequestBody String body,
            HttpServletRequest request) {
        if (verify(request)) {
            RequestData data = JSON.parseObject(body, RequestData.class);
            Integer type = data.getType();
            Integer index = data.getIndex();
            String key = data.getKey();
            String value = data.getValue();
            boolean k = false;
            switch (type) {
                case 0:
                    k = modifyData(key, index, value);
                    break;
                case 1:
                    k = modifyWeight(key, index, Integer.valueOf(value));
                    break;
                case 2:
                    k = modifyKey(key, index, value);
                    break;
                case 3:
                    k = modifyState(key, index, Integer.valueOf(value));
                    break;
                default:
                    break;
            }
            if (k) {
                sourceMap();
                Plugin0AutoReply.INSTANCE.getLogger().debug(String.format("succeeded in modifying one(%s,%s,%s,%s)", key, index, type, value));
            } else {
                Plugin0AutoReply.INSTANCE.getLogger().debug(String.format("failed in modifying one(%s,%s,%s,%s)", key, index, type, value));
            }
            return entityMap;
        }
        return null;
    }

    @RequestMethod("/delete")
    public Map<String, Object> delete(@RequestBody String body,
                                      HttpServletRequest request) {
        if (verify(request)) {
            RequestData data = JSON.parseObject(body, RequestData.class);
            Integer type = data.getType();
            Integer index = data.getIndex();
            String key = data.getKey();
            String value = data.getValue();
            boolean k = false;
            switch (type) {
                case 0:
                    k = deleteData(key, index, value);
                    break;
                case 1:
                case 2:
                case 3:
                    k = deleteM(key, value);
                    break;
                default:
                    break;
            }
            if (k) {
                sourceMap();
                Plugin0AutoReply.INSTANCE.getLogger().debug(String.format("succeeded in deleted one(%s,%s,%s,%s)", key, index, type, value));
            } else {
                Plugin0AutoReply.INSTANCE.getLogger().debug(String.format("failed in deleted one(%s,%s,%s,%s)", key, index, type, value));
            }
            return entityMap;
        }
        return null;
    }

    @RequestMethod("/search")
    public Object search(@RequestParm("value") String value, HttpServletRequest request) {
        if (verify(request)) {
            try {
                return trySearch(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequestMethod("/append")
    public Object append(
            @RequestBody String body,
            HttpServletRequest request) {
        if (verify(request)) {
            RequestData data = JSON.parseObject(body, RequestData.class);
            String k = data.getKey();
            String v = data.getValue();
            if ("添加完成".equals(Resource.append(k, v))) {
                return entityMap;
            } else {
                Plugin0AutoReply.INSTANCE.getLogger().debug(String.format("failed in append one(%s,%s)", k, v));
            }
        }
        return null;
    }

    @RequestMethod("/get_all")
    public Object all(HttpServletRequest request) {
        if (verify(request)) {
            return entityMap;
        } else {
            return null;
        }
    }

    @RequestMethod("/favicon.ico")
    public void favicon(HttpServletResponse response) {
        try {
            response.sendRedirect("http://q1.qlogo.cn/g?b=qq&nk=" + (Bot.getInstances().size() > 0 ?
                    Bot.getInstances().get(0).getId() : "") + "&s=640");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMethod("/")
    public void index(@RequestParm("key") String key, HttpServletResponse response) throws IOException {
        if (key != null && key.equals(uuid)) {
            Cookie cookie = new Cookie("key", key);
            response.addCookie(cookie);
            response.sendRedirect("/index.html");
        } else {
            response.sendRedirect("https://www.baidu.com");
        }
    }

    @RequestMethod("/getBotId")
    public String getBotId() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "-1");
        for (Bot instance : Bot.getInstances()) {
            jsonObject.put("id", instance.getId());
            jsonObject.put("name", instance.getNick());
            jsonObject.put("icon", instance.getAvatarUrl());
            return jsonObject.toJSONString();
        }
        return jsonObject.toJSONString();
    }

    @RequestMethod("/getAlarms")
    public List<AlarmClock> getAlarmClocks(HttpServletRequest request) {
        if (verify(request)) {
            return ALARM_CLOCKS;
        } else {
            return null;
        }
    }

    @RequestMethod("/changeStateAlarmClock")
    public List<AlarmClock> changeStateAlarmClock(HttpServletRequest request, @RequestParm("uuid") String uuid) {
        if (verify(request)) {
            for (AlarmClock alarmClock : ALARM_CLOCKS) {
                if (uuid.equals(alarmClock.getUuid())) {
                    alarmClock.setEnable(!alarmClock.isEnable());
                }
            }
            saveAlarmClocks();
            return ALARM_CLOCKS;
        } else {
            return null;
        }
    }

    @RequestMethod("/changeWeekStateAlarmClock")
    public List<AlarmClock> changeWeekStateAlarmClock(HttpServletRequest request,
                                                      @RequestParm("uuid") String uuid,
                                                      @RequestParm("st") Integer st
    ) {
        if (verify(request)) {
            for (AlarmClock alarmClock : ALARM_CLOCKS) {
                if (uuid.equals(alarmClock.getUuid())) {
                    List<Integer> list = asList(alarmClock.getWeeks());
                    if (list.contains(st)) {
                        list.remove((Object) st);
                    } else {
                        list.add(st);
                    }
                    alarmClock.setWeeks(list.toArray(new Integer[0]));
                    System.out.println(list);
                }
            }
            saveAlarmClocks();
            return ALARM_CLOCKS;
        } else {
            return null;
        }
    }

    private List<Integer> asList(Integer[] weeks) {
        List<Integer> integers = new ArrayList<>();
        for (Integer week : weeks) {
            integers.add(week);
        }
        return integers;
    }
}
