package org.example.inventario.escritorio.vista_escritorio;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.example.inventario.componente.controlador_servicio.FabricaComponente;
import org.example.inventario.componente.controlador_servicio.InventarioServicio;


public class AppEscritorio {

    public static void main(String[] args) {
        InventarioServicio servicio = FabricaComponente.crear();

        System.out.println();
        System.out.println("Aplicación de Escritorio - Inventario");
        System.out.println("  Productos : " + servicio.totalProductos());

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorado) {
                // Si el look and feel del sistema no esta disponible se usa el de Java.
            }
            new VentanaInventario(servicio).setVisible(true);
        });
    }
}
