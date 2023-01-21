package Bodega.com.example.demo.servicios.interfaces;

import Bodega.com.example.demo.domain.entities.ProductModel;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface InventaryServiceInterface {
    Query findProduct(int id);

    Mono<ServerResponse> productUpdated(ServerRequest request);

    Mono<ProductModel> DeleteOldProduct(ServerRequest request);
}
