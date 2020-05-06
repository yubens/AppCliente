package ar.com.idus.www.appcliente.models;

import java.io.Serializable;
import java.util.ArrayList;

public class HeadOrder implements Serializable {
    private String idOrder;
    private String idCustomer;
    private String dateOrder;
    private String dateStart;
    private String dateEnd;
    private String dateDelivery;
    private String geo;
    private String observations;
    private ArrayList<BodyOrder> bodyOrders;

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getDateDelivery() {
        return dateDelivery;
    }

    public void setDateDelivery(String dateDelivery) {
        this.dateDelivery = dateDelivery;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public ArrayList<BodyOrder> getBodyOrders() {
        return bodyOrders;
    }

    public void setBodyOrders(ArrayList<BodyOrder> bodyOrders) {
        this.bodyOrders = bodyOrders;
    }
}
