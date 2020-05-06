package ar.com.idus.www.appcliente.models;

import java.io.Serializable;

public class BodyOrder implements Serializable {
    private String idItem;
    private String idProduct;
    private String quantity;
    private float price;
    private String name;
    private float total;

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
