package org.example.inventario.componente.dominio_negocio;

public class ProductoNoEncontradoException extends NegocioException {

    public ProductoNoEncontradoException(String codigo) {
        super("No existe un producto con el codigo '" + codigo + "'");
    }
}
