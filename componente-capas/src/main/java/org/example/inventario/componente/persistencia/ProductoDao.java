package org.example.inventario.componente.persistencia;

import java.util.List;
import java.util.Optional;

import org.example.inventario.componente.dominio_negocio.Producto;


public interface ProductoDao {

    Producto guardar(Producto producto);

    boolean actualizar(Producto producto);

    boolean actualizarStock(String codigo, int nuevoStock);

    Optional<Producto> buscarPorId(int id);

    Optional<Producto> buscarPorCodigo(String codigo);

    List<Producto> listarTodos();

    List<Producto> buscarPorNombre(String fragmento);

    boolean eliminar(String codigo);

    int contar();

    /** Nombre legible del motor que atiende este DAO. Se muestra en las vistas. */
    String nombreMotor();
}
