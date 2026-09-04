package org.example.dominio_negocio;

import org.example.persistencia.ProductoRepositorio;

import java.util.List;

public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;

    public ProductoServicio(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
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
