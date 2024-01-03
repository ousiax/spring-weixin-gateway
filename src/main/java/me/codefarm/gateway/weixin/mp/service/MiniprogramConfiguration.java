package me.codefarm.gateway.weixin.mp.service;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "weixin.mp")
@Data
public class MiniprogramConfiguration {

    private String appId;

    private String appSecret;

    private MiniprogramConfiguration.Uris Uris;

    @Data
    public static class Uris {
        private URI jscode2session;
        private URI token;
        private URI userphonenumber;
    }
}
