package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.controlador_servicio.ProductoControladorServicio;
import org.example.dominio_negocio.Producto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoWebHandler implements HttpHandler {

    private final ProductoControladorServicio controlador;

    public ProductoWebHandler(ProductoControladorServicio controlador) {
        this.controlador = controlador;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();

        if ("POST".equalsIgnoreCase(metodo)) {
            procesarPost(exchange);
        } else {
            procesarGet(exchange);
        }
    }

    private void procesarGet(HttpExchange exchange) throws IOException {
        String html = generarHtml();
        byte[] responseBytes = html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private void procesarPost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String formData = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseFormData(formData);

        String nombre = params.getOrDefault("nombre", "");
        double precio = Double.parseDouble(params.getOrDefault("precio", "0"));
        int stock = Integer.parseInt(params.getOrDefault("stock", "0"));

        controlador.crearProducto(nombre, precio, stock);

        // Redirección POST-Redirect-GET (303) para evitar duplicados al recargar
        exchange.getResponseHeaders().set("Location", "/productos");
        exchange.sendResponseHeaders(303, -1);
    }

    private String generarHtml() {
        List<Producto> productos = controlador.listarProductos();
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Inventario</title></head><body>");
        html.append("<h1>Inventario de productos</h1>");
        html.append("<form method='post' action='/productos'>")
                .append("Nombre: <input name='nombre' required> ")
                .append("Precio: <input name='precio' type='number' step='0.01' required> ")
                .append("Stock: <input name='stock' type='number' required> ")
                .append("<button type='submit'>Registrar</button>")
                .append("</form><br>");

        html.append("<table border='1'><tr><th>Id</th><th>Nombre</th><th>Precio</th><th>Stock</th></tr>");
        for (Producto producto : productos) {
            html.append("<tr><td>").append(producto.getId()).append("</td>")
                    .append("<td>").append(producto.getNombre()).append("</td>")
                    .append("<td>").append(producto.getPrecio()).append("</td>")
                    .append("<td>").append(producto.getStock()).append("</td></tr>");
        }
        html.append("</table></body></html>");
        return html.toString();
    }

    private Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                map.put(key, value);
            }
        }
        return map;
    }
}