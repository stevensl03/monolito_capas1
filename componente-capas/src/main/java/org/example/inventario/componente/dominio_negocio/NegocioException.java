package org.example.inventario.componente.dominio_negocio;

/** Falla causada por una regla de negocio */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
