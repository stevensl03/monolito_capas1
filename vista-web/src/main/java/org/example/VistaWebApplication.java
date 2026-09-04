package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.controlador_servicio.ProductoControladorServicio;
import org.example.dominio_negocio.ProductoServicio;
import org.example.persistencia.ProductoRepositorioH2;

import java.io.IOException;
import java.net.InetSocketAddress;

public class VistaWebApplication {

    public static void main(String[] args) throws IOException {
        int puerto = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);

        ProductoControladorServicio controlador = new ProductoControladorServicio(
                new ProductoServicio(new ProductoRepositorioH2()));

        // Asignar el manejador de peticiones para la ruta /productos
        server.createContext("/productos", new ProductoWebHandler(controlador));
        server.setExecutor(null);

        System.out.println("Servidor corriendo en http://localhost:" + puerto + "/productos");
        server.start();
    }
}