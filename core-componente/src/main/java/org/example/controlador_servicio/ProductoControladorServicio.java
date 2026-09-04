package org.example.controlador_servicio;

import org.example.dominio_negocio.Producto;
import org.example.persistencia.ProductoRepositorio;

import java.util.List;

public class ProductoControladorServicio {

    private final ProductoRepositorio productoRepositorio;

    public ProductoControladorServicio() {
        this.productoRepositorio = ProductoRepositorio.inicializarProductoRepositorio();
    }

    public boolean crearProducto(String nombre, double precio, int stock) {
        Producto producto = new Producto(nombre, precio, stock);
        return productoRepositorio.guardar(producto);
    }

    public List<Producto> listarProductos() {
        return productoRepositorio.listarProductos();
    }

    public boolean actualizarStock(int id, int nuevoStock) {
        return productoRepositorio.actualizarStock(id, nuevoStock);
    }
}
