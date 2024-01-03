package me.codefarm.gateway.weixin.mp.service;

import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import me.codefarm.gateway.weixin.mp.dto.Code2session;
import reactor.core.publisher.Mono;

@Service
public final class LoginService {
    private final WebClient webClient;
    private MiniprogramConfiguration configuration;

    public LoginService(MiniprogramConfiguration configuration) {
        this.configuration = configuration;
        this.webClient = buildWebClient();
    }

    /**
     * 登录凭证校验。通过 wx.login 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程。更多使用方法详见小程序登录。
     * 
     * @see https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html
     */
    public Mono<Code2session> code2Session(String code) {
        return webClient
                .get()
                .uri(configuration.getUris().getJscode2session().toString(), builder -> {
                    return builder
                            .queryParam("appid", configuration.getAppId())
                            .queryParam("secret", configuration.getAppSecret())
                            .queryParam("js_code", code)
                            .queryParam("grant_type", "authorization_code").build();
                })
                .accept(MediaType.APPLICATION_JSON) // not working, the content-type always be text/plain.
                .exchangeToMono(resp -> {
                    return resp.bodyToMono(Code2session.class);
                });
    }

    /**
     * 校验服务器所保存的登录态 session_key 是否合法。为了保持 session_key 私密性，接口不明文传输
     * session_key，而是通过校验登录态签名完成。
     * 
     * @see https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/checkSessionKey.html
     */
    public boolean checkSessionKey() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    // The content-type of jscode2session always return text/plain, instead of
    // application/json ???
    //
    // curl -i -H Accept: application/json -XGET
    // https://api.weixin.qq.com/sns/jscode2session?appid=...
    //
    // HTTP/1.1 200 OK
    // Connection: keep-alive
    // Content-Type: text/plain
    // Date: Mon, 18 Sep 2023 06:41:48 GMT
    // Content-Length: 74
    private WebClient buildWebClient() {
        ObjectMapper objectMapper = JsonMapper.builder().build();
        // Configure Jackson2JsonDecoder to accept text/plain as application/json
        Jackson2JsonDecoder jsonDecoder = new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN);
        // Set custom ExchangeStrategies with the modified Jackson2JsonDecoder
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(jsonDecoder))
                .build();
        // Create a new WebClient instance with the custom ExchangeStrategies
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}
