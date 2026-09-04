package org.example;

import org.example.controlador_servicio.ProductoControladorServicio;
import org.example.dominio_negocio.Producto;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ProductoControladorServicio controlador = new ProductoControladorServicio();
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n--- Inventario de productos ---");
            System.out.println("1. Registrar producto");
            System.out.println("2. Listar productos");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    System.out.print("Nombre: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Precio: ");
                    double precio = Double.parseDouble(scanner.nextLine());
                    System.out.print("Stock: ");
                    int stock = Integer.parseInt(scanner.nextLine());
                    controlador.crearProducto(nombre, precio, stock);
                    System.out.println("Producto registrado.");
                    break;
                case 2:
                    List<Producto> productos = controlador.listarProductos();
                    productos.forEach(System.out::println);
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }
}
