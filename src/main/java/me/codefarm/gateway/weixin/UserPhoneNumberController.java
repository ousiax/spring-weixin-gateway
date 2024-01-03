package me.codefarm.gateway.weixin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import me.codefarm.gateway.weixin.mp.dto.Code;
import me.codefarm.gateway.weixin.mp.dto.UserPhoneNumber.PhoneInfo;
import me.codefarm.gateway.weixin.mp.service.MiniprogramConfiguration;
import me.codefarm.gateway.weixin.mp.service.UserPhoneNumberService;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class UserPhoneNumberController {
    private final UserPhoneNumberService phoneNumberService;
    private final MiniprogramConfiguration conf;

    public UserPhoneNumberController(
            UserPhoneNumberService phoneNumberService,
            MiniprogramConfiguration configuration) {
        this.phoneNumberService = phoneNumberService;
        this.conf = configuration;
    }

    @PostMapping("/wx/userphonenumber")
    public Mono<PhoneInfo> getUserPhoneNumber(@RequestBody Code code) {
        return phoneNumberService
                .getUserPhoneNumber(code)
                .flatMap(r -> {
                    if (r.getErrCode() != 0) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                String.format("%s: %s", r.getErrCode(), r.getErrMsg())));
                    }

                    if (!conf.getAppId().equals(r.getPhoneInfo().getWatermark().getAppId())) {
                        var error = String.format("The appid {%s} of watermark is not consistent with the system {%s}.",
                                r.getPhoneInfo().getWatermark().getAppId(), conf.getAppId());
                        log.error(error);
                        return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error));
                    }

                    return Mono.just(r.getPhoneInfo());
                });
    }
}
