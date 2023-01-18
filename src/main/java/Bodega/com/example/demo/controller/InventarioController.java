package Bodega.com.example.demo.controller;
import Bodega.com.example.demo.model.Model;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class InventarioController {

    private  ReactiveMongoTemplate template;

    public InventarioController(ReactiveMongoTemplate template) {
        this.template = template;
    }



    @Bean
    public RouterFunction<ServerResponse> createCategory() {
        return route(
                POST("/test/create/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.save(request.bodyToMono(Model.class), "TESTDEPRUEBA")
                        .then(ServerResponse.ok().build())
        );
    }

}
