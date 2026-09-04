package org.example.controlador_servicio;

import org.example.dominio_negocio.Producto;
import org.example.dominio_negocio.ProductoServicio;

import java.util.List;

public class ProductoControladorServicio {

    private final ProductoServicio productoServicio;

    public ProductoControladorServicio(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    public boolean crearProducto(String nombre, double precio, int stock) {
        Producto producto = new Producto(nombre, precio, stock);
        return productoServicio.crearProducto(nombre, precio, stock);
    }

    public List<Producto> listarProductos() {
        return productoServicio.listarProductos();
    }

    public boolean actualizarStock(int id, int nuevoStock) {
        return productoServicio.actualizarStock(id, nuevoStock);
    }
}
