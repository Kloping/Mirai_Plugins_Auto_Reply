package com.github.kloping.sp;

import com.github.kloping.Plugin0AutoReply;
import com.github.kloping.Resource;
import io.github.kloping.MySpringTool.StarterObjectApplication;
import io.github.kloping.MySpringTool.annotations.CommentScan;
import io.github.kloping.little_web.conf.TomcatConfig;

import static com.github.kloping.Resource.uuid;

/**
 * @author github.kloping
 */
@CommentScan(path = "com.github.kloping.sp.controllers")
public class Starter {
    public static void main(String[] args) {
        try {
            TomcatConfig config = new TomcatConfig();
            config.setPort(Resource.conf.getPort());
            config.setName("autoReply-web");
            StarterObjectApplication application = new StarterObjectApplication();
            application.PRE_SCAN_RUNNABLE.add(() -> {
                application.INSTANCE.getContextManager().append(config);
                application.INSTANCE.getContextManager().append("autoReply-servlet0", "servletName");
            });
            application.SCAN_LOADER = Plugin0AutoReply.class.getClassLoader();
            application.run0(Starter.class);
            Plugin0AutoReply.INSTANCE.getLogger().info("AutoReply 服务启动成功 address: http://localhost:" + Resource.conf.getPort() + "?key=" + uuid);
        } catch (Throwable e) {
            System.err.println("AutoReply服务启动异常(请检查端口是否被占用)");
            e.printStackTrace();
        }
    }
}
