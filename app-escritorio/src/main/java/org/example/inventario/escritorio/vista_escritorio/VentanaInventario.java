package org.example.inventario.escritorio.vista_escritorio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.example.inventario.componente.controlador_servicio.InventarioServicio;
import org.example.inventario.componente.dominio_negocio.NegocioException;
import org.example.inventario.componente.dominio_negocio.Producto;

/**
 * Ventana principal de la vista de escritorio (Swing, incluido en el JDK).
 */
class VentanaInventario extends JFrame {

    private final InventarioServicio servicio;
    private final ProductoTableModel modelo = new ProductoTableModel();
    private final JLabel barraEstado = new JLabel();

    private final JTextField campoCodigo = new JTextField(7);
    private final JTextField campoNombre = new JTextField(16);
    private final JTextField campoCategoria = new JTextField(10);
    private final JTextField campoPrecio = new JTextField(8);
    private final JTextField campoStock = new JTextField(5);
    private final JTextField campoMinimo = new JTextField(5);

    private final JTextField campoCodigoMovimiento = new JTextField(7);
    private final JTextField campoCantidad = new JTextField(5);
    private final JComboBox<String> tipoMovimiento = new JComboBox<>(new String[]{"Entrada", "Salida"});

    VentanaInventario(InventarioServicio servicio) {
        super("Inventario de productos");
        this.servicio = servicio;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 8));

        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);
        add(construirPanelInferior(), BorderLayout.SOUTH);

        setSize(950, 620);
        setLocationRelativeTo(null);

        refrescar();
    }

    private Component construirEncabezado() {
        JLabel titulo = new JLabel("Inventario de productos");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 8, 12));
        panel.add(titulo);
        return panel;
    }

    private Component construirTabla() {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane desplazador = new JScrollPane(tabla);
        desplazador.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        desplazador.setPreferredSize(new Dimension(900, 330));
        return desplazador;
    }

    private Component construirPanelInferior() {
        JPanel inferior = new JPanel(new BorderLayout(0, 4));
        inferior.add(construirFormularioAlta(), BorderLayout.NORTH);
        inferior.add(construirFormularioMovimiento(), BorderLayout.CENTER);

        barraEstado.setBorder(BorderFactory.createEmptyBorder(6, 12, 10, 12));
        barraEstado.setHorizontalAlignment(SwingConstants.LEFT);
        inferior.add(barraEstado, BorderLayout.SOUTH);
        return inferior;
    }

    private JPanel construirFormularioAlta() {

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        fila1.add(new JLabel("Codigo:"));
        fila1.add(campoCodigo);
        fila1.add(new JLabel("Nombre:"));
        fila1.add(campoNombre);
        fila1.add(new JLabel("Categoria:"));
        campoCategoria.setText("GENERAL");
        fila1.add(campoCategoria);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        fila2.add(new JLabel("Precio:"));
        fila2.add(campoPrecio);
        fila2.add(new JLabel("Stock:"));
        campoStock.setText("0");
        fila2.add(campoStock);
        fila2.add(new JLabel("Minimo:"));
        campoMinimo.setText("0");
        fila2.add(campoMinimo);

        JButton agregar = new JButton("Agregar");
        agregar.addActionListener(evento -> agregarProducto());
        fila2.add(Box.createHorizontalStrut(12));
        fila2.add(agregar);

        JPanel panel = new JPanel(new java.awt.GridLayout(2, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Registrar producto"));
        panel.add(fila1);
        panel.add(fila2);
        return panel;
    }

    private JPanel construirFormularioMovimiento() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Movimiento de stock"));

        panel.add(new JLabel("Codigo:"));
        panel.add(campoCodigoMovimiento);
        panel.add(new JLabel("Cantidad:"));
        campoCantidad.setText("1");
        panel.add(campoCantidad);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoMovimiento);

        JButton aplicar = new JButton("Aplicar");
        aplicar.addActionListener(evento -> aplicarMovimiento());
        panel.add(Box.createHorizontalStrut(8));
        panel.add(aplicar);

        JButton refrescar = new JButton("Refrescar");
        refrescar.addActionListener(evento -> refrescar());
        panel.add(refrescar);
        return panel;
    }

    private void agregarProducto() {
        ejecutar(() -> {
            servicio.registrarProducto(
                    campoCodigo.getText(),
                    campoNombre.getText(),
                    campoCategoria.getText(),
                    new BigDecimal(campoPrecio.getText().trim()),
                    Integer.parseInt(campoStock.getText().trim()),
                    Integer.parseInt(campoMinimo.getText().trim()));
            campoCodigo.setText("");
            campoNombre.setText("");
            campoPrecio.setText("");
            campoStock.setText("0");
            campoMinimo.setText("0");
        });
    }

    private void aplicarMovimiento() {
        ejecutar(() -> {
            String codigo = campoCodigoMovimiento.getText();
            int cantidad = Integer.parseInt(campoCantidad.getText().trim());
            if ("Entrada".equals(tipoMovimiento.getSelectedItem())) {
                servicio.registrarEntrada(codigo, cantidad);
            } else {
                servicio.registrarSalida(codigo, cantidad);
            }
            campoCodigoMovimiento.setText("");
            campoCantidad.setText("1");
        });
    }

    /** Ejecuta una accion del usuario y traduce los fallos a un dialogo. */
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
            refrescar();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Regla de negocio", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Revise los campos numericos: " + e.getMessage(),
                    "Dato invalido", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, String.valueOf(e.getMessage()),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescar() {
        List<Producto> productos = servicio.listarProductos();
        modelo.reemplazar(productos);

        List<Producto> bajos = servicio.productosBajoStock();
        String alerta = bajos.isEmpty()
                ? "sin alertas"
                : bajos.size() + " producto(s) por reponer: "
                        + bajos.stream().map(Producto::getCodigo).toList();

        barraEstado.setText(String.format(Locale.US,
                "Productos: %d    |    Valor total: %,.2f    |    %s",
                productos.size(), servicio.valorTotalInventario(), alerta));
    }
}
