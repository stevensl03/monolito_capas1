package org.example.inventario.web.vista_web;

import com.sun.net.httpserver.HttpServer;
import org.example.inventario.componente.controlador_servicio.FabricaComponente;
import org.example.inventario.componente.controlador_servicio.InventarioServicio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * PROYECTO 3: Vista Web (MVC) montada sobre el componente de capas.
 *
 * Todo el servidor es JDK puro (com.sun.net.httpserver, incluido desde Java 6);
 * no hay Spring, ni Servlets, ni contenedor externo.
 *
 * Fijarse en la unica linea que conecta con el negocio:
 *     InventarioServicio servicio = FabricaComponente.crear();
 * Ese es el "puerto" del componente. La vista no importa DAOs ni conexiones.
 */
public class ServidorWeb {

    private static final int PUERTO = 8080;

    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : PUERTO;

        InventarioServicio servicio = FabricaComponente.crear();

        HttpServer servidor = HttpServer.create(new InetSocketAddress(puerto), 0);
        servidor.createContext("/", new ProductoControladorWeb(servicio));
        servidor.setExecutor(Executors.newFixedThreadPool(4));
        servidor.start();

        System.out.println();
        System.out.println("Servidor Web de Inventario:");
        System.out.println("  Productos : " + servicio.totalProductos());
        System.out.println("  Abrir en  : http://localhost:" + puerto + "/");
        System.out.println("  JSON      : http://localhost:" + puerto + "/api/productos");
        System.out.println("  (Ctrl+C para detener)");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Deteniendo el servidor...");
            servidor.stop(0);
        }));
    }
}
