package WebShop;

import com.google.gson.*;

public class Articulo {
    int id;
    String precio;
    String cantidad;
    byte[] foto;
    String descripcion;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrecio() {
        return Integer.parseInt(precio);
    }

    public void setPrecio(int precio) {
        this.precio = String.valueOf(precio);
    }

    public int getCantidad() {
        return Integer.parseInt(cantidad);
    }

    public void setCantidad(int cantidad) {
        this.cantidad = String.valueOf(cantidad);
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
        
    public static Articulo valueOf(String data) throws Exception {
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new GsonBase64Adapter()).create();
        return (Articulo) j.fromJson(data, Articulo.class);
    }
}
