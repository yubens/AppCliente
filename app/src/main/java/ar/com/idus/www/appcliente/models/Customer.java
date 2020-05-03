package ar.com.idus.www.appcliente.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Customer implements Serializable {
    @SerializedName("CODIGO")
    @Expose
    private String codigo;

    @SerializedName("EMPRESA_ID")
    @Expose
    private String empresaId;

    @SerializedName("NOMBRE")
    @Expose
    private String nombre;

    @SerializedName("DOMICILIO")
    @Expose
    private String domicilio;

    @SerializedName("CUIT")
    @Expose
    private String cuit;

    @SerializedName("DNI")
    @Expose
    private String dni;

    @SerializedName("SALDOCUENTACORRIENTE")
    @Expose
    private String saldoCuentaCorriente;

    @SerializedName("TELEFONO_DISTRI")
    @Expose
    private String telefonoDistribuidora;

    @SerializedName("EMAIL_DISTRI")
    @Expose
    private String emailDistribuidora;

    @SerializedName("CODE_LISTA")
    @Expose
    private String codigoLista;

    @SerializedName("NOMBRE_VENDEDOR")
    @Expose
    private String nombreVendedor;

    @SerializedName("CANAL")
    @Expose
    private String canal;

    @SerializedName("LATITUD")
    @Expose
    private Object latitud;

    @SerializedName("LONGITUD")
    @Expose
    private Object longitud;

    @SerializedName("direccion_otorgada")
    @Expose
    private String direccionOtorgada;

    @SerializedName("email_otorgado")
    @Expose
    private String emailOtorgado;

    @SerializedName("telefono_otorgado")
    @Expose
    private String telefonoOtorgado;

    @SerializedName("contrase\u00f1a")
    @Expose
    private String contrasena;

    @SerializedName("HABILITADO")
    @Expose
    private String habilidado;

    private String idCliente;

    private final static long serialVersionUID = -4264745338845686666L;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getSaldoCuentaCorriente() {
        return saldoCuentaCorriente;
    }

    public void setSaldoCuentaCorriente(String saldoCuentaCorriente) {
        this.saldoCuentaCorriente = saldoCuentaCorriente;
    }

    public String getTelefonoDistribuidora() {
        return telefonoDistribuidora;
    }

    public void setTelefonoDistribuidora(String telefonoDistribuidora) {
        this.telefonoDistribuidora = telefonoDistribuidora;
    }

    public String getEmailDistribuidora() {
        return emailDistribuidora;
    }

    public void setEmailDistribuidora(String emailDistribuidora) {
        this.emailDistribuidora = emailDistribuidora;
    }

    public String getCodigoLista() {
        return codigoLista;
    }

    public void setCodigoLista(String codigoLista) {
        this.codigoLista = codigoLista;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public Object getLatitud() {
        return latitud;
    }

    public void setLatitud(Object latitud) {
        this.latitud = latitud;
    }

    public Object getLongitud() {
        return longitud;
    }

    public void setLongitud(Object longitud) {
        this.longitud = longitud;
    }

    public String getDireccionOtorgada() {
        return direccionOtorgada;
    }

    public void setDireccionOtorgada(String direccionOtorgada) {
        this.direccionOtorgada = direccionOtorgada;
    }

    public String getEmailOtorgado() {
        return emailOtorgado;
    }

    public void setEmailOtorgado(String emailOtorgado) {
        this.emailOtorgado = emailOtorgado;
    }

    public String getTelefonoOtorgado() {
        return telefonoOtorgado;
    }

    public void setTelefonoOtorgado(String telefonoOtorgado) {
        this.telefonoOtorgado = telefonoOtorgado;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getHabilidado() {
        return habilidado;
    }

    public void setHabilidado(String habilidado) {
        this.habilidado = habilidado;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }
}