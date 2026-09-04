package org.example.inventario.monolito.persistencia;

/** Falla tecnica de la capa de persistencia (JDBC, driver, esquema). */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}
