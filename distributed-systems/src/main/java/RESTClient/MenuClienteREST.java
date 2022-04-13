

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;

public class MenuClienteREST {
    private Scanner lector;
    private ClienteREST clienteREST;
    
    public MenuClienteREST() {
        lector = new Scanner(System.in);
    }
    
    public void inicializarClienteREST(String ipServidor, String puerto, String ruta) {
        try {
            clienteREST = new ClienteREST(ipServidor, puerto, ruta);
        } catch (MalformedURLException murle) {
            murle.printStackTrace();
        }
    }
    
    public void ejecutarMenu() throws IOException, MalformedURLException{
        String entrada = "";
        RespuestaServidor respuesta;
        Usuario datosUsuario = new Usuario();
        
        while (entrada != "d") {
            System.out.println("MENU");
            System.out.println("a. Alta de usuario");
            System.out.println("b. Consulta de usuario");
            System.out.println("c. Borar usuario");
            System.out.println("d. Salir");

            entrada = lector.nextLine();
            datosUsuario.limpiar();
            
            switch (entrada) {
                case "a":
                    datosUsuario = leerDatosUsuario(null);
                    respuesta = clienteREST.registrarUsuario(datosUsuario);
                    System.out.println(respuesta.getMensaje());
                break;
                case "b":
                    datosUsuario = leerCorreoUsuario();
                    respuesta = clienteREST.consultaUsuario(datosUsuario);
                    if (respuesta.getCodigoStatus() == HttpsURLConnection.HTTP_OK) {
                        System.out.println(respuesta.getUsuario());
                        
                        System.out.println("¿Desea modificar el usuario (s/n)?");
                        String modificar = lector.nextLine();
                        if (modificar.equalsIgnoreCase("s")) {
                            datosUsuario = leerDatosUsuario(respuesta.getUsuario());
                            respuesta = clienteREST.modificarUsuario(datosUsuario);
                            if (respuesta.getCodigoStatus() == HttpsURLConnection.HTTP_OK) {
                                System.out.println("OK");
                            }
                        }
                    } else {
                        System.out.println(respuesta.mensaje);
                    }
                break;
                case "c":
                    datosUsuario = leerCorreoUsuario();
                    respuesta = clienteREST.borrarUsuario(datosUsuario);
                    System.out.println(respuesta.getMensaje());
                break;
                case "d":
                    return;
                default:
                    System.out.println("Opción no válida");
                break;
            }
        }
    }
    
    private Usuario leerDatosUsuario (Usuario datosUsuario) {
        Usuario nuevoUsuario = new Usuario();
        String entrada;
        
        if (datosUsuario == null) {
            System.out.println("Ingrese el EMAIL del usuario");
            entrada = lector.nextLine();
            nuevoUsuario.email = (entrada.length() > 0 ? entrada : nuevoUsuario.email);
            datosUsuario = new Usuario();
        } else {
            nuevoUsuario = datosUsuario;
        }
        
        System.out.println("Ingrese el NOMBRE del usuario");
        entrada = lector.nextLine();
        nuevoUsuario.nombre = (entrada.length() > 0 ? entrada : datosUsuario.nombre);
        
        System.out.println("Ingrese el APELLIDO PATERNO del usuario");
        entrada = lector.nextLine();
        nuevoUsuario.apellido_paterno = (entrada.length() > 0 ? entrada : datosUsuario.apellido_paterno);
        
        System.out.println("Ingrese el APELLIDO MATERNO del usuario");
        entrada = lector.nextLine();
        nuevoUsuario.apellido_materno = (entrada.length() > 0 ? entrada : datosUsuario.apellido_materno);
        
        System.out.println("Ingrese la FECHA DE NACIMIENTO del usuario. (En formato aaaa-mm-dd)");
        String fechaNacimiento = lector.nextLine();
        if (datosUsuario != null && fechaNacimiento.length() == 0) {
            fechaNacimiento = datosUsuario.fecha_nacimiento;
        } else {
            LocalDateTime fechaIngresada = LocalDateTime.parse(fechaNacimiento + "T12:00:00.000");
        }
        nuevoUsuario.fecha_nacimiento = fechaNacimiento.replace("T", " ").replace("Z", "");
        
        System.out.println("Ingrese el TELÉFONO del usuario.");
        entrada = lector.nextLine();
        nuevoUsuario.telefono = (entrada.length() > 0 ? entrada : datosUsuario.telefono);
        
        System.out.println("Ingrese el GÉNERO del usuario. (M o F)");
        entrada = lector.nextLine();
        nuevoUsuario.genero = (entrada.length() > 0 ? entrada : datosUsuario.genero);
        
        return nuevoUsuario;
    }
    
    private Usuario leerCorreoUsuario () {
        Usuario nuevoUsuario = new Usuario();
        
        System.out.println("Ingrese el EMAIL del usuario");
        nuevoUsuario.email = lector.nextLine();
        
        return nuevoUsuario;
    }
}
