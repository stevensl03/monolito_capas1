package org.example.inventario.componente.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.inventario.componente.dominio_negocio.Producto;

/**
 * Mecanica JDBC comun a los dos motores.

 */
abstract class ProductoDaoJdbc implements ProductoDao {

    protected abstract String sqlInsertar();

    protected abstract String sqlActualizar();

    protected abstract String sqlActualizarStock();

    protected abstract String sqlBuscarPorId();

    protected abstract String sqlBuscarPorCodigo();

    protected abstract String sqlListarTodos();

    protected abstract String sqlBuscarPorNombre();

    protected abstract String sqlEliminar();

    protected abstract String sqlContar();

    private Connection conexion() {
        return FabricaConexion.obtener().obtenerConexion();
    }

    @Override
    public Producto guardar(Producto producto) {
        try (PreparedStatement sentencia =
                     conexion().prepareStatement(sqlInsertar(), Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, producto.getCodigo());
            sentencia.setString(2, producto.getNombre());
            sentencia.setString(3, producto.getCategoria());
            sentencia.setBigDecimal(4, producto.getPrecio());
            sentencia.setInt(5, producto.getStock());
            sentencia.setInt(6, producto.getStockMinimo());
            sentencia.executeUpdate();
            try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                if (llaves.next()) {
                    return producto.conId(llaves.getInt(1));
                }
            }
            return producto;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar el producto " + producto.getCodigo(), e);
        }
    }

    @Override
    public boolean actualizar(Producto producto) {
        try (PreparedStatement sentencia = conexion().prepareStatement(sqlActualizar())) {
            sentencia.setString(1, producto.getNombre());
            sentencia.setString(2, producto.getCategoria());
            sentencia.setBigDecimal(3, producto.getPrecio());
            sentencia.setInt(4, producto.getStock());
            sentencia.setInt(5, producto.getStockMinimo());
            sentencia.setString(6, producto.getCodigo());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar el producto " + producto.getCodigo(), e);
        }
    }

    @Override
    public boolean actualizarStock(String codigo, int nuevoStock) {
        try (PreparedStatement sentencia = conexion().prepareStatement(sqlActualizarStock())) {
            sentencia.setInt(1, nuevoStock);
            sentencia.setString(2, codigo);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar el stock de " + codigo, e);
        }
    }

    @Override
    public Optional<Producto> buscarPorId(int id) {
        try (PreparedStatement sentencia = conexion().prepareStatement(sqlBuscarPorId())) {
            sentencia.setInt(1, id);
            return primerResultado(sentencia);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el producto con id " + id, e);
        }
    }

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        try (PreparedStatement sentencia = conexion().prepareStatement(sqlBuscarPorCodigo())) {
            sentencia.setString(1, codigo == null ? null : codigo.trim().toUpperCase());
            return primerResultado(sentencia);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el producto " + codigo, e);
        }
    }

    @Override
    public List<Producto> listarTodos() {
        try (PreparedStatement sentencia = conexion().prepareStatement(sqlListarTodos())) {
            return todosLosResultados(sentencia);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los productos", e);
        }
    }

    @Override
    public List<Producto> buscarPorNombre(String fragmento) {
        try (PreparedStatement sentencia = conexion().prepareStatement(sqlBuscarPorNombre())) {
            sentencia.setString(1, "%" + (fragmento == null ? "" : fragmento.trim().toUpperCase()) + "%");
            return todosLosResultados(sentencia);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar productos por nombre", e);
        }
    }

    @Override
    public boolean eliminar(String codigo) {
        try (PreparedStatement sentencia = conexion().prepareStatement(sqlEliminar())) {
            sentencia.setString(1, codigo == null ? null : codigo.trim().toUpperCase());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar el producto " + codigo, e);
        }
    }

    @Override
    public int contar() {
        try (PreparedStatement sentencia = conexion().prepareStatement(sqlContar());
             ResultSet resultado = sentencia.executeQuery()) {
            return resultado.next() ? resultado.getInt(1) : 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al contar los productos", e);
        }
    }

    private Optional<Producto> primerResultado(PreparedStatement sentencia) throws SQLException {
        try (ResultSet resultado = sentencia.executeQuery()) {
            return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
        }
    }

    private List<Producto> todosLosResultados(PreparedStatement sentencia) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                productos.add(mapear(resultado));
            }
        }
        return productos;
    }

    /** Traduce una fila de la tabla a un objeto del dominio. */
    private Producto mapear(ResultSet resultado) throws SQLException {
        return new Producto(
                resultado.getInt("id"),
                resultado.getString("codigo"),
                resultado.getString("nombre"),
                resultado.getString("categoria"),
                resultado.getBigDecimal("precio"),
                resultado.getInt("stock"),
                resultado.getInt("stock_minimo"));
    }
}
