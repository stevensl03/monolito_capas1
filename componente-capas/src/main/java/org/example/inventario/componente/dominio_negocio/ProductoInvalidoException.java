package org.example.inventario.componente.dominio_negocio;

public class ProductoInvalidoException extends NegocioException {

    public ProductoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
