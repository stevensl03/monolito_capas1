package org.example.inventario.monolito.controlador_servicio;

import java.math.BigDecimal;
import java.util.List;

import org.example.inventario.monolito.dominio_negocio.InventarioNegocio;
import org.example.inventario.monolito.dominio_negocio.Producto;
import org.example.inventario.monolito.dominio_negocio.ProductoInvalidoException;
import org.example.inventario.monolito.dominio_negocio.ProductoNoEncontradoException;
import org.example.inventario.monolito.persistencia.FabricaConexion;
import org.example.inventario.monolito.persistencia.ProductoDao;

/**
 * Capa Controlador / Servicio.
 *
 */
public class InventarioServicio {

    private final ProductoDao productoDao;
    private final InventarioNegocio negocio;

    public InventarioServicio(ProductoDao productoDao, InventarioNegocio negocio) {
        this.productoDao = productoDao;
        this.negocio = negocio;
    }

    /** Caso de uso: dar de alta un producto. */
    public Producto registrarProducto(String codigo, String nombre, String categoria,
                                      BigDecimal precio, int stock, int stockMinimo) {
        Producto producto = new Producto(codigo, nombre, categoria, precio, stock, stockMinimo);
        negocio.validar(producto);
        productoDao.buscarPorCodigo(producto.getCodigo()).ifPresent(existente -> {
            throw new ProductoInvalidoException(
                    "Ya existe un producto con el codigo '" + existente.getCodigo() + "'");
        });
        return productoDao.guardar(producto);
    }

    public List<Producto> listarProductos() {
        return productoDao.listarTodos();
    }

    public List<Producto> buscarPorNombre(String fragmento) {
        return productoDao.buscarPorNombre(fragmento);
    }

    public Producto buscarPorCodigo(String codigo) {
        return productoDao.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ProductoNoEncontradoException(codigo));
    }

    /** Caso de uso: entrada de mercancia. */
    public Producto registrarEntrada(String codigo, int cantidad) {
        Producto actual = buscarPorCodigo(codigo);
        Producto actualizado = negocio.aplicarEntrada(actual, cantidad);
        productoDao.actualizarStock(actualizado.getCodigo(), actualizado.getStock());
        return actualizado;
    }

    /** Caso de uso: salida de mercancia. Falla si no alcanza el stock. */
    public Producto registrarSalida(String codigo, int cantidad) {
        Producto actual = buscarPorCodigo(codigo);
        Producto actualizado = negocio.aplicarSalida(actual, cantidad);
        productoDao.actualizarStock(actualizado.getCodigo(), actualizado.getStock());
        return actualizado;
    }

    public boolean eliminar(String codigo) {
        buscarPorCodigo(codigo);
        return productoDao.eliminar(codigo);
    }

    public BigDecimal valorTotalInventario() {
        return negocio.valorTotal(productoDao.listarTodos());
    }

    public List<Producto> productosBajoStock() {
        return negocio.filtrarBajoStock(productoDao.listarTodos());
    }

    public int totalProductos() {
        return productoDao.contar();
    }

    /** Texto informativo del motor activo, para mostrarlo en cualquier vista. */
    public String motorActivo() {
        return productoDao.nombreMotor() + " -> " + FabricaConexion.obtener().descripcionMotor();
    }
}
