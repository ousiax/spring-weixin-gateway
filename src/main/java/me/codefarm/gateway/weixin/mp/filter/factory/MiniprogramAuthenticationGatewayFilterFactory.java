package me.codefarm.gateway.weixin.mp.filter.factory;

import static me.codefarm.gateway.weixin.mp.constants.SessionConstants.OPEN_ID;
import static me.codefarm.gateway.weixin.mp.constants.SessionConstants.SESSION_KEY;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

@Component
public class MiniprogramAuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<MiniprogramAuthenticationGatewayFilterFactory.Config> {
    public MiniprogramAuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return exchange.getSession()
                    .flatMap(session -> {
                        try {
                            session.getRequiredAttribute(SESSION_KEY);
                            session.getRequiredAttribute(OPEN_ID);
                        } catch (IllegalArgumentException iae) {
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                    "Use wx.login() to exhange session key first."));
                        }

                        return chain.filter(exchange);
                    });
        };
    }

    public static class Config {

    }
}
