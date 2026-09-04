package org.example.persistencia;

import org.example.dominio_negocio.Producto;

import java.util.List;

public interface ProductoRepositorio {

    boolean guardar(Producto producto);

    List<Producto> listarProductos();

    boolean actualizarStock(int id, int nuevoStock);
}
