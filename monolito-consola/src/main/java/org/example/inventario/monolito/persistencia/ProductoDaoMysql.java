package org.example.inventario.monolito.persistencia;

/**
 * DAO con dialecto MySQL: identificadores entre acentos graves (`) y LIMIT 1 en
 * las busquedas de un solo registro.
 *
 * El SQL es real y ejecutable contra un servidor MySQL. Cuando ese servidor no
 * existe, {@link FabricaConexion} simula la conexion con H2 en MODE=MySQL, que
 * acepta esta misma sintaxis; por eso este DAO funciona en ambos escenarios sin
 * un solo cambio.
 */
public class ProductoDaoMysql extends ProductoDaoJdbc {

    private static final String COLUMNAS =
            "`id`, `codigo`, `nombre`, `categoria`, `precio`, `stock`, `stock_minimo`";

    @Override
    protected String sqlInsertar() {
        return "INSERT INTO `productos` "
                + "(`codigo`, `nombre`, `categoria`, `precio`, `stock`, `stock_minimo`) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String sqlActualizar() {
        return "UPDATE `productos` SET `nombre` = ?, `categoria` = ?, `precio` = ?, "
                + "`stock` = ?, `stock_minimo` = ? WHERE `codigo` = ?";
    }

    @Override
    protected String sqlActualizarStock() {
        return "UPDATE `productos` SET `stock` = ? WHERE `codigo` = ?";
    }

    @Override
    protected String sqlBuscarPorId() {
        return "SELECT " + COLUMNAS + " FROM `productos` WHERE `id` = ? LIMIT 1";
    }

    @Override
    protected String sqlBuscarPorCodigo() {
        return "SELECT " + COLUMNAS + " FROM `productos` WHERE `codigo` = ? LIMIT 1";
    }

    @Override
    protected String sqlListarTodos() {
        return "SELECT " + COLUMNAS + " FROM `productos` ORDER BY `codigo` ASC";
    }

    @Override
    protected String sqlBuscarPorNombre() {
        return "SELECT " + COLUMNAS + " FROM `productos` "
                + "WHERE UPPER(`nombre`) LIKE ? ORDER BY `codigo` ASC";
    }

    @Override
    protected String sqlEliminar() {
        return "DELETE FROM `productos` WHERE `codigo` = ?";
    }

    @Override
    protected String sqlContar() {
        return "SELECT COUNT(*) FROM `productos`";
    }

    @Override
    public String nombreMotor() {
        return "MySQL";
    }
}
