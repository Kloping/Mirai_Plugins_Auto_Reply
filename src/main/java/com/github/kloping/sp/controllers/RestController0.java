package com.github.kloping.sp.controllers;

import com.alibaba.fastjson.JSONObject;
import com.github.kloping.Plugin0AutoReply;
import com.github.kloping.Resource;
import com.github.kloping.e0.AlarmClock;
import io.github.kloping.little_web.annotations.RequestMethod;
import io.github.kloping.little_web.annotations.RequestParm;
import io.github.kloping.little_web.annotations.WebRestController;
import net.mamoe.mirai.Bot;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public Map<String, Object> modify(@RequestParm("key") String key, @RequestParm("index") Integer index,
                                      @RequestParm("type") Integer type, @RequestParm("value") String value,
                                      HttpServletRequest request) {
        if (verify(request)) {
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
    public Map<String, Object> delete(@RequestParm("key") String key,
                                      @RequestParm("index") Integer index,
                                      @RequestParm("type") Integer type,
                                      @RequestParm("value") String value,
                                      HttpServletRequest request) {
        if (verify(request)) {
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
    public Object append(@RequestParm("k") String k, @RequestParm("v") String v, HttpServletRequest request) {
        if (verify(request)) {
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
            return ALARM_CLOCKS;
        } else {
            return null;
        }
    }
}
