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
    public static class Consulta
    {
        [FunctionName("buscar-articulo")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            Console.WriteLine("Hola");
            string descripcion = req.Query["descripcion"];

            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);
            
            descripcion = descripcion ?? data?.descripcion;

            if (descripcion == null)
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("Se recibió una descripción null")));
            }

            descripcion = "%" + descripcion + "%";

            try
            {
                string databaseCredentials = GeneradorDeCredencialesBD.ObtenerCredencialesBaseDeDatos();
                var conexion = new MySqlConnection(databaseCredentials);
                conexion.Open();

                try
                {
                    var queryCommand = new MySqlCommand("SELECT id_articulo, descripcion, precio, cantidad, length(foto), foto FROM articulos WHERE descripcion like @descripcion;", conexion);

                    queryCommand.Parameters.AddWithValue("@descripcion", descripcion);

                    MySqlDataReader reader = queryCommand.ExecuteReader();
                    List<Articulo> articulos = new List<Articulo>();
                    while (reader.Read())
                    {
                        Articulo articulo = new Articulo();

                        articulo.Id = reader.GetInt32(0);
                        articulo.Descripcion = reader.GetString(1);
                        articulo.Precio = reader.GetString(2);
                        articulo.Cantidad = reader.GetString(3);
                        int longitud = reader.GetInt32(4);
                        articulo.Foto = new byte[longitud];
                        reader.GetBytes(5, 0, articulo.Foto, 0, longitud);

                        articulos.Add(articulo);
                    }
                    reader.Close();
                    return new OkObjectResult(JsonConvert.SerializeObject(articulos));
                }
                catch (Exception e)
                {
                    return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje(e.Message)));
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
