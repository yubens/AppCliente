package ar.com.idus.www.appcliente.models;

import java.io.Serializable;

public class Client implements Serializable {
    private String id;
    private String email;
    private String phone;
    private String name;
    private String address;
    private String pass;
    private String passRep;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPassRep() {
        return passRep;
    }

    public void setPassRep(String passRep) {
        this.passRep = passRep;
    }
}
