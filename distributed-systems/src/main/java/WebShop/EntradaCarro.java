package WebShop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EntradaCarro {
    int id;
    Articulo articulo;
    int cantidad;

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Articulo getProducto() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }
    
    public static EntradaCarro valueOf(String data) throws Exception {
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new GsonBase64Adapter()).create();
        return (EntradaCarro) j.fromJson(data, EntradaCarro.class);
    }
}
