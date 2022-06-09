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
    public static class CarritoCompra
    {
        [FunctionName("carrito-compra")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            try
            {
                var conexion = new MySqlConnection(GeneradorDeCredencialesBD.ObtenerCredencialesBaseDeDatos());
                conexion.Open();

                var queryCommand = new MySqlCommand("SELECT articulos.*, length(articulos.foto), carrito_compra.* FROM articulos RIGHT JOIN carrito_compra ON articulos.id_articulo = carrito_compra.id_articulo;", conexion);
                
                var reader = queryCommand.ExecuteReader();
                List<EntradaCarro> articulosEnCarro = new List<EntradaCarro>();

                while (reader.Read())
                {
                    var articulo = new Articulo();
                    var entradaCarro = new EntradaCarro();

                    articulo.Id = reader.GetInt32(0);
                    articulo.Descripcion = reader.GetString(1);
                    articulo.Precio = reader.GetString(2);
                    articulo.Cantidad = reader.GetString(3);
                    int longitud = reader.GetInt32(5);
                    articulo.Foto = new byte[longitud];
                    reader.GetBytes(4, 0, articulo.Foto, 0, longitud);
                    

                    entradaCarro.Id = reader.GetInt32(6);
                    entradaCarro.Cantidad = reader.GetInt32(8);
                    entradaCarro.Articulo = articulo;

                    articulosEnCarro.Add(entradaCarro);
                }
                return new OkObjectResult(JsonConvert.SerializeObject(articulosEnCarro)); 
            } catch (Exception e)
            {
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Mensaje(e.Message)));
            }
        }
    }
}
