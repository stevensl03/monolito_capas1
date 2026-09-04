package org.example.inventario.escritorio.vista_escritorio;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.example.inventario.componente.dominio_negocio.Producto;

/**
 * Adapta la lista de productos del dominio al modelo que espera JTable.
 * Es codigo de vista.
 */
class ProductoTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS =
            {"Codigo", "Nombre", "Categoria", "Precio", "Stock", "Minimo", "Estado"};

    private List<Producto> productos = new ArrayList<>();

    void reemplazar(List<Producto> nuevos) {
        this.productos = new ArrayList<>(nuevos);
        fireTableDataChanged();
    }

    Producto en(int fila) {
        return productos.get(fila);
    }

    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Producto producto = productos.get(fila);
        return switch (columna) {
            case 0 -> producto.getCodigo();
            case 1 -> producto.getNombre();
            case 2 -> producto.getCategoria();
            case 3 -> producto.getPrecio();
            case 4 -> producto.getStock();
            case 5 -> producto.getStockMinimo();
            case 6 -> producto.estaBajoStock() ? "REPONER" : "OK";
            default -> "";
        };
    }
}
