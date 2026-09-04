package org.example.persistencia;

import org.example.dominio_negocio.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositorioMySQL implements ProductoRepositorio {

    private Connection conexion;

    public ProductoRepositorioMySQL() {
        try {
            conexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inventariodb",
                    "root",
                    "");
            try (Statement crearTabla = conexion.createStatement()) {
                crearTabla.execute("CREATE TABLE IF NOT EXISTS productos (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "nombre VARCHAR(100), " +
                        "precio DOUBLE, " +
                        "stock INT)");
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo conectar a MySQL", e);
        }
    }

    @Override
    public boolean guardar(Producto producto) {
        String sql = "INSERT INTO productos (nombre, precio, stock) VALUES (?, ?, ?)";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, producto.getNombre());
            statement.setDouble(2, producto.getPrecio());
            statement.setInt(3, producto.getStock());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el producto en MySQL", e);
        }
    }

    @Override
    public List<Producto> listarProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, stock FROM productos";
        try (Statement statement = conexion.createStatement();
             ResultSet resultado = statement.executeQuery(sql)) {
            while (resultado.next()) {
                productos.add(new Producto(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getDouble("precio"),
                        resultado.getInt("stock")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los productos en MySQL", e);
        }
        return productos;
    }

    @Override
    public boolean actualizarStock(int id, int nuevoStock) {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, nuevoStock);
            statement.setInt(2, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el stock en MySQL", e);
        }
    }
}
