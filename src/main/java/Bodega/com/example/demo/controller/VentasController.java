package Bodega.com.example.demo.controller;

import Bodega.com.example.demo.model.Product;
import Bodega.com.example.demo.model.ProductDto;
import Bodega.com.example.demo.model.SalesModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
public class VentasController {
    private ReactiveMongoTemplate template;

    public VentasController(ReactiveMongoTemplate template) {
        this.template = template;
    }



    @Bean
    public RouterFunction<ServerResponse> createSales() {
        return route(
                POST("/create/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.save(request.bodyToMono(Product.class), "Sales")
                        .then(ServerResponse.ok().build())
        );
    }

    public void handleCreateSale(ServerRequest request){
        request.bodyToMono(SalesModel.class).flatMap(s -> {
            var productlist=s.getProducts();
            var listaIDProductos=productlist.stream().map(ProductDto::getIdProduct
            ).collect(Collectors.toList());
            template.find(findProducts(listaIDProductos),Product.class,"Products")
                    .collectList().doOnNext(p -> {
                        if (p.size()!= productlist.size()){
                            throw new RuntimeException(productlist.size()-p.size()+"de los productos no esta disponible");
                        }
                    })
        })
    }
    private Query findProducts(List <Integer> ID) {
        return new Query(Criteria.where("_id").in(ID));
    }

    @Bean
    public RouterFunction<ServerResponse> History() {
        return route(
                GET("/history/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.findAll((Product.class), "Sales").collectList()
                        .flatMap(list -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromPublisher(Flux.fromIterable(list), Product.class)))
        );
    }
}
