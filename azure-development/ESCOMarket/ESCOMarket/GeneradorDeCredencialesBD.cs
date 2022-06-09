using System;
using System.Collections.Generic;
using System.Text;

namespace ESCOMarket
{
    public class GeneradorDeCredencialesBD
    {
        public static string ObtenerCredencialesBaseDeDatos()
        {
            string Server = Environment.GetEnvironmentVariable("Server");
            string UserID = Environment.GetEnvironmentVariable("UserID");
            string Password = Environment.GetEnvironmentVariable("Password");
            string Database = Environment.GetEnvironmentVariable("Database");

            string databaseConnectionCredentials =
                "Server=" + Server +
                ";UserID=" + UserID +
                ";Password=" + Password +
                ";Database=" + Database +
                ";SslMode=Preferred;";

            return databaseConnectionCredentials;
        }
    }
}
