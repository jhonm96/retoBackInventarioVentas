package Bodega.com.example.demo.model;

public class Product {
        private int idproducts;
        private String nombre;
        private boolean enabled;
        private int inventory;
        private int min;
        private int max;

    public int getIdproducts() {
        return idproducts;
    }

    public void setIdproducts(int idproducts) {
        this.idproducts = idproducts;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}

