using System;
using System.Collections.Generic;
using System.Text;

namespace ESCOMarket
{
    public class Articulo
    {
        public int Id { get; set; }
        public string Precio { get; set; }
        public string Cantidad { get; set; }
        public string Descripcion { get; set; }
        public byte[] Foto { get; set; }
    }
}
