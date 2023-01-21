package Bodega.com.example.demo.servicios;

import Bodega.com.example.demo.domain.entities.ProductModel;
import Bodega.com.example.demo.servicios.interfaces.InventaryServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
@Service
public class InventaryService implements InventaryServiceInterface {
    @Autowired
    private ReactiveMongoTemplate template;
    @Override
    public Query findProduct(int id) {
        return new Query(Criteria.where("idproducts").is(id));
    }
    @Override
    public Mono<ServerResponse> productUpdated(ServerRequest request){
        return template.save(request.bodyToMono(ProductModel.class), "Products").then(ServerResponse.ok().build());
    }
    @Override
    public Mono<ProductModel> DeleteOldProduct(ServerRequest request){
        return template.findAndRemove(findProduct(Integer.parseInt(request.pathVariable("idproducts"))), (ProductModel.class), "Products");
    }
}
