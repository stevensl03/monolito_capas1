package org.example;

import org.example.controlador_servicio.ProductoControladorServicio;
import org.example.dominio_negocio.Producto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ProductoWebController {

    private final ProductoControladorServicio controlador = new ProductoControladorServicio();

    @GetMapping("/productos")
    @ResponseBody
    public String listarProductos() {
        List<Producto> productos = controlador.listarProductos();
        StringBuilder html = new StringBuilder();
        html.append("<h1>Inventario de productos</h1>");
        html.append("<form method='post' action='/productos'>")
                .append("Nombre: <input name='nombre'> ")
                .append("Precio: <input name='precio'> ")
                .append("Stock: <input name='stock'> ")
                .append("<button type='submit'>Registrar</button>")
                .append("</form>");
        html.append("<table border='1'><tr><th>Id</th><th>Nombre</th><th>Precio</th><th>Stock</th></tr>");
        for (Producto producto : productos) {
            html.append("<tr><td>").append(producto.getId()).append("</td>")
                    .append("<td>").append(producto.getNombre()).append("</td>")
                    .append("<td>").append(producto.getPrecio()).append("</td>")
                    .append("<td>").append(producto.getStock()).append("</td></tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    @PostMapping("/productos")
    @ResponseBody
    public String crearProducto(@RequestParam String nombre,
                                 @RequestParam double precio,
                                 @RequestParam int stock) {
        controlador.crearProducto(nombre, precio, stock);
        return listarProductos();
    }
}
