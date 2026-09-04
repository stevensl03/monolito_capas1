package org.example.inventario.monolito.dominio_negocio;

public class StockInsuficienteException extends NegocioException {

    public StockInsuficienteException(String codigo, int disponible, int solicitado) {
        super("Stock insuficiente para '%s': hay %d y se solicitan %d"
                .formatted(codigo, disponible, solicitado));
    }
}
