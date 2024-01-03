package me.codefarm.gateway.weixin.mp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import reactor.core.publisher.Mono;

@Service
public class TokenService {
    private final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final WebClient client = WebClient.create();
    private final MiniprogramConfiguration configuration;
    private Token token;

    public TokenService(MiniprogramConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * 获取小程序全局唯一后台接口调用凭据，token有效期为7200s，开发者需要进行妥善保存。
     * 
     * @see https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/mp-access-token/getAccessToken.html
     */
    public Mono<Token> getAccessToken() {
        if (token == null) {
            return acquireToken().flatMap(t -> {
                this.token = t;
                return Mono.just(t);
            });
        }

        return Mono.just(token);
    }

    // access_token 的存储与更新
    //
    // * access_token 的存储至少要保留 512 个字符空间；
    // * access_token 的有效期目前为 2 个小时，需定时刷新，重复获取将导致上次获取的 access_token 失效；
    // * 建议开发者使用中控服务器统一获取和刷新 access_token，其他业务逻辑服务器所使用的 access_token
    // 均来自于该中控服务器，不应该各自去刷新，否则容易造成冲突，导致 access_token 覆盖而影响业务；
    // * access_token 的有效期通过返回的 expires_in
    // 来传达，目前是7200秒之内的值，中控服务器需要根据这个有效时间提前去刷新。在刷新过程中，中控服务器可对外继续输出的老
    // access_token，此时公众平台后台会保证在5分钟内，新老 access_token 都可用，这保证了第三方业务的平滑过渡；
    // * access_token 的有效时间可能会在未来有调整，所以中控服务器不仅需要内部定时主动刷新，还需要提供被动刷新 access_token
    // 的接口，这样便于业务服务器在API调用获知 access_token 已超时的情况下，可以触发 access_token 的刷新流程。
    @Scheduled(initialDelay = 60 * 60 * 1000, fixedRate = 90 * 60 * 1000) // init delay 60 minutes, and repeat every 90
                                                                          // minutes
    private void refreshAccessToken() {
        log.info("Starting to refresh token...");

        acquireToken().subscribe(t -> this.token = t);

        log.info("Completed to refresh token...");
    }

    private Mono<Token> acquireToken() {
        return client.get()
                .uri(configuration.getUris().getToken().toString(), uri -> {
                    return uri
                            .queryParam("appid", configuration.getAppId())
                            .queryParam("secret", configuration.getAppSecret())
                            .queryParam("grant_type", "client_credential")
                            .build();
                }).exchangeToMono(resp -> {
                    return resp.bodyToMono(Token.class);
                });
    }

    @Data
    public static class Token {
        /**
         * 获取到的凭证
         */
        @JsonProperty("access_token")
        private String accessToken;

        /**
         * 凭证有效时间，单位：秒。目前是7200秒之内的值。
         */
        @JsonProperty("expires_in")
        private int expiresIn;
    }
}
