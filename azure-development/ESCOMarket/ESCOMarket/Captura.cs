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
    public static class Captura
    {
        [FunctionName("captura-articulo")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function, "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);
            if (data.descripcion == null || data.descripcion == "")
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("Se debe ingresar una descripción de artículo")));
            }
            if (data.precio == null || data.precio == "")
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("Se debe asignar un precio al artículo")));
            }
            if (data.cantidad == null || data.cantidad == "")
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("Se debe ingresar la cantidad de articulos ingresados")));
            }
            if (data.foto == null || data.foto == "")
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("Se debe ingresar una foto del artículo")));
            }

            try
            {
                string databaseCredentials = GeneradorDeCredencialesBD.ObtenerCredencialesBaseDeDatos();
                var conexion = new MySqlConnection(databaseCredentials);
                conexion.Open();

                try
                {
                    var queryCommand = new MySqlCommand("SELECT id_articulo FROM articulos WHERE descripcion=@descripcion", conexion);
                    
                    queryCommand.Parameters.AddWithValue("@descripcion", data.descripcion);
                    
                    MySqlDataReader reader = queryCommand.ExecuteReader();
                    if (reader.Read())
                    {
                        reader.Close();
                        return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje("La descripcion ya existe, asegurese de no registrar articulos repetidos.")));
                    }
                    reader.Close();
                } catch (Exception e)
                {
                    return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje(e.Message)));
                }

                var insertionCommand = new MySqlCommand("INSERT INTO articulos VALUES (0,@descripcion,@precio,@cantidad,@foto)", conexion);
                try
                {
                    insertionCommand.Parameters.AddWithValue("@descripcion", data.descripcion);
                    insertionCommand.Parameters.AddWithValue("@precio", data.precio);
                    insertionCommand.Parameters.AddWithValue("@cantidad", data.cantidad);
                    insertionCommand.Parameters.AddWithValue("@foto", Convert.FromBase64String((string)data.foto));

                    insertionCommand.ExecuteNonQuery();

                    return new OkObjectResult(JsonConvert.SerializeObject(new Mensaje("Se ha agregado el artículo con éxito")));
                }
                finally
                {
                    conexion.Close();
                }
            } 
            catch (Exception e)
            {
                return new BadRequestObjectResult(e.Message);
            }
        }
    }
}
