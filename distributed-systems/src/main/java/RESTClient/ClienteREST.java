

import com.google.gson.*;
import java.net.*;
import java.io.*;

public class ClienteREST {
    private String ipServidor;
    private String rutaGlobal;
    private String puerto;
    private String cadenaURL;
    
    public static final String RUTA_ALTA_USUARIO = "/alta_usuario";
    public static final String RUTA_CONSULTA_USUARIO = "/consulta_usuario";
    public static final String RUTA_MODIFICA_USUARIO = "/modifica_usuario";
    public static final String RUTA_BORRA_USUARIO = "/borra_usuario";
    
    private int codigoRespuesta;
    private String respuestaServidor;
    
    private static Gson gsonBuilder = new GsonBuilder()
		.registerTypeAdapter(byte[].class,new AdaptadorGsonBase64())
		.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
		.create();
    
    public ClienteREST(String ipServidor, String puerto, String ruta) throws MalformedURLException {
        this.ipServidor = ipServidor;
        this.rutaGlobal = ruta;
        this.puerto = puerto;
        this.cadenaURL = "http://" + this.ipServidor + ":" + puerto + this.rutaGlobal;
        // Verifica que la URL global esté bien formada
        new URL(cadenaURL);
    }
    
    private void enviarSolicitud(String informacion, URL url, boolean esCorreo) throws IOException{
        HttpURLConnection conexion = null;
        
        conexion = (HttpURLConnection) url.openConnection();

        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        String parametros;
        if (esCorreo) {
            parametros = "email=" + URLEncoder.encode(informacion,"UTF-8");
        } else {
            parametros = "usuario=" + URLEncoder.encode(informacion,"UTF-8");
        }
        OutputStream outputStream = conexion.getOutputStream();
        outputStream.write(parametros.getBytes());
        outputStream.flush();

        String fragmentoRespuesta;
        respuestaServidor = "";
        codigoRespuesta = conexion.getResponseCode();
        if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            while ((fragmentoRespuesta = br.readLine()) != null) respuestaServidor += fragmentoRespuesta;
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            while ((fragmentoRespuesta = br.readLine()) != null) respuestaServidor += fragmentoRespuesta;
        }

        conexion.disconnect();
    }
    
    public RespuestaServidor registrarUsuario(Usuario usuario) throws IOException, MalformedURLException {
        URL urlServidor = new URL(this.cadenaURL + RUTA_ALTA_USUARIO);
        
        String jsonUsuario = gsonBuilder.toJson(usuario);
        
        enviarSolicitud(jsonUsuario, urlServidor, false);
        
        if (codigoRespuesta == HttpURLConnection.HTTP_OK) {
            return new RespuestaServidor(null, codigoRespuesta, "OK");
        }
        
        Error error = gsonBuilder.fromJson(respuestaServidor, Error.class);
        return new RespuestaServidor(null, codigoRespuesta, error.message);
    }
    
    public RespuestaServidor modificarUsuario(Usuario usuario) throws IOException, MalformedURLException {
        URL urlServidor = new URL(this.cadenaURL + RUTA_MODIFICA_USUARIO);
        
        String jsonUsuario = gsonBuilder.toJson(usuario);
        
        enviarSolicitud(jsonUsuario, urlServidor, false);
        
        if (codigoRespuesta == HttpURLConnection.HTTP_OK) {
            return new RespuestaServidor(null, codigoRespuesta, "OK");
        }
        Error error = gsonBuilder.fromJson(respuestaServidor, Error.class);
        return new RespuestaServidor(null, codigoRespuesta, error.message);
    }
    
    public RespuestaServidor consultaUsuario(Usuario usuario) throws IOException, MalformedURLException{
        URL urlServidor = new URL(this.cadenaURL + RUTA_CONSULTA_USUARIO);
        
        enviarSolicitud(usuario.email, urlServidor, true);
        
        if (codigoRespuesta == HttpURLConnection.HTTP_OK) {
            Usuario usuarioConsultado = gsonBuilder.fromJson(respuestaServidor, Usuario.class);
            return new RespuestaServidor(usuarioConsultado, codigoRespuesta, "OK");
        }
        Error error = gsonBuilder.fromJson(respuestaServidor, Error.class);
        return new RespuestaServidor(null, codigoRespuesta, error.message);
    }
    
    public RespuestaServidor borrarUsuario(Usuario usuario) throws IOException, MalformedURLException {
        URL urlServidor = new URL(this.cadenaURL + RUTA_BORRA_USUARIO);
        
        enviarSolicitud(usuario.email, urlServidor, true);
        
        if (codigoRespuesta == HttpURLConnection.HTTP_OK) {
            return new RespuestaServidor(null, codigoRespuesta, "OK");
        }
        
        Error error = gsonBuilder.fromJson(respuestaServidor, Error.class);
        return new RespuestaServidor(null, codigoRespuesta, error.message);
    }
}
