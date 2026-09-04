package org.example.persistencia;

import org.example.dominio_negocio.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductoRepositorioSimulado implements ProductoRepositorio {

    private final List<Producto> almacen = new ArrayList<>();
    private final AtomicInteger contadorId = new AtomicInteger(0);

    @Override
    public boolean guardar(Producto producto) {
        producto.setId(contadorId.incrementAndGet());
        almacen.add(producto);
        return true;
    }

    @Override
    public List<Producto> listarProductos() {
        return new ArrayList<>(almacen);
    }

    @Override
    public boolean actualizarStock(int id, int nuevoStock) {
        for (Producto producto : almacen) {
            if (producto.getId().equals(id)) {
                producto.setStock(nuevoStock);
                return true;
            }
        }
        return false;
    }
}
