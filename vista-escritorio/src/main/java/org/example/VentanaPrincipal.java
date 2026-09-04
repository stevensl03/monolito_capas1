package org.example;

import org.example.controlador_servicio.ProductoControladorServicio;
import org.example.dominio_negocio.Producto;
import org.example.dominio_negocio.ProductoServicio;
import org.example.persistencia.ProductoRepositorioH2;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private final ProductoControladorServicio controlador = new ProductoControladorServicio(
            new ProductoServicio(new ProductoRepositorioH2()));
    private final JTextField campoNombre = new JTextField(10);
    private final JTextField campoPrecio = new JTextField(5);
    private final JTextField campoStock = new JTextField(5);
    private final JTextArea areaListado = new JTextArea(10, 30);

    public VentanaPrincipal() {
        super("Inventario de productos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formulario = new JPanel();
        formulario.add(new JLabel("Nombre:"));
        formulario.add(campoNombre);
        formulario.add(new JLabel("Precio:"));
        formulario.add(campoPrecio);
        formulario.add(new JLabel("Stock:"));
        formulario.add(campoStock);
        JButton botonAgregar = new JButton("Agregar");
        botonAgregar.addActionListener(evento -> agregarProducto());
        formulario.add(botonAgregar);

        areaListado.setEditable(false);
        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(areaListado), BorderLayout.CENTER);

        actualizarListado();
        pack();
        setLocationRelativeTo(null);
    }

    private void agregarProducto() {
        String nombre = campoNombre.getText();
        double precio = Double.parseDouble(campoPrecio.getText());
        int stock = Integer.parseInt(campoStock.getText());
        controlador.crearProducto(nombre, precio, stock);
        actualizarListado();
    }

    private void actualizarListado() {
        List<Producto> productos = controlador.listarProductos();
        StringBuilder texto = new StringBuilder();
        productos.forEach(producto -> texto.append(producto).append("\n"));
        areaListado.setText(texto.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
