package com.github.kloping.sp.controllers;

import com.github.kloping.Plugin0AutoReply;
import com.github.kloping.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.github.kloping.Resource.*;

/**
 * @author github.kloping
 */
@RestController
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

    @RequestMapping("/modify")
    public Map<String, Object> modify(@RequestParam("key") String key,
                                      @RequestParam("index") Integer index,
                                      @RequestParam("type") Integer type,
                                      @RequestParam("value") String value,
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

    @RequestMapping("/delete")
    public Map<String, Object> delete(@RequestParam("key") String key,
                                      @RequestParam("index") Integer index,
                                      @RequestParam("type") Integer type,
                                      @RequestParam("value") String value,
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

    @RequestMapping("/search")
    public Object search(@RequestParam("value") String value, HttpServletRequest request) {
        if (verify(request)) {
            try {
                return trySearch(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequestMapping("/append")
    public Object append(@RequestParam("k") String k, @RequestParam("v") String v, HttpServletRequest request) {
        if (verify(request)) {
            if ("添加成功".equals(Resource.append(k, v))) {
                return entityMap;
            } else {
                Plugin0AutoReply.INSTANCE.getLogger().debug(String.format("failed in append one(%s,%s)", k, v));
            }
        }
        return null;
    }

    @RequestMapping("/get_all")
    public Object all(HttpServletRequest request) {
        if (verify(request)) {
            return entityMap;
        } else {
            return null;
        }
    }

    @RequestMapping("/favicon.ico")
    public void favicon(HttpServletResponse response) {
        try {
            response.sendRedirect("http://q1.qlogo.cn/g?b=qq&nk=3474006766&s=640");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/")
    public void index(@RequestParam("key") String key, HttpServletResponse response) throws IOException {
        if (key != null && key.equals(conf.getPassword())) {
            Cookie cookie = new Cookie("key", key);
            response.addCookie(cookie);
            response.sendRedirect("/index.html");
        } else {
            response.sendRedirect("https://www.baidu.com");
        }
    }
}
