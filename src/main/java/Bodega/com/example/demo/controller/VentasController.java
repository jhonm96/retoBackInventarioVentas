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
                POST("/create/").and(accept(MediaType.APPLICATION_JSON)),
                request -> {
                    creacionDespuesdeValidacion(request).then();
                    return ServerResponse.ok().build();
                });
    }

    public Mono<Object> creacionDespuesdeValidacion(ServerRequest request) {
        return obtenerProductosCompra(request).flatMap(productoscompra -> {
            return validation(request).flatMapMany(listaproductosexistentesvalidada -> {
                var productoActualizado=ActualizacionInventario(listaproductosexistentesvalidada,productoscompra);

                var productos= productoActualizado.stream().map(guardadoproductosactualizados->
                        template.findAndReplace(findProductParaActualizar(guardadoproductosactualizados.getIdProduct()),
                                guardadoproductosactualizados,new FindAndReplaceOptions().returnNew(),Product.class,"Product"))
                                .collect(Collectors.toList());
                return Flux.fromIterable(productos);
            });
        });
    }

    public List<Product> ActualizacionInventario(List<Product> listaProductosExistentes, List<Producto> productosVenta) {
        return listaProductosExistentes.stream().map(actualizacion -> {
            var productoVendido = productosVenta.stream()
                    .filter(comparandoproducto -> comparandoproducto.getIdProduct() == actualizacion.getIdProduct()).findFirst().orElseThrow();
            actualizacion.setInventory(actualizacion.getInventory()-productoVendido.getCantidad());
            return actualizacion;
        }).collect(Collectors.toList());
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
        var listaId = productos.stream().map(p -> p.getIdProduct()).collect(Collectors.toList());
        return template.find(findProducts(listaId), Product.class, "Products")
                .collectList();
    }

    public Mono<Void> validacionExistencia(List<Product> listaProductosExistentes, List<Producto> productosVenta) {
        if (listaProductosExistentes.size() != productosVenta.size()) {
            throw new RuntimeException(productosVenta.size() - listaProductosExistentes.size() + "de los productos no esta disponible");
        }
        return Mono.empty();
    }

    private void validaciontopeventaProductosCompra(List<Product> productosexistentes, List<Producto> ProductosPorComprar) {
        productosexistentes.forEach(product -> {
            var productoActual = ProductosPorComprar.stream()
                    .filter(sale -> sale.getIdProduct() == product.getIdProduct())
                    .findFirst().orElseThrow();
            if (product.getMin() < productoActual.getCantidad() ||
                    product.getMax() > productoActual.getCantidad()) {
                throw new RuntimeException("No se puede realizar la compra por los topes");
            }
            if (productoActual.getCantidad() > product.getInventory()) {
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

    private Query findProducts(List<Integer> ID) {
        return new Query(Criteria.where("_id").in(ID));
    }
    private Query findProductParaActualizar(int ID) {
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
