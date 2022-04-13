
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpRestRequest {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://20.231.54.196:8080/Servicio/rest/ws/consulta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        // true si se va a enviar un "body", en este caso el "body" son los parámetros
        conexion.setDoOutput(true);
        // en este caso utilizamos el método POST de HTTP
        conexion.setRequestMethod("POST");
        // indica que la petición estará codificada como URL
        conexion.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        // el método web "consulta_usuario" recibe como parámetro el email de un usuario, en este caso el email es a@c
        String parametros = "email=" + URLEncoder.encode("donayseg@gmail.com","UTF-8");
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();
        // se debe verificar si hubo error
        if (conexion.getResponseCode() == 200)
        {
          // no hubo error
          BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
          String respuesta;
          // el método web regresa una string en formato JSON
          while ((respuesta = br.readLine()) != null) System.out.println(respuesta);
        }
        else
        {
          // hubo error
          BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));

          String respuesta;

          // el método web regresa una instancia de la clase Error en formato JSON
          while ((respuesta = br.readLine()) != null) System.out.println(respuesta);
        }
        conexion.disconnect();
    }
}
