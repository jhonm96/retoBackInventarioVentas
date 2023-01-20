package Bodega.com.example.demo.model;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.List;


public class SalesModel {
    @Id
    private int idclient;
    private Instant date = Instant.now();
    private String idtype;
    private String clientname;

    private List<Producto> products;

    public Instant getDate() {
        return date;
    }
    public void setDate(Instant date) {
        this.date = Instant.now();
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public int getIdclient() {
        return idclient;
    }

    public void setIdclient(int idclient) {
        this.idclient = idclient;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public List<Producto> getProducts() {
        return products;
    }

    public void setProducts(List<Producto> products) {
        this.products = products;
    }

}