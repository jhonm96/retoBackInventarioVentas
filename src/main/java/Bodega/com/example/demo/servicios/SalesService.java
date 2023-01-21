package Bodega.com.example.demo.servicios;

import Bodega.com.example.demo.domain.entities.ProductModel;
import Bodega.com.example.demo.domain.entities.SalesProductsModel;
import Bodega.com.example.demo.domain.entities.SalesModel;
import Bodega.com.example.demo.servicios.interfaces.SalesServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
@Service
public class SalesService implements SalesServiceInterface {
    @Autowired
    private ReactiveMongoTemplate template;
    @Override
    public Mono<ServerResponse> createNewSale(ServerRequest request) {
        return template.save(request.bodyToMono(SalesModel.class), "Sales")
                .then(ServerResponse.ok().build());
    }
    @Override
    public Flux<List<ProductModel>> inventoryDiscount(ServerRequest request) {

        return getSoldProducts(request).flatMapMany(soldproducts -> {
            return validations(request).flatMapMany(productListValidated -> {
                var updatedProduct = inventoryUpdate(productListValidated, soldproducts);
                updatedProduct.stream().map(savingUpdatedProducts ->
                        template.findAndRemove(findProductParaActualizar(savingUpdatedProducts.getIdproducts()),
                                ProductModel.class, "Product"));
                return template.save(updatedProduct);
            });
        });
    }
    @Override
    public List<ProductModel> inventoryUpdate(List<ProductModel> inventoryProductList, List<SalesProductsModel> soldProductList) {
        return inventoryProductList.stream().map(update -> {
            var productSold = soldProductList.stream()
                    .filter(actualStockUpdate ->
                            actualStockUpdate.getidProduct() == update.getIdproducts()).findFirst().orElseThrow();
            update.setInventory(update.getInventory() - productSold.getquantity());
            return update;
        }).toList();
    }

    @Override
    public Mono<List<ProductModel>> validations(ServerRequest request) {
        return getSoldProducts(request).flatMap(list -> {
            return stockList(list).flatMap(p -> {
                stockValidation(p, list);
                minStockValidation(p);
                minMaxValidation(p, list);
                return Mono.just(p);
            });
        });
    }
    @Override
    public Mono<List<SalesProductsModel>> getSoldProducts(ServerRequest request) {
        return request.bodyToMono(SalesModel.class).flatMap(sales -> Mono.just(sales.getProducts()));
    }
    @Override
    public Mono<List<ProductModel>> stockList(List<SalesProductsModel> products) {
        var listaId = products.stream().map(SalesProductsModel::getidProduct).toList();
        return template.find(findProducts(listaId), ProductModel.class, "Products")
                .collectList();
    }
    @Override
    public void stockValidation(List<ProductModel> inventoryProductList, List<SalesProductsModel> soldProductList) {
        if (inventoryProductList.size() != soldProductList.size()) {
            throw new RuntimeException(soldProductList.size() - inventoryProductList.size() + " Products does not exist");
        }
    }
    @Override
    public void minMaxValidation(List<ProductModel> inventoryProductList, List<SalesProductsModel> soldProductList) {
        inventoryProductList.forEach(product -> {
            var soldProducts = soldProductList.stream()
                    .filter(sale -> sale.getidProduct() == product.getIdproducts())
                    .findFirst().orElseThrow();
            if (product.getMin() < soldProducts.getquantity() ||
                    product.getMax() > soldProducts.getquantity()) {
                throw new RuntimeException(" Does not meet the minimum or maximum value of units authorized for sale");
            }
            if (soldProducts.getquantity() > product.getInventory()) {
                throw new RuntimeException(" There are not enough units in stock");
            }
        });
    }
    @Override
    public void minStockValidation(List<ProductModel> inventoryProductList) {
        inventoryProductList.forEach(product -> {
            if (product.getInventory() < product.getMin()) {
                throw new RuntimeException(product.getName() + " There are less than the minimum number of units");
            }
        });
    }
    @Override
    public Query findProducts(List<Integer> id) {
        return new Query(Criteria.where("_id").in(id));
    }
    @Override
    public Query findProductParaActualizar(int id) {
        return new Query(Criteria.where("_id").in(id));
    }
}
