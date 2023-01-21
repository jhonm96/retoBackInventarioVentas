package Bodega.com.example.demo.servicios.interfaces;

import Bodega.com.example.demo.domain.entities.ProductModel;
import Bodega.com.example.demo.domain.entities.SalesProductsModel;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SalesServiceInterface {
    Mono<ServerResponse> createNewSale(ServerRequest request);

    Flux<List<ProductModel>> inventoryDiscount(ServerRequest request);

    List<ProductModel> inventoryUpdate(List<ProductModel> listaProductosExistentes, List<SalesProductsModel> productosVenta);

    Mono<List<ProductModel>> validations(ServerRequest request);

    Mono<List<SalesProductsModel>> getSoldProducts(ServerRequest request);

    Mono<List<ProductModel>> stockList(List<SalesProductsModel> productos);

    void stockValidation(List<ProductModel> listaProductosExistentes, List<SalesProductsModel> productosVenta);

    void minMaxValidation(List<ProductModel> productosexistentes, List<SalesProductsModel> productosPorComprar);

    void minStockValidation(List<ProductModel> productosexistentes);

    Query findProducts(List<Integer> id);

    Query findProductParaActualizar(int id);
}
