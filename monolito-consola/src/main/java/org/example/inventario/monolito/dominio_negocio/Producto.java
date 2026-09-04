package org.example.inventario.monolito.dominio_negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


public class Producto {

    private final Integer id;
    private final String codigo;
    private final String nombre;
    private final String categoria;
    private final BigDecimal precio;
    private final int stock;
    private final int stockMinimo;

    public Producto(Integer id, String codigo, String nombre, String categoria,
                    BigDecimal precio, int stock, int stockMinimo) {
        this.id = id;
        this.codigo = codigo == null ? null : codigo.trim().toUpperCase();
        this.nombre = nombre == null ? null : nombre.trim();
        this.categoria = categoria == null || categoria.isBlank() ? "GENERAL" : categoria.trim();
        this.precio = precio == null ? null : precio.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    /** Producto todavía sin identificador asignado por la capa de persistencia. */
    public Producto(String codigo, String nombre, String categoria,
                    BigDecimal precio, int stock, int stockMinimo) {
        this(null, codigo, nombre, categoria, precio, stock, stockMinimo);
    }

    /** Copia con el id */
    public Producto conId(Integer nuevoId) {
        return new Producto(nuevoId, codigo, nombre, categoria, precio, stock, stockMinimo);
    }

    /** Copia con otra cantidad en existencia. */
    public Producto conStock(int nuevoStock) {
        return new Producto(id, codigo, nombre, categoria, precio, nuevoStock, stockMinimo);
    }

    /** Regla: cuánto dinero representa este producto */
    public BigDecimal valorEnInventario() {
        return precio.multiply(BigDecimal.valueOf(stock));
    }

    /** Regla: el producto necesita reabastecimiento. */
    public boolean estaBajoStock() {
        return stock <= stockMinimo;
    }

    public Integer getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        return otro instanceof Producto p && Objects.equals(codigo, p.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "%s | %-24s | %-12s | %10s | stock %4d%s"
                .formatted(codigo, nombre, categoria, precio, stock, estaBajoStock() ? "  <-- BAJO" : "");
    }
}
