package org.example.inventario.monolito.dominio_negocio;

public class ProductoInvalidoException extends NegocioException {

    public ProductoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
