package org.example.inventario.monolito.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Crea la tabla y siembra datos de ejemplo. Es idempotente: se puede ejecutar
 * en cada arranque sin duplicar nada.
 *
 * Gracias a esta siembra, cualquiera de las tres vistas muestra informacion
 * util apenas arranca, sin que el usuario tenga que capturar nada.
 */
public final class EsquemaBD {

    private static final String CREAR_TABLA = """
            CREATE TABLE IF NOT EXISTS productos (
                id            INT AUTO_INCREMENT PRIMARY KEY,
                codigo        VARCHAR(30)    NOT NULL UNIQUE,
                nombre        VARCHAR(120)   NOT NULL,
                categoria     VARCHAR(60)    NOT NULL,
                precio        DECIMAL(12,2)  NOT NULL,
                stock         INT            NOT NULL,
                stock_minimo  INT            NOT NULL
            )
            """;

    private static final String INSERTAR_SEMILLA = """
            INSERT INTO productos (codigo, nombre, categoria, precio, stock, stock_minimo)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final Object[][] SEMILLA = {
            {"P001", "Teclado mecanico RGB",   "PERIFERICOS", "189900.00", 25,  5},
            {"P002", "Mouse inalambrico",      "PERIFERICOS",  "79900.00", 40, 10},
            {"P003", "Monitor 24 pulgadas",    "PANTALLAS",   "749000.00",  8,  3},
            {"P004", "Disco SSD 1TB",          "ALMACENAMIENTO", "349000.00", 4, 6},
            {"P005", "Memoria RAM 16GB",       "COMPONENTES", "259000.00", 12,  4},
            {"P006", "Cable HDMI 2m",          "ACCESORIOS",   "29900.00",  3,  8},
    };

    private EsquemaBD() {
    }

    public static void preparar(Connection conexion) {
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.execute(CREAR_TABLA);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo crear la tabla 'productos'", e);
        }
        if (estaVacia(conexion)) {
            sembrar(conexion);
        }
    }

    private static boolean estaVacia(Connection conexion) {
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT COUNT(*) FROM productos")) {
            return resultado.next() && resultado.getInt(1) == 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el estado de la tabla", e);
        }
    }

    private static void sembrar(Connection conexion) {
        try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR_SEMILLA)) {
            for (Object[] fila : SEMILLA) {
                sentencia.setString(1, (String) fila[0]);
                sentencia.setString(2, (String) fila[1]);
                sentencia.setString(3, (String) fila[2]);
                sentencia.setBigDecimal(4, new java.math.BigDecimal((String) fila[3]));
                sentencia.setInt(5, (Integer) fila[4]);
                sentencia.setInt(6, (Integer) fila[5]);
                sentencia.addBatch();
            }
            sentencia.executeBatch();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron insertar los datos de ejemplo", e);
        }
    }
}
