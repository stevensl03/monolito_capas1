package org.example.inventario.monolito.vista_consola;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.example.inventario.monolito.controlador_servicio.EnsambladorCapas;
import org.example.inventario.monolito.controlador_servicio.InventarioServicio;
import org.example.inventario.monolito.dominio_negocio.NegocioException;
import org.example.inventario.monolito.dominio_negocio.Producto;

/**
 * Capa Vista (consola) del MONOLITO.
 *
 */
public class MainConsola {

    private static final String SEPARADOR = "-".repeat(78);

    public static void main(String[] args) {
        InventarioServicio servicio = EnsambladorCapas.montar();

        imprimirEncabezado(servicio);
        imprimirInventario(servicio);

        try (Scanner teclado = new Scanner(System.in)) {
            bucleMenu(servicio, teclado);
        }
        System.out.println("Fin de la aplicacion.");
    }

    private static void bucleMenu(InventarioServicio servicio, Scanner teclado) {
        while (true) {
            imprimirMenu();
            if (!teclado.hasNextLine()) {
                // Sin entrada disponible (por ejemplo al canalizar la salida):
                // el reporte ya se mostro, se termina sin error.
                System.out.println();
                return;
            }
            String opcion = limpiar(teclado.nextLine());
            try {
                switch (opcion) {
                    case "1" -> registrarProducto(servicio, teclado);
                    case "2" -> moverStock(servicio, teclado, true);
                    case "3" -> moverStock(servicio, teclado, false);
                    case "4" -> imprimirInventario(servicio);
                    case "0", "" -> {
                        return;
                    }
                    default -> System.out.println("Opcion no reconocida: " + opcion);
                }
            } catch (NegocioException e) {
                System.out.println("  [regla de negocio] " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("  [error] " + e.getMessage());
            }
        }
    }

    private static void registrarProducto(InventarioServicio servicio, Scanner teclado) {
        String codigo = pedir(teclado, "Codigo");
        String nombre = pedir(teclado, "Nombre");
        String categoria = pedir(teclado, "Categoria");
        BigDecimal precio = new BigDecimal(pedir(teclado, "Precio"));
        int stock = Integer.parseInt(pedir(teclado, "Stock"));
        int stockMinimo = Integer.parseInt(pedir(teclado, "Stock minimo"));

        Producto creado = servicio.registrarProducto(codigo, nombre, categoria, precio, stock, stockMinimo);
        System.out.println("  Registrado: " + creado.getCodigo() + " (id " + creado.getId() + ")");
        imprimirInventario(servicio);
    }

    private static void moverStock(InventarioServicio servicio, Scanner teclado, boolean esEntrada) {
        String codigo = pedir(teclado, "Codigo");
        int cantidad = Integer.parseInt(pedir(teclado, "Cantidad"));
        Producto resultado = esEntrada
                ? servicio.registrarEntrada(codigo, cantidad)
                : servicio.registrarSalida(codigo, cantidad);
        System.out.println("  " + (esEntrada ? "Entrada" : "Salida") + " aplicada. "
                + resultado.getCodigo() + " queda con stock " + resultado.getStock());
        imprimirInventario(servicio);
    }

    private static String pedir(Scanner teclado, String etiqueta) {
        System.out.print("  " + etiqueta + ": ");
        if (!teclado.hasNextLine()) {
            throw new IllegalStateException("Entrada terminada");
        }
        return limpiar(teclado.nextLine());
    }

    
    private static String limpiar(String linea) {
        return linea.replace("\uFEFF", "").trim();
    }

    private static void imprimirEncabezado(InventarioServicio servicio) {
        System.out.println();
        System.out.println(SEPARADOR);
        System.out.println("  INVENTARIO DE PRODUCTOS - CONSOLA");
        System.out.println(SEPARADOR);
    }

    private static void imprimirInventario(InventarioServicio servicio) {
        List<Producto> productos = servicio.listarProductos();

        System.out.println();
        System.out.printf("%-8s %-26s %-16s %14s %8s %8s%n",
                "CODIGO", "NOMBRE", "CATEGORIA", "PRECIO", "STOCK", "MINIMO");
        System.out.println(SEPARADOR);
        for (Producto producto : productos) {
            System.out.printf(Locale.US, "%-8s %-26s %-16s %14.2f %8d %8d%s%n",
                    producto.getCodigo(),
                    recortar(producto.getNombre(), 26),
                    recortar(producto.getCategoria(), 16),
                    producto.getPrecio(),
                    producto.getStock(),
                    producto.getStockMinimo(),
                    producto.estaBajoStock() ? "  <- reponer" : "");
        }
        System.out.println(SEPARADOR);
        System.out.printf(Locale.US, "  Productos: %d      Valor total del inventario: %,.2f%n",
                servicio.totalProductos(), servicio.valorTotalInventario());

        List<Producto> bajos = servicio.productosBajoStock();
        if (!bajos.isEmpty()) {
            System.out.println("  Bajo stock (" + bajos.size() + "): "
                    + bajos.stream().map(Producto::getCodigo).toList());
        }
    }

    private static void imprimirMenu() {
        System.out.println();
        System.out.println("  1) Registrar producto    2) Entrada de stock");
        System.out.println("  3) Salida de stock       4) Refrescar listado       0) Salir");
        System.out.print("  Opcion: ");
    }

    private static String recortar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 1) + ".";
    }
}
