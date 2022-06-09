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
using System.Collections.Generic;

namespace ESCOMarket
{
    public static class QuitarDeCarrito
    {
        [FunctionName("quitar-de-carrito")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);
            if (data.id == null || data.id == 0)
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("Se recibió un identificador no válido")));
            }

            try
            {
                var conexion = new MySqlConnection(GeneradorDeCredencialesBD.ObtenerCredencialesBaseDeDatos());

                try
                {
                    conexion.Open();
                    var queryCommand = new MySqlCommand("SELECT articulos.id_articulo, articulos.descripcion, articulos.precio, articulos.cantidad, carrito_compra.* FROM articulos INNER JOIN carrito_compra ON articulos.id_articulo = carrito_compra.id_articulo AND carrito_compra.id_entrada = @id_entrada;", conexion);
                    queryCommand.Parameters.AddWithValue("@id_entrada", data.id);

                    var reader = queryCommand.ExecuteReader();
                    EntradaCarro entrada = new EntradaCarro();
                    try
                    {
                        if (reader.Read())
                        {
                            var articulo = new Articulo();

                            articulo.Id = reader.GetInt32(0);
                            articulo.Descripcion = reader.GetString(1);
                            articulo.Precio = reader.GetString(2);
                            articulo.Cantidad= reader.GetString(3);

                            entrada.Id = reader.GetInt32(4);
                            entrada.Cantidad = reader.GetInt32(6);
                            entrada.Articulo = articulo;
                        } 
                        else
                        {
                            return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("No se encontró la entrada en el carrito")));
                        }
                    }
                    catch
                    {
                        throw;
                    }
                    finally
                    {
                        reader.Close();
                    }

                    var transaction = conexion.BeginTransaction();
                    var deleteCommand = new MySqlCommand("DELETE FROM carrito_compra where id_entrada = @id_entrada;", conexion);
                    var updateCommand = new MySqlCommand("UPDATE articulos SET cantidad = @cantidad where id_articulo = @id_articulo;", conexion);
                    try
                    {
                        deleteCommand.Parameters.AddWithValue("@id_entrada", entrada.Id);
                        deleteCommand.ExecuteNonQuery();

                        updateCommand.Parameters.AddWithValue("@cantidad", Convert.ToInt32(entrada.Articulo.Cantidad)  + entrada.Cantidad);
                        updateCommand.Parameters.AddWithValue("@id_articulo", entrada.Articulo.Id);
                        updateCommand.ExecuteNonQuery();

                        transaction.Commit();

                        return new OkObjectResult(JsonConvert.SerializeObject(new Mensaje("Se eliminó la entrada del carrito")));
                    }
                    catch
                    {
                        transaction.Rollback();
                        throw;
                    }
                }
                catch
                {
                    throw;
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
