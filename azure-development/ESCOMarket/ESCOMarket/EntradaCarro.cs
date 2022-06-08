using System;
using System.Collections.Generic;
using System.Text;

namespace ESCOMarket
{
    public class EntradaCarro
    {
        public int Id {get; set;}
        public Articulo Articulo { get; set; }
        public int Cantidad { get; set; }
    }
}
