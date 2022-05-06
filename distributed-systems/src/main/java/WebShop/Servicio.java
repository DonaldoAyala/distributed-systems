package WebShop;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.sql.*;

import com.google.gson.*;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@Path("escomarket")
public class Servicio {
    static DataSource pool = null;

    static {
        try {
            Context ctx = new InitialContext();
            pool = (DataSource) ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Gson j = new GsonBuilder()
            .registerTypeAdapter(byte[].class, new GsonBase64Adapter())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .create();
    
    
    @POST
    @Path("test")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(@FormParam("mensaje") Mensaje mensaje) {
	    if (mensaje != null) System.out.println("Fine");
        return Response.status(200).entity(j.toJson(new Mensaje("Se debe ingresar una descrición de artículo"))).build();
    }
    
    @POST
    @Path("captura-articulo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarArticulo(@FormParam("articulo") Articulo articulo) throws Exception {
        Connection conexion = pool.getConnection();

        if (articulo.descripcion == null || articulo.descripcion.equals("")) {
            return Response.status(400).entity(j.toJson(new Mensaje("Se debe ingresar una descrición de artículo"))).build();
        }

        if (articulo.precio == null || articulo.precio.equals("")) {
            return Response.status(400).entity(j.toJson(new Mensaje("Se debe ingresar una descrición de artículo"))).build();
        }

        if (articulo.cantidad == null || articulo.cantidad.equals("")) {
            return Response.status(400).entity(j.toJson(new Mensaje("Se debe ingresar una cantidad en almacén para el articulo"))).build();
        }
        
        if (articulo.foto == null || articulo.foto.equals("")) {
            return Response.status(400).entity(j.toJson(new Mensaje("Se debe una fotografía del articulo"))).build();
        }

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement("SELECT id_articulo FROM articulos WHERE descripcion=?");
            try {
                stmt_1.setString(1, articulo.descripcion);

                ResultSet rs = stmt_1.executeQuery();
                try {
                    if (rs.next()) {
                        return Response.status(400).entity(j.toJson(new Mensaje("La descripcion ya existe, asegurese de no registrar articulos repetidos."))).build();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }

            PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO articulos VALUES (0,?,?,?,?)");
            try {
                stmt_2.setString(1, articulo.descripcion);
                stmt_2.setInt(2, articulo.getPrecio());
                stmt_2.setInt(3, articulo.getCantidad());
                stmt_2.setBytes(4, articulo.foto);
                
                stmt_2.executeUpdate();
            } finally {
                stmt_2.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Mensaje(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("buscar-articulo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarProducto(@FormParam("articulo") Articulo articulo) throws Exception {
        Connection conexion = pool.getConnection();

        if (articulo.descripcion == null) {
            return Response.status(400).entity(j.toJson(new Mensaje("Se recibió una descripción null"))).build();
        }

        PreparedStatement stmt_1 = conexion.prepareStatement("SELECT * FROM articulos WHERE descripcion like ?;");
        try {
            stmt_1.setString(1, "%" + articulo.descripcion + "%");

            ResultSet rs = stmt_1.executeQuery();

            List<Articulo> articulos = new ArrayList<Articulo>();
            try {
                while (rs.next()) {
                    Articulo coincidenciaProducto = new Articulo();

                    coincidenciaProducto.setId(rs.getInt(1));
                    coincidenciaProducto.setDescripcion(rs.getString(2));
                    coincidenciaProducto.setPrecio(rs.getInt(3));
                    coincidenciaProducto.setCantidad(rs.getInt(4));
                    coincidenciaProducto.setFoto(rs.getBytes(5));

                    articulos.add(coincidenciaProducto);
                }
                return Response.ok().entity(j.toJson(articulos)).build();
            } finally {
                rs.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Mensaje(e.getMessage()))).build();
        } finally {
            stmt_1.close();
        }
    }
    
    @POST
    @Path("agregar-a-carrito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarACarrito(@FormParam("articulo") Articulo articulo) throws Exception {
        Connection conexion = pool.getConnection();
        conexion.setAutoCommit(false);

        if (articulo.id == 0) {
            return Response.status(400).entity(j.toJson(new Mensaje("Se recibió un identificador no válido"))).build();
        }
        
        if (articulo.getCantidad() <= 0) {
            return Response.status(400).entity(j.toJson(new Mensaje("La cantidad no es válida"))).build();
        }

        PreparedStatement stmt_1 = conexion.prepareStatement("SELECT * FROM articulos WHERE id_articulo = ?;");
        try {
            stmt_1.setInt(1, articulo.id);

            ResultSet rs = stmt_1.executeQuery();

            Articulo coincidenciaProducto = new Articulo();
            try {
                if (rs.next()) {
                    coincidenciaProducto.setId(rs.getInt(1));
                    coincidenciaProducto.setDescripcion(rs.getString(2));
                    coincidenciaProducto.setPrecio(rs.getInt(3));
                    coincidenciaProducto.setCantidad(rs.getInt(4));
                    coincidenciaProducto.setFoto(rs.getBytes(5));
                } else {
                    return Response.status(400).entity(j.toJson(new Mensaje("No se encontró el articulo"))).build();
                }
                
                if (articulo.getCantidad() > coincidenciaProducto.getCantidad()) {
                    return Response.status(400).entity(j.toJson(new Mensaje("Lo sentimos, no existen demasiados articulos en almacén, solo se cuenta con " + coincidenciaProducto.getCantidad()))).build();
                } else {
                    PreparedStatement stmt_2 = conexion.prepareStatement("INSERT into carrito_compra values(0,?,?);");
                    PreparedStatement stmt_3 = conexion.prepareStatement("UPDATE articulos SET cantidad = ? WHERE id_articulo = ?;");
                    try {
                        // INSERT al carrito de compra
                        stmt_2.setInt(1, articulo.getId());
                        stmt_2.setInt(2, articulo.getCantidad());

                        stmt_2.execute();

                        // UPDATE de las existencias en articulos
                        stmt_3.setInt(1, coincidenciaProducto.getCantidad() - articulo.getCantidad());
                        stmt_3.setInt(2, articulo.getId());

                        stmt_3.execute();
                        
                        // Si ningún error ocurre, se guardan los cambios
                        conexion.commit();
                    } catch (Exception e) {
                        // Si ocurre una excepción se descartan los cambios
                        conexion.rollback();
                        return Response.status(400).entity(j.toJson(new Mensaje("Se produjó un error, vuelva a intentar más tarde"))).build();
                    } finally {
                        stmt_2.close();
                        stmt_3.close();
                    }
                }
            } finally {
                rs.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Mensaje(e.getMessage()))).build();
        } finally {
            stmt_1.close();
        }
        
        return Response.ok().build();
    }
    
    @POST
    @Path("carrito-compra")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCarrito() throws Exception {
        Connection conexion = pool.getConnection();

        PreparedStatement stmt_1 = conexion.prepareStatement("SELECT articulos.*, carrito_compra.* FROM articulos RIGHT JOIN carrito_compra ON articulos.id_articulo = carrito_compra.id_articulo;");
        try {
            ResultSet rs = stmt_1.executeQuery();

            List<EntradaCarro> articulosEnCarro = new ArrayList<>();
            try {
                while (rs.next()) {
                    EntradaCarro entrada = new EntradaCarro();
                    Articulo articulo = new Articulo();

                    
                    articulo.setId(rs.getInt(1));
                    articulo.setDescripcion(rs.getString(2));
                    articulo.setPrecio(rs.getInt(3));
                    articulo.setCantidad(rs.getInt(4));
                    articulo.setFoto(rs.getBytes(5));
                    
                    entrada.setId(rs.getInt(6));
                    entrada.setCantidad(rs.getInt(8));
                    entrada.setArticulo(articulo);

                    articulosEnCarro.add(entrada);
                }
                return Response.ok().entity(j.toJson(articulosEnCarro)).build();
            } finally {
                rs.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Mensaje(e.getMessage()))).build();
        } finally {
            stmt_1.close();
        }
    }
    
    @POST
    @Path("quitar-de-carrito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response quitarDeCarrito(@FormParam("entrada") EntradaCarro entrada) throws Exception {
        Connection conexion = pool.getConnection();
        conexion.setAutoCommit(false);

        if (entrada.id == 0) {
            return Response.status(400).entity(j.toJson(new Mensaje("Se recibió una entrada de carrito no válida"))).build();
        }
        
        PreparedStatement stmt_1 = conexion.prepareStatement("SELECT articulos.*, carrito_compra.* FROM articulos INNER JOIN carrito_compra ON articulos.id_articulo = carrito_compra.id_articulo AND carrito_compra.id_entrada = ?;");
        EntradaCarro entradaConsultada = new EntradaCarro();
        try {
            stmt_1.setInt(1, entrada.getId());
            ResultSet rs = stmt_1.executeQuery();

            try {
                if (rs.next()) {
                    Articulo articulo = new Articulo();
                    
                    articulo.setId(rs.getInt(1));
                    articulo.setDescripcion(rs.getString(2));
                    articulo.setPrecio(rs.getInt(3));
                    articulo.setCantidad(rs.getInt(4));
                    articulo.setFoto(rs.getBytes(5));
                    
                    entradaConsultada.setId(rs.getInt(6));
                    entradaConsultada.setCantidad(rs.getInt(8));
                    entradaConsultada.setArticulo(articulo);
                } else {
                    return Response.status(400).entity(j.toJson(new Mensaje("No se encontró la entrada en el carrito"))).build();
                }
            } finally {
                rs.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Mensaje(e.getMessage()))).build();
        } finally {
            stmt_1.close();
        }
        
        PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM carrito_compra where id_entrada = ?;");
        PreparedStatement stmt_3 = conexion.prepareStatement("UPDATE articulos SET cantidad = ? where id_articulo = ?;");
        try {
            stmt_2.setInt(1, entradaConsultada.getId());

            stmt_2.execute();
            
            // Se asigna la cantidad del carrito más la cantidad en almacén
            stmt_3.setInt(1, entradaConsultada.articulo.getCantidad() + entradaConsultada.cantidad);
            stmt_3.setInt(2, entradaConsultada.articulo.getId());
            
            stmt_3.execute();
            
            conexion.commit();
        } catch (Exception e) {
            conexion.rollback();
            return Response.status(400).entity(j.toJson(new Mensaje(e.getStackTrace().toString()))).build();
        } finally {
            stmt_2.close();
            stmt_3.close();
        }
        
        return Response.ok().build();
    }
    
    
    @POST
    @Path("borrar-carrito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response borrarCarrito() throws Exception {
        Connection conexion = pool.getConnection();
        conexion.setAutoCommit(false);
        
        PreparedStatement stmt_1 = conexion.prepareStatement("SELECT articulos.*, carrito_compra.* FROM articulos RIGHT JOIN carrito_compra ON articulos.id_articulo = carrito_compra.id_articulo;");
        List<EntradaCarro> entradas = new ArrayList<>();
        try {
            ResultSet rs = stmt_1.executeQuery();

            try {
                while (rs.next()) {
                    EntradaCarro entradaConsultada = new EntradaCarro();
                    Articulo articulo = new Articulo();
                    
                    articulo.setId(rs.getInt(1));
                    articulo.setDescripcion(rs.getString(2));
                    articulo.setPrecio(rs.getInt(3));
                    articulo.setCantidad(rs.getInt(4));
                    articulo.setFoto(rs.getBytes(5));
                    entradaConsultada.setId(rs.getInt(6));
                    entradaConsultada.setCantidad(rs.getInt(8));
                    entradaConsultada.setArticulo(articulo);
                    
                    entradas.add(entradaConsultada);
                }
            } finally {
                rs.close();
            }
            
            try {
                for (EntradaCarro entrada : entradas) {
                    PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM carrito_compra where id_entrada = ?;");
                    PreparedStatement stmt_3 = conexion.prepareStatement("UPDATE articulos SET cantidad = ? where id_articulo = ?;");
                    PreparedStatement stmt_4 = conexion.prepareStatement("SELECT cantidad FROM articulos WHERE id_articulo = ?;");

                    try {
                        stmt_2.setInt(1, entrada.getId());

                        stmt_2.execute();
			stmt_4.setInt(1, entrada.articulo.getId());
			ResultSet stock = stmt_4.executeQuery();
			int existencia = 0;
			if (stock.next()) {
				existencia = stock.getInt(1);
			}
                        
			// Se asigna la cantidad del carrito más la cantidad en almacén
                        stmt_3.setInt(1, existencia + entrada.cantidad);
                        stmt_3.setInt(2, entrada.articulo.getId());

                        stmt_3.execute();

                    } catch (Exception e) {
                        throw e;
                    } finally {
                        stmt_2.close();
                        stmt_3.close();
			stmt_4.close();
                    }
                }
            } catch (Exception e) {
                conexion.rollback();
                return Response.status(400).entity(j.toJson(new Mensaje("Ocurrió un error, intente de nuevo más tarde"))).build();
            } 
            conexion.commit();
        } catch (Exception e) {
            conexion.rollback();
            return Response.status(400).entity(j.toJson(new Mensaje(e.getMessage()))).build();
        } finally {
            stmt_1.close();
        }
        
        return Response.ok().build();
    }
}

