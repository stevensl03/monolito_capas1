package org.example.inventario.monolito.persistencia;

import org.example.inventario.monolito.dominio_negocio.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de la capa de persistencia (patron DAO).
 *
 * Gracias a esta interfaz el resto del componente no sabe si detras hay H2 o
 * MySQL: cambiar de motor es cambiar la implementacion, no el servicio.
 */
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
