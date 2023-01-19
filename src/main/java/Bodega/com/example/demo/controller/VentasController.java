package Bodega.com.example.demo.controller;

import Bodega.com.example.demo.model.Model;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
public class VentasController {
    private ReactiveMongoTemplate template;

    public VentasController(ReactiveMongoTemplate template) {
        this.template = template;
    }


    @Bean
    public RouterFunction<ServerResponse> History() {
        return route(
                GET("/history/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.findAll((Model.class), "Sales").collectList()
                        .flatMap(list -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromPublisher(Flux.fromIterable(list), Model.class)))
        );
    }
}
