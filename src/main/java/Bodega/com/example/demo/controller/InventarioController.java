package Bodega.com.example.demo.controller;


import Bodega.com.example.demo.domain.entities.ProductModel;
import Bodega.com.example.demo.servicios.InventaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;


import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class InventarioController {

    private final ReactiveMongoTemplate template;

    @Autowired
    private InventaryService inventarioService;

    public InventarioController(ReactiveMongoTemplate template) {
        this.template = template;
    }


    @Bean
    public RouterFunction<ServerResponse> createProduct() {
        return route(
                POST("/create/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.save(request.bodyToMono(ProductModel.class), "Products")
                        .then(ServerResponse.ok().build())
        );
    }

    @Bean
    public RouterFunction<ServerResponse> productList() {
        return route(
                GET("/list/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.findAll((ProductModel.class), "Products").collectList()
                        .flatMap(list -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromPublisher(Flux.fromIterable(list), ProductModel.class)))
        );
    }

    @Bean
    public RouterFunction<ServerResponse> updateProduct() {
        return route(
                PUT("/update/{idproducts}").and(accept(MediaType.APPLICATION_JSON)),
                request ->{
                    return inventarioService.DeleteOldProduct(request)
                            .flatMap(u->inventarioService.productUpdated(request));
                } );
    }


    @Bean
    public RouterFunction<ServerResponse> deleteProduct() {
        return route(
                DELETE("/delete/{id}").and(accept(MediaType.APPLICATION_JSON)),
                request ->
                        template.findAndRemove(inventarioService.findProduct(Integer.parseInt(request.pathVariable("id"))),
                                        (ProductModel.class), "Products")
                        .then(ServerResponse.ok().build())
        );
    }
}
