using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using MySql.Data.MySqlClient;

namespace ESCOMarket
{
    public static class AgregarACarrito
    {
        [FunctionName("agregar-a-carrito")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);

            Articulo articulo = new Articulo();
            articulo.Id = (int)data?.id;
            articulo.Cantidad = data?.cantidad;

            if (articulo.Id == 0)
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("Se recibió un identificador no válido")));
            }

            if (Convert.ToInt32(articulo.Cantidad) <= 0)
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("Cantidad no válida")));
            }

            try
            {
                var conexion = new MySqlConnection(GeneradorDeCredencialesBD.ObtenerCredencialesBaseDeDatos());
                conexion.Open();
                MySqlTransaction transaccion = conexion.BeginTransaction();

                try
                {
                    var query = new MySqlCommand("SELECT id_articulo, descripcion, precio, cantidad FROM articulos WHERE id_articulo = @id_articulo;", conexion);
                    query.Parameters.AddWithValue("@id_articulo", articulo.Id);

                    var reader = query.ExecuteReader();

                    Articulo coincidenciaProducto = new Articulo();
                    if (reader.Read())
                    {
                        coincidenciaProducto.Id = reader.GetInt32(0);
                        coincidenciaProducto.Descripcion = reader.GetString(1);
                        coincidenciaProducto.Precio = reader.GetString(2);
                        coincidenciaProducto.Cantidad = reader.GetString(3);
                    } 
                    else
                    {
                        return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("No se encontró el artículo")));
                    }
                    reader.Close();

                    if (Convert.ToInt32(articulo.Cantidad) > Convert.ToInt32(coincidenciaProducto.Cantidad))
                    {
                        return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("Lo sentimos, no existen demasiados artículos en almacén, solo se cuenta con " + Convert.ToInt32(coincidenciaProducto.Cantidad))));
                    }

                    var insercionCarrito = new MySqlCommand("INSERT into carrito_compra values(0,@id_articulo,@cantidad);", conexion);
                    var actualizacionCantidad = new MySqlCommand("UPDATE articulos SET cantidad = @cantidad WHERE id_articulo = @id_articulo;", conexion);
                    try
                    {
                        insercionCarrito.Parameters.AddWithValue("@id_articulo", articulo.Id);
                        insercionCarrito.Parameters.AddWithValue("@cantidad", articulo.Cantidad);

                        insercionCarrito.ExecuteNonQuery();

                        actualizacionCantidad.Parameters.AddWithValue("@cantidad", Convert.ToInt32(coincidenciaProducto.Cantidad) - Convert.ToInt32(articulo.Cantidad));
                        actualizacionCantidad.Parameters.AddWithValue("@id_articulo", articulo.Id);

                        actualizacionCantidad.ExecuteNonQuery();

                        transaccion.Commit();

                        return new OkObjectResult(JsonConvert.SerializeObject(new Mensaje("Se Agregó el artículo al carrito")));
                    }
                    catch (Exception e)
                    {
                        transaccion.Rollback();
                        return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje(e.Message)));
                    }
                }
                finally
                {
                    conexion.Close();
                }
            }
            catch (Exception e)
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje(e.Message)));
            }

        }
    }
}
