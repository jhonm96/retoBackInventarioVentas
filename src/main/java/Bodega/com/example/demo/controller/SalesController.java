package Bodega.com.example.demo.controller;
import Bodega.com.example.demo.domain.entities.SalesModel;
import Bodega.com.example.demo.servicios.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class SalesController {

    private final ReactiveMongoTemplate template;
    @Autowired
    private  SalesService salesService;

    public SalesController(ReactiveMongoTemplate template) {
        this.template = template;
    }

    @Bean
    public RouterFunction<ServerResponse> createSales() {
        return route(
                POST("/createSale/").and(accept(MediaType.APPLICATION_JSON)),
                request -> {
                    salesService.inventoryDiscount(request);
                    return salesService.createNewSale(request);

                });
    }


    @Bean
    public RouterFunction<ServerResponse> salesHistory() {
        return route(
                GET("/history/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.findAll(SalesModel.class, "Sales").collectList()
                        .flatMap(list -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromPublisher(Flux.fromIterable(list), SalesModel.class)))
        );
    }
}
