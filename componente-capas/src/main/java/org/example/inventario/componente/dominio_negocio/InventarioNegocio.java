package org.example.inventario.componente.dominio_negocio;

import java.math.BigDecimal;
import java.util.List;

/**
 * Capa Dominio / Negocio.
 *
 */
public class InventarioNegocio {

    private static final int LONGITUD_MINIMA_NOMBRE = 3;

    /** Valida un producto*/
    public void validar(Producto producto) {
        if (producto == null) {
            throw new ProductoInvalidoException("El producto no puede ser nulo");
        }
        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            throw new ProductoInvalidoException("El codigo es obligatorio");
        }
        if (producto.getNombre() == null || producto.getNombre().length() < LONGITUD_MINIMA_NOMBRE) {
            throw new ProductoInvalidoException(
                    "El nombre debe tener al menos " + LONGITUD_MINIMA_NOMBRE + " caracteres");
        }
        if (producto.getPrecio() == null || producto.getPrecio().signum() <= 0) {
            throw new ProductoInvalidoException("El precio debe ser mayor que cero");
        }
        if (producto.getStock() < 0) {
            throw new ProductoInvalidoException("El stock no puede ser negativo");
        }
        if (producto.getStockMinimo() < 0) {
            throw new ProductoInvalidoException("El stock minimo no puede ser negativo");
        }
    }

    /** Entrada de mercancia */
    public Producto aplicarEntrada(Producto producto, int cantidad) {
        exigirCantidadPositiva(cantidad);
        return producto.conStock(producto.getStock() + cantidad);
    }

    /** Salida de mercancia */
    public Producto aplicarSalida(Producto producto, int cantidad) {
        exigirCantidadPositiva(cantidad);
        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException(producto.getCodigo(), producto.getStock(), cantidad);
        }
        return producto.conStock(producto.getStock() - cantidad);
    }

    /** Suma el valor en bodega  */
    public BigDecimal valorTotal(List<Producto> productos) {
        return productos.stream()
                .map(Producto::valorEnInventario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Productos que alcanzaron o cruzaron su punto de reorden. */
    public List<Producto> filtrarBajoStock(List<Producto> productos) {
        return productos.stream().filter(Producto::estaBajoStock).toList();
    }

    private void exigirCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new ProductoInvalidoException("La cantidad del movimiento debe ser mayor que cero");
        }
    }
}
