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
    public static class Articulo
    {
        class Usuario
        {
            public string nombre;
        }

        [FunctionName("captura")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            log.LogInformation("C# HTTP trigger function processed a request.");

            string name = req.Query["name"];

            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);
            name = name ?? data?.name;

            string responseMessage = string.IsNullOrEmpty(name)
                ? "This HTTP triggered function executed successfully. Pass a name in the query string or in the request body for a personalized response."
                : $"Hello, {name}. This HTTP triggered function executed successfully.";

            string Server = "localhost";
            string UserID = "donaldo";
            string Password = "donaldohoracio";
            string Database = "test";
            try
            {
                string sc = "Server=" + Server + ";UserID=" + UserID + ";Password=" + Password + ";" + "Database=" + Database + ";SslMode=Preferred;";
                var conexion = new MySqlConnection(sc);
                conexion.Open();

                try
                {
                    var cmd = new MySqlCommand("select * from test", conexion);
                    cmd.Parameters.AddWithValue("@email", "test");
                    MySqlDataReader r = cmd.ExecuteReader();
                    List<Usuario> lista = new List<Usuario>();
                    while (r.Read())
                    {
                        Usuario usuario = new Usuario();
                        usuario.nombre= r.GetString(0);
                        lista.Add(usuario);
                    }
                    return new OkObjectResult(JsonConvert.SerializeObject(lista));
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
            

            return new OkObjectResult(responseMessage);
        }
    }
}
