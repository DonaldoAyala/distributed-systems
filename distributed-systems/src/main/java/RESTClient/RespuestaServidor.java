public class RespuestaServidor {
    Usuario usuario;
    int codigoStatus;
    String mensaje;

    public RespuestaServidor(Usuario usuario, int codigoStatus, String mensaje) {
        this.usuario = usuario;
        this.codigoStatus = codigoStatus;
        this.mensaje = mensaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getCodigoStatus() {
        return codigoStatus;
    }

    public void setCodigoStatus(int codigoStatus) {
        this.codigoStatus = codigoStatus;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
