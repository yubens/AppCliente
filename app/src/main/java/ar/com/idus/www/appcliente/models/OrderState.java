package ar.com.idus.www.appcliente.models;

import java.io.Serializable;

public class OrderState implements Serializable {
    private String id_order;
    private String date_order;
    private String date_delivery;
    private String cant_items;
    private String tot_order;
    private String state;

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public String getDate_order() {
        return date_order;
    }

    public void setDate_order(String date_order) {
        this.date_order = date_order;
    }

    public String getDate_delivery() {
        return date_delivery;
    }

    public void setDate_delivery(String date_delivery) {
        this.date_delivery = date_delivery;
    }

    public String getCant_items() {
        return cant_items;
    }

    public void setCant_items(String cant_items) {
        this.cant_items = cant_items;
    }

    public String getTot_order() {
        return tot_order;
    }

    public void setTot_order(String tot_order) {
        this.tot_order = tot_order;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
