package org.example.inventario.monolito.dominio_negocio;

/** Falla causada por una regla de negocio */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
