package com.louis.tp2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class Tp2Router {

    @Bean
    public RouterFunction<ServerResponse> route(Tp2Handler handler) {
        return RouterFunctions.route()
                .GET("/rh/ping", handler::pong)
                .GET("/rh/langues", handler::langues)
                .POST("rh/ajouterLangue", handler::ajouterLangue)
                .GET("rh/{langue}/anagrammesStrict/{mot}", handler::anagrammesStrict)
                .build();
    }
}
