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
    public static class BorrarCarrito
    {
        [FunctionName("borrar-carrito")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            try
            {
                var conexion = new MySqlConnection(GeneradorDeCredencialesBD.ObtenerCredencialesBaseDeDatos());
                try
                {
                    conexion.Open();

                    var queryCommand = new MySqlCommand("SELECT articulos.id_articulo, articulos.descripcion, articulos.cantidad, articulos.precio, carrito_compra.* FROM articulos RIGHT JOIN carrito_compra ON articulos.id_articulo = carrito_compra.id_articulo;", conexion);
                    List<EntradaCarro> entradas = new List<EntradaCarro>();
                    var reader = queryCommand.ExecuteReader();

                    while (reader.Read())
                    {
                        var entrada = new EntradaCarro();
                        var articulo = new Articulo();

                        articulo.Id = reader.GetInt32(0);
                        articulo.Descripcion = reader.GetString(1);
                        articulo.Cantidad = reader.GetString(2);
                        articulo.Precio = reader.GetString(3);

                        entrada.Id = reader.GetInt32(4);
                        entrada.Cantidad = reader.GetInt32(6);
                        entrada.Articulo = articulo;

                        entradas.Add(entrada);
                    }
                    reader.Close();

                    var transaction = conexion.BeginTransaction();
                    try
                    {
                        foreach (EntradaCarro entrada in entradas)
                        {
                            var selectCommand = new MySqlCommand("SELECT cantidad FROM articulos WHERE id_articulo = @id_articulo;", conexion);
                            var deleteCommand = new MySqlCommand("DELETE FROM carrito_compra where id_entrada = @id_entrada;", conexion);
                            var updateCommand = new MySqlCommand("UPDATE articulos SET cantidad = @cantidad where id_articulo = @id_articulo;", conexion);

                            selectCommand.Parameters.AddWithValue("@id_articulo", entrada.Articulo.Id);
                            var stock = selectCommand.ExecuteReader();
                            int existencias = 0;
                            if (stock.Read())
                            {
                                existencias = stock.GetInt32(0);
                            }
                            stock.Close();

                            deleteCommand.Parameters.AddWithValue("@id_entrada", entrada.Id);
                            deleteCommand.ExecuteNonQuery();

                            updateCommand.Parameters.AddWithValue("@cantidad", existencias + entrada.Cantidad);
                            updateCommand.Parameters.AddWithValue("@id_articulo", entrada.Articulo.Id);
                            updateCommand.ExecuteNonQuery();
                        }

                        transaction.Commit();
                        return new OkObjectResult(JsonConvert.SerializeObject(new Mensaje("Se eliminó el carrito")));
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
