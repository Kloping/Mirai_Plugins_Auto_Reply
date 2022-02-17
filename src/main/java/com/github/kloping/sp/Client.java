package com.github.kloping.sp;

import com.github.kloping.Resource;
import com.github.kloping.sp.controllers.RestController0;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.core.io.support.SpringFactoriesLoader.FACTORIES_RESOURCE_LOCATION;

/**
 * @author github-kloping
 */
@SpringBootApplication(scanBasePackageClasses = RestController0.class)
public class Client {
    public static void main(String[] args) {
        ClassLoader classLoader = EnableAutoConfiguration.class.getClassLoader();
        System.out.println(classLoader);
        System.out.println(classLoader.getResource(FACTORIES_RESOURCE_LOCATION).getPath());
        Object port = Resource.conf == null ? 20044 : Resource.conf.getPort();
        args = new String[]{"--server.port=" + port.toString()};
        SpringApplication.run(Client.class, args);
    }
}
