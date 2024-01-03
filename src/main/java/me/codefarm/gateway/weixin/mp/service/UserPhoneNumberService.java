package me.codefarm.gateway.weixin.mp.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import me.codefarm.gateway.weixin.mp.dto.Code;
import me.codefarm.gateway.weixin.mp.dto.UserPhoneNumber;
import reactor.core.publisher.Mono;

@Service
public final class UserPhoneNumberService {
    private final WebClient client = WebClient.create();
    private final TokenService tokenService;
    private final MiniprogramConfiguration conf;

    public UserPhoneNumberService(TokenService tokenService, MiniprogramConfiguration conf) {
        this.tokenService = tokenService;
        this.conf = conf;
    }

    public Mono<UserPhoneNumber> getUserPhoneNumber(Code code) {
        return tokenService.getAccessToken().flatMap(accessToken -> {
            return client
                    .post()
                    .uri(conf.getUris().getUserphonenumber().toString(), b -> {
                        return b
                                .queryParam("access_token", accessToken.getAccessToken())
                                .build();
                    })
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(code), Code.class)
                    .retrieve()
                    .bodyToMono(UserPhoneNumber.class);
        });
    }
}
