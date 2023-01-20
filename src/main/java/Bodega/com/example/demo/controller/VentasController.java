package Bodega.com.example.demo.controller;

import Bodega.com.example.demo.model.Product;
import Bodega.com.example.demo.model.Producto;
import Bodega.com.example.demo.model.SalesModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                POST("/createventa/").and(accept(MediaType.APPLICATION_JSON)),
                request -> {
                    descuentodeInventario(request).doOnNext((mono)-> mono.subscribe());
                    return creacionDelaventa(request);

                });
    }
    //creacionDelaventa(request).then(ServerResponse.ok().build())


    public Mono<ServerResponse> creacionDelaventa(ServerRequest request) {
        return template.save(request.bodyToMono(SalesModel.class), "Sales")
                .then(ServerResponse.ok().build());
    }

    public Flux<Mono<Product>> descuentodeInventario(ServerRequest request) {
        return obtenerProductosCompra(request).flatMapMany(productoscompra -> {
            return validation(request).flatMapMany(listaproductosexistentesvalidada -> {
                var productoActualizado = ActualizacionInventario(listaproductosexistentesvalidada, productoscompra);
                var productos = productoActualizado.stream().map(guardadoproductosactualizados ->
                        template.findAndReplace(findProductParaActualizar(guardadoproductosactualizados.getIdproducts()),
                                guardadoproductosactualizados, new FindAndReplaceOptions().returnNew(), Product.class, "Product")).toList();

                return Flux.fromIterable(productos);
            });
        });
    }

    public List<Product> ActualizacionInventario(List<Product> listaProductosExistentes, List<Producto> productosVenta) {
        return listaProductosExistentes.stream().map(actualizacion -> {
            var productoVendido = productosVenta.stream()
                    .filter(comparandoproducto -> comparandoproducto.getidProduct() == actualizacion.getIdproducts()).findFirst().orElseThrow();
             actualizacion.setInventory(actualizacion.getInventory() - productoVendido.getcantidad());
            return actualizacion;
        }).toList();
    }


    public Mono<List<Product>> validation(ServerRequest request) {
        return obtenerProductosCompra(request).flatMap(lista -> {
            return listaStock(lista).flatMap(p -> {
                validacionExistencia(p, lista);
                validacionStockminimo(p);
                validaciontopeventaProductosCompra(p, lista);
                return Mono.just(p);
            });
        });
    }

    public Mono<List<Producto>> obtenerProductosCompra(ServerRequest request) {
        return request.bodyToMono(SalesModel.class).flatMap(sales -> Mono.just(sales.getProducts()));
    }

    public Mono<List<Product>> listaStock(List<Producto> productos) {
        var listaId = productos.stream().map(Producto::getidProduct).toList();
        return template.find(findProducts(listaId), Product.class, "Products")
                .collectList();
    }

    public void validacionExistencia(List<Product> listaProductosExistentes, List<Producto> productosVenta) {
        if (listaProductosExistentes.size() != productosVenta.size()) {
            throw new RuntimeException(productosVenta.size() - listaProductosExistentes.size() + "de los productos no esta disponible");
        }
    }

    private void validaciontopeventaProductosCompra(List<Product> productosexistentes, List<Producto> productosPorComprar) {
        productosexistentes.forEach(product -> {
            var productoActual = productosPorComprar.stream()
                    .filter(sale -> sale.getidProduct() == product.getIdproducts())
                    .findFirst().orElseThrow();
            if (product.getMin() < productoActual.getcantidad() ||
                    product.getMax() > productoActual.getcantidad()) {
                throw new RuntimeException("No se puede realizar la compra por los topes");
            }
            if (productoActual.getcantidad() > product.getInventory()) {
                throw new RuntimeException("No hay suficientes unidades en inventario");
            }
        });
    }

    private void validacionStockminimo(List<Product> productosexistentes) {
        productosexistentes.forEach(product -> {
            if (product.getInventory() < product.getMin()) {
                throw new RuntimeException(product.getNombre() + " Sin stock minimo para venta");
            }
        });
    }

    private Query findProducts(List<Integer> id) {
        return new Query(Criteria.where("_id").in(id));
    }

    private Query findProductParaActualizar(int id) {
        return new Query(Criteria.where("_id").in(id));
    }

    @Bean
    public RouterFunction<ServerResponse> historyOfSales() {
        return route(
                GET("/history/").and(accept(MediaType.APPLICATION_JSON)),
                request -> template.findAll(SalesModel.class, "Sales").collectList()
                        .flatMap(list -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromPublisher(Flux.fromIterable(list), SalesModel.class)))
        );
    }
}
