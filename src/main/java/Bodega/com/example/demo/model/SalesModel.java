package Bodega.com.example.demo.model;

import java.time.LocalDate;
import java.util.List;

public class SalesModel {

    private LocalDate name;
    private String IdType;
    private int ID;
    private String ClientNAme;
    private List <ProductDto> Products;

    public LocalDate getName() {
        return name;
    }

    public void setName(LocalDate name) {
        this.name = name;
    }

    public String getIdType() {
        return IdType;
    }

    public void setIdType(String idType) {
        IdType = idType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getClientNAme() {
        return ClientNAme;
    }

    public void setClientNAme(String clientNAme) {
        ClientNAme = clientNAme;
    }

    public List<ProductDto> getProducts() {
        return Products;
    }

    public void setProducts(List<ProductDto> products) {
        Products = products;
    }
}
