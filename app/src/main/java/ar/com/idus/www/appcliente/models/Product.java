package ar.com.idus.www.appcliente.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {
    @SerializedName("ID_PRODUCT")
    @Expose
    public String idProduct;

    @SerializedName("CODIGO")
    @Expose
    public String code;

    @SerializedName("CODIGOLIDER")
    @Expose
    public String leaderCode;

    @SerializedName("NOMBRE")
    @Expose
    public String name;

    @SerializedName("NOMBRE_RUBRO")
    @Expose
    public String categoryName;

    @SerializedName("NOMBRE_SUBRUBO")
    @Expose
    public String subCategoryName;

    @SerializedName("MULTIPLO")
    @Expose
    public String multiple;

    @SerializedName("PRECIO_VTA00")
    @Expose
    public String salePrice00;

    @SerializedName("PRECIOOFERTA")
    @Expose
    public String offerPrice;

    @SerializedName("PRECIO_LISTA00")
    @Expose
    public String listPrice00;

    @SerializedName("PRECIO_LISTA01")
    @Expose
    public String listPrice01;

    @SerializedName("PRECIO_LISTA02")
    @Expose
    public String listPrice02;

    private float realPrice;

    public float getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(float realPrice) {
        this.realPrice = realPrice;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLeaderCode() {
        return leaderCode;
    }

    public void setLeaderCode(String leaderCode) {
        this.leaderCode = leaderCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getMultiple() {
        return multiple;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public String getSalePrice00() {
        return salePrice00;
    }

    public void setSalePrice00(String salePrice00) {
        this.salePrice00 = salePrice00;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getListPrice00() {
        return listPrice00;
    }

    public void setListPrice00(String listPrice00) {
        this.listPrice00 = listPrice00;
    }

    public String getListPrice01() {
        return listPrice01;
    }

    public void setListPrice01(String listPrice01) {
        this.listPrice01 = listPrice01;
    }

    public String getListPrice02() {
        return listPrice02;
    }

    public void setListPrice02(String listPrice02) {
        this.listPrice02 = listPrice02;
    }
}
