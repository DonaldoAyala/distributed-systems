drop database if exists web_shop;
create database web_shop;
use web_shop;


create table articulos (
	id_articulo integer auto_increment primary key,
	descripcion varchar(50),
	precio integer,
	cantidad integer,
	foto longblob
);

create table carrito_compra (
	id_entrada integer auto_increment primary key,
	id_articulo integer,
	cantidad integer
);
ALTER TABLE carrito_compra
ADD FOREIGN KEY (id_articulo) REFERENCES articulos(id_articulo);
