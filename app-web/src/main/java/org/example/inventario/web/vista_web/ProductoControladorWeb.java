package org.example.inventario.web.vista_web;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.example.inventario.componente.controlador_servicio.InventarioServicio;
import org.example.inventario.componente.dominio_negocio.NegocioException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


class ProductoControladorWeb implements HttpHandler {

    private final InventarioServicio servicio;

    ProductoControladorWeb(InventarioServicio servicio) {
        this.servicio = servicio;
    }

    @Override
    public void handle(HttpExchange intercambio) throws IOException {
        String ruta = intercambio.getRequestURI().getPath();
        String metodo = intercambio.getRequestMethod();
        try {
            if ("GET".equals(metodo) && ("/".equals(ruta) || "/productos".equals(ruta))) {
                responderPagina(intercambio, mensajeDeLaUrl(intercambio));
            } else if ("GET".equals(metodo) && "/api/productos".equals(ruta)) {
                responder(intercambio, 200, "application/json; charset=utf-8",
                        PlantillaHtml.json(servicio.listarProductos()));
            } else if ("POST".equals(metodo) && "/productos".equals(ruta)) {
                crearProducto(intercambio);
            } else if ("POST".equals(metodo) && "/productos/stock".equals(ruta)) {
                moverStock(intercambio);
            } else {
                responder(intercambio, 404, "text/plain; charset=utf-8", "Ruta no encontrada: " + ruta);
            }
        } catch (RuntimeException e) {
            // Una regla de negocio rota no tumba el servidor.
            responder(intercambio, 500, "text/plain; charset=utf-8", "Error: " + e.getMessage());
        }
    }

    private void crearProducto(HttpExchange intercambio) throws IOException {
        Map<String, String> campos = leerFormulario(intercambio);
        try {
            servicio.registrarProducto(
                    campos.get("codigo"),
                    campos.get("nombre"),
                    campos.getOrDefault("categoria", "GENERAL"),
                    new BigDecimal(campos.getOrDefault("precio", "0")),
                    enteroDe(campos.get("stock")),
                    enteroDe(campos.get("stockMinimo")));
            redirigir(intercambio, "Producto " + campos.get("codigo") + " registrado.");
        } catch (NegocioException | NumberFormatException e) {
            redirigir(intercambio, "No se pudo registrar: " + e.getMessage());
        }
    }

    private void moverStock(HttpExchange intercambio) throws IOException {
        Map<String, String> campos = leerFormulario(intercambio);
        String codigo = campos.get("codigo");
        boolean esEntrada = !"salida".equalsIgnoreCase(campos.getOrDefault("movimiento", "entrada"));
        try {
            int cantidad = enteroDe(campos.get("cantidad"));
            var resultado = esEntrada
                    ? servicio.registrarEntrada(codigo, cantidad)
                    : servicio.registrarSalida(codigo, cantidad);
            redirigir(intercambio, (esEntrada ? "Entrada" : "Salida") + " aplicada a " + codigo
                    + ". Stock actual: " + resultado.getStock());
        } catch (NegocioException | NumberFormatException e) {
            redirigir(intercambio, "No se pudo aplicar el movimiento: " + e.getMessage());
        }
    }

    private void responderPagina(HttpExchange intercambio, String mensaje) throws IOException {
        String html = PlantillaHtml.pagina(
                servicio.listarProductos(),
                servicio.valorTotalInventario(),
                servicio.productosBajoStock(),
                mensaje);
        responder(intercambio, 200, "text/html; charset=utf-8", html);
    }

    /** Patron POST-Redirect-GET: evita reenviar el formulario al recargar. */
    private void redirigir(HttpExchange intercambio, String mensaje) throws IOException {
        String destino = "/?m=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
        intercambio.getResponseHeaders().set("Location", destino);
        intercambio.sendResponseHeaders(302, -1);
        intercambio.close();
    }

    private String mensajeDeLaUrl(HttpExchange intercambio) {
        String consulta = intercambio.getRequestURI().getRawQuery();
        return consulta == null ? null : parametros(consulta).get("m");
    }

    private Map<String, String> leerFormulario(HttpExchange intercambio) throws IOException {
        String cuerpo = new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return parametros(cuerpo);
    }

    /** Parseo de application/x-www-form-urlencoded y de query strings. */
    private Map<String, String> parametros(String texto) {
        Map<String, String> campos = new HashMap<>();
        if (texto == null || texto.isBlank()) {
            return campos;
        }
        for (String par : texto.split("&")) {
            int igual = par.indexOf('=');
            if (igual < 0) {
                continue;
            }
            String clave = URLDecoder.decode(par.substring(0, igual), StandardCharsets.UTF_8);
            String valor = URLDecoder.decode(par.substring(igual + 1), StandardCharsets.UTF_8);
            campos.put(clave, valor);
        }
        return campos;
    }

    private int enteroDe(String texto) {
        return texto == null || texto.isBlank() ? 0 : Integer.parseInt(texto.trim());
    }

    private void responder(HttpExchange intercambio, int estado, String tipo, String cuerpo)
            throws IOException {
        byte[] datos = cuerpo.getBytes(StandardCharsets.UTF_8);
        intercambio.getResponseHeaders().set("Content-Type", tipo);
        intercambio.sendResponseHeaders(estado, datos.length);
        try (var salida = intercambio.getResponseBody()) {
            salida.write(datos);
        }
    }
}
