package com.github.kloping.sp;

import com.github.kloping.Plugin0AutoReply;
import com.github.kloping.Resource;
import io.github.kloping.MySpringTool.StarterApplication;
import io.github.kloping.MySpringTool.annotations.CommentScan;
import io.github.kloping.little_web.WebExtension;
import io.github.kloping.little_web.conf.TomcatConfig;

import static com.github.kloping.Resource.uuid;

/**
 * @author github.kloping
 */
@CommentScan(path = "com.github.kloping.sp.controllers")
public class Starter {
    public static void main(String[] args) {
        try {
            TomcatConfig.DEFAULT.setPort(Resource.conf.getPort());
            StarterApplication.SCAN_LOADER = Plugin0AutoReply.class.getClassLoader();
            StarterApplication.run(Starter.class);
            Plugin0AutoReply.INSTANCE.getLogger().info("AutoReply 服务启动成功 address: http://localhost:" + Resource.conf.getPort() + "?key=" + uuid);
        } catch (Throwable e) {
            System.err.println("AutoReply服务启动异常(请检查端口是否被占用)");
            e.printStackTrace();
        }
    }
}
