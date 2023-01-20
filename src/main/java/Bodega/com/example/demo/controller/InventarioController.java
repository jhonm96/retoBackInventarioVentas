package Bodega.com.example.demo.controller;

import Bodega.com.example.demo.model.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class InventarioController {

    private ReactiveMongoTemplate template;

    public InventarioController(ReactiveMongoTemplate template) {
        this.template = template;
    }


    @Bean
    public RouterFunction<ServerResponse> createCategory() {
        return route(
                POST("/create/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.save(request.bodyToMono(Product.class), "Products")
                        .then(ServerResponse.ok().build())
        );
    }

    @Bean
    public RouterFunction<ServerResponse> listarCategory() {
        return route(
                GET("/list/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.findAll((Product.class), "Products").collectList()
                        .flatMap(list -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromPublisher(Flux.fromIterable(list), Product.class)))
        );
    }

    @Bean
    public RouterFunction<ServerResponse> UpdateProduct() {
        return route(
                PUT("/update/{name}").and(accept(MediaType.APPLICATION_JSON)),
                request ->{
                    return Eliminarproductoantiguo(request).flatMap(u->productoModificado(request));
                } );
    }

    public Mono<ServerResponse> productoModificado(ServerRequest request){
        return template.save(request.bodyToMono(Product.class), "Products").then(ServerResponse.ok().build());
    }
    public Mono<Product> Eliminarproductoantiguo(ServerRequest request){
        return template.findAndRemove(findProduct(request.pathVariable("name")), (Product.class), "Products");
    }


    @Bean
    public RouterFunction<ServerResponse> DeleteCategory() {
        return route(
                DELETE("/delete/{name}").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.findAndRemove(findProduct(request.pathVariable("name")), (Product.class), "Products")
                        .then(ServerResponse.ok().build())
        );
    }

    private Query findProduct(String name) {
        return new Query(Criteria.where("nombre").is(name));
    }
}
