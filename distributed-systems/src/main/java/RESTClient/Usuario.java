import com.google.gson.*;
import java.time.Instant;
import java.time.ZoneId;

public class Usuario
{
    String nombre;
    String email;
    String apellido_paterno;
    String apellido_materno;
    String fecha_nacimiento;
    String telefono;
    String genero;
    byte[] foto;
  
    public Usuario () {
        this.nombre = null;
        this.email = null;
        this.apellido_paterno = null;
        this.apellido_materno = null;
        this.fecha_nacimiento = null;
        this.telefono = null;
        this.genero = null;
        this.foto = null;
    }
    
    public void limpiar() {
        this.nombre = null;
        this.email = null;
        this.apellido_paterno = null;
        this.apellido_materno = null;
        this.fecha_nacimiento = null;
        this.telefono = null;
        this.genero = null;
        this.foto = null;
    }
  
    public static Usuario valueOf(String s) throws Exception {
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).create();
        return (Usuario)j.fromJson(s,Usuario.class);
    }
    
    @Override
    public String toString() {
        String s = "";
        s += "Email: " + (email != null ? email : "null") + "\n";
        s += "Nombre: " + (nombre != null ? nombre : "null") + "\n";
        s += "Apellido Paterno: " + (apellido_paterno != null ? apellido_paterno : "null") + "\n";
        s += "Apellido Materno: " + (apellido_materno != null ? apellido_materno : "null") + "\n";
        
        String fechaNacimientoLocal = null;
        if (fecha_nacimiento != null) {
            fechaNacimientoLocal = Instant.parse(fecha_nacimiento.replace(" ", "T").concat("Z")).toString().substring(0,10);
            
        }
        s += "Fecha de nacimiento: " + (fechaNacimientoLocal != null ? fechaNacimientoLocal : "null") + "\n";
        s += "Teléfono: " + (telefono != null ? telefono : "null") + "\n";
        s += "Género: " + (genero != null ? genero : "null") + "\n";
        s += "Foto: " + (foto != null ? foto : "null") + "\n";
        return s;
    }
}
