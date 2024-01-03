package me.codefarm.gateway.weixin;

import static me.codefarm.gateway.weixin.mp.constants.SessionConstants.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.WebSession;

import io.swagger.v3.oas.annotations.Parameter;
import me.codefarm.gateway.weixin.mp.dto.Code;
import me.codefarm.gateway.weixin.mp.service.LoginService;
import reactor.core.publisher.Mono;

@RestController
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Parameter(name = "session", hidden = true)
    @PostMapping("/wx/login/code2session")
    public Mono<Void> code2Session(@RequestBody Code code, WebSession session) {
        return loginService
                .code2Session(code.getCode())
                .flatMap(c -> {
                    if (c.getErrCode() != 0) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                String.format("%s: %s", c.getErrCode(), c.getErrMsg())));
                    }
                    var attributes = session.getAttributes();
                    attributes.put(SESSION_KEY, c.getSessionKey());
                    attributes.put(OPEN_ID, c.getOpenId());
                    attributes.put(UNION_ID, c.getUnionId());
                    return Mono.empty();
                });
    }
}
