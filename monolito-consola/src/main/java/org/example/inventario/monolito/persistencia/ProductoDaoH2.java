package org.example.inventario.monolito.persistencia;

/**
 * DAO con SQL estandar, el que entiende H2 de forma nativa.
 * Comparar con {@link ProductoDaoMysql} para ver la diferencia de dialecto.
 */
public class ProductoDaoH2 extends ProductoDaoJdbc {

    private static final String COLUMNAS = "id, codigo, nombre, categoria, precio, stock, stock_minimo";

    @Override
    protected String sqlInsertar() {
        return "INSERT INTO productos (codigo, nombre, categoria, precio, stock, stock_minimo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String sqlActualizar() {
        return "UPDATE productos SET nombre = ?, categoria = ?, precio = ?, stock = ?, stock_minimo = ? "
                + "WHERE codigo = ?";
    }

    @Override
    protected String sqlActualizarStock() {
        return "UPDATE productos SET stock = ? WHERE codigo = ?";
    }

    @Override
    protected String sqlBuscarPorId() {
        return "SELECT " + COLUMNAS + " FROM productos WHERE id = ?";
    }

    @Override
    protected String sqlBuscarPorCodigo() {
        return "SELECT " + COLUMNAS + " FROM productos WHERE codigo = ?";
    }

    @Override
    protected String sqlListarTodos() {
        return "SELECT " + COLUMNAS + " FROM productos ORDER BY codigo";
    }

    @Override
    protected String sqlBuscarPorNombre() {
        return "SELECT " + COLUMNAS + " FROM productos WHERE UPPER(nombre) LIKE ? ORDER BY codigo";
    }

    @Override
    protected String sqlEliminar() {
        return "DELETE FROM productos WHERE codigo = ?";
    }

    @Override
    protected String sqlContar() {
        return "SELECT COUNT(*) FROM productos";
    }

    @Override
    public String nombreMotor() {
        return "H2";
    }
}
