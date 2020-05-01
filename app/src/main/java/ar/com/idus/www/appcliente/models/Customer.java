package ar.com.idus.www.appcliente.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Customer implements Serializable {
    @SerializedName("CODIGO")
    private String codigo;
    private String EMPRESA_ID;
    private String NOMBRE;
    private String DOMICILIO;
    private String CUIT;
    private String DNI;
    private String SALDOCUENTACORRIENTE;
    private String TELEFONO_DISTRI;
    private String EMAIL_DISTRI;
    private String CODE_LISTA;
    private String NOMBRE_VENDEDOR;
    private String CANAL;
    private String LATITUD = null;
    private String LONGITUD = null;
    private String direccion_otorgada;
    private String email_otorgado;
    private String telefono_otorgado;
    private String contraseña;
    private String HABILITADO;


    // Getter Methods

    public String getCodigo() {
        return codigo;
    }

    public String getEMPRESA_ID() {
        return EMPRESA_ID;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public String getDOMICILIO() {
        return DOMICILIO;
    }

    public String getCUIT() {
        return CUIT;
    }

    public String getDNI() {
        return DNI;
    }

    public String getSALDOCUENTACORRIENTE() {
        return SALDOCUENTACORRIENTE;
    }

    public String getTELEFONO_DISTRI() {
        return TELEFONO_DISTRI;
    }

    public String getEMAIL_DISTRI() {
        return EMAIL_DISTRI;
    }

    public String getCODE_LISTA() {
        return CODE_LISTA;
    }

    public String getNOMBRE_VENDEDOR() {
        return NOMBRE_VENDEDOR;
    }

    public String getCANAL() {
        return CANAL;
    }

    public String getLATITUD() {
        return LATITUD;
    }

    public String getLONGITUD() {
        return LONGITUD;
    }

    public String getDireccion_otorgada() {
        return direccion_otorgada;
    }

    public String getEmail_otorgado() {
        return email_otorgado;
    }

    public String getTelefono_otorgado() {
        return telefono_otorgado;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getHABILITADO() {
        return HABILITADO;
    }

    // Setter Methods

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setEMPRESA_ID(String EMPRESA_ID) {
        this.EMPRESA_ID = EMPRESA_ID;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public void setDOMICILIO(String DOMICILIO) {
        this.DOMICILIO = DOMICILIO;
    }

    public void setCUIT(String CUIT) {
        this.CUIT = CUIT;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public void setSALDOCUENTACORRIENTE(String SALDOCUENTACORRIENTE) {
        this.SALDOCUENTACORRIENTE = SALDOCUENTACORRIENTE;
    }

    public void setTELEFONO_DISTRI(String TELEFONO_DISTRI) {
        this.TELEFONO_DISTRI = TELEFONO_DISTRI;
    }

    public void setEMAIL_DISTRI(String EMAIL_DISTRI) {
        this.EMAIL_DISTRI = EMAIL_DISTRI;
    }

    public void setCODE_LISTA(String CODE_LISTA) {
        this.CODE_LISTA = CODE_LISTA;
    }

    public void setNOMBRE_VENDEDOR(String NOMBRE_VENDEDOR) {
        this.NOMBRE_VENDEDOR = NOMBRE_VENDEDOR;
    }

    public void setCANAL(String CANAL) {
        this.CANAL = CANAL;
    }

    public void setLATITUD(String LATITUD) {
        this.LATITUD = LATITUD;
    }

    public void setLONGITUD(String LONGITUD) {
        this.LONGITUD = LONGITUD;
    }

    public void setDireccion_otorgada(String direccion_otorgada) {
        this.direccion_otorgada = direccion_otorgada;
    }

    public void setEmail_otorgado(String email_otorgado) {
        this.email_otorgado = email_otorgado;
    }

    public void setTelefono_otorgado(String telefono_otorgado) {
        this.telefono_otorgado = telefono_otorgado;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void setHABILITADO(String HABILITADO) {
        this.HABILITADO = HABILITADO;
    }
}