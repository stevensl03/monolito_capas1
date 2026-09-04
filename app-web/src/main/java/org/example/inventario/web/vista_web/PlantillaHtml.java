package org.example.inventario.web.vista_web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.example.inventario.componente.dominio_negocio.Producto;

final class PlantillaHtml {

    private static final String ESTILOS = """
            body{font-family:system-ui,Segoe UI,Arial,sans-serif;margin:0;background:#f4f5f7;color:#1c1e21}
            .contenedor{max-width:1000px;margin:0 auto;padding:24px}
            h1{font-size:20px;margin:0 0 4px}
            .sub{color:#5b6470;font-size:13px;margin:0 0 20px}
            .tarjeta{background:#fff;border:1px solid #dfe3e8;border-radius:8px;padding:16px;margin-bottom:16px}
            table{width:100%;border-collapse:collapse;font-size:14px}
            th,td{text-align:left;padding:8px 10px;border-bottom:1px solid #eceff2}
            th{background:#fafbfc;font-size:12px;text-transform:uppercase;color:#5b6470}
            td.num{text-align:right;font-variant-numeric:tabular-nums}
            .bajo{color:#b42318;font-weight:600}
            form{display:flex;flex-wrap:wrap;gap:8px;align-items:flex-end}
            label{display:flex;flex-direction:column;font-size:12px;color:#5b6470;gap:4px}
            input,select{padding:7px 9px;border:1px solid #cbd2d9;border-radius:5px;font-size:14px}
            button{padding:8px 16px;border:0;border-radius:5px;background:#1f6feb;color:#fff;font-size:14px;cursor:pointer}
            .total{font-size:15px;font-weight:600}
            .aviso{background:#fff4ed;border:1px solid #ffd6bd;color:#9c3d00;padding:10px 12px;border-radius:6px;font-size:13px}
            .pie{color:#5b6470;font-size:12px;margin-top:20px}
            """;

    private PlantillaHtml() {
    }

    static String pagina(List<Producto> productos, BigDecimal valorTotal,
                         List<Producto> bajoStock, String mensaje) {
        StringBuilder html = new StringBuilder(4096);
        html.append("<!doctype html><html lang=\"es\"><head><meta charset=\"utf-8\">")
                .append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">")
                .append("<title>Inventario - Vista Web</title><style>").append(ESTILOS)
                .append("</style></head><body><div class=\"contenedor\">");

        html.append("<h1>Inventario de Productos</h1>");

        if (mensaje != null && !mensaje.isBlank()) {
            html.append("<div class=\"tarjeta aviso\">").append(escapar(mensaje)).append("</div>");
        }

        html.append("<div class=\"tarjeta\">").append(tabla(productos))
                .append("<p class=\"total\">Valor total del inventario: ")
                .append(formatear(valorTotal)).append("</p>");
        if (!bajoStock.isEmpty()) {
            html.append("<p class=\"bajo\">Bajo stock: ");
            for (int i = 0; i < bajoStock.size(); i++) {
                html.append(i > 0 ? ", " : "").append(escapar(bajoStock.get(i).getCodigo()));
            }
            html.append("</p>");
        }
        html.append("</div>");

        html.append(formularioAlta()).append(formularioMovimiento());

        html.append("<p class=\"pie\">Datos en JSON: <a href=\"/api/productos\">/api/productos</a></p>");

        return html.append("</div></body></html>").toString();
    }

    private static String tabla(List<Producto> productos) {
        StringBuilder tabla = new StringBuilder();
        tabla.append("<table><thead><tr><th>Codigo</th><th>Nombre</th><th>Categoria</th>")
                .append("<th class=\"num\">Precio</th><th class=\"num\">Stock</th>")
                .append("<th class=\"num\">Minimo</th></tr></thead><tbody>");
        if (productos.isEmpty()) {
            tabla.append("<tr><td colspan=\"6\">Sin productos registrados.</td></tr>");
        }
        for (Producto producto : productos) {
            tabla.append("<tr").append(producto.estaBajoStock() ? " class=\"bajo\"" : "").append(">")
                    .append("<td>").append(escapar(producto.getCodigo())).append("</td>")
                    .append("<td>").append(escapar(producto.getNombre())).append("</td>")
                    .append("<td>").append(escapar(producto.getCategoria())).append("</td>")
                    .append("<td class=\"num\">").append(formatear(producto.getPrecio())).append("</td>")
                    .append("<td class=\"num\">").append(producto.getStock()).append("</td>")
                    .append("<td class=\"num\">").append(producto.getStockMinimo()).append("</td>")
                    .append("</tr>");
        }
        return tabla.append("</tbody></table>").toString();
    }

    private static String formularioAlta() {
        return """
                <div class="tarjeta">
                  <form method="post" action="/productos">
                    <label>Codigo<input name="codigo" required></label>
                    <label>Nombre<input name="nombre" required></label>
                    <label>Categoria<input name="categoria" value="GENERAL"></label>
                    <label>Precio<input name="precio" type="number" step="0.01" min="0.01" required></label>
                    <label>Stock<input name="stock" type="number" min="0" value="0" required></label>
                    <label>Stock minimo<input name="stockMinimo" type="number" min="0" value="0" required></label>
                    <button type="submit">Registrar producto</button>
                  </form>
                </div>
                """;
    }

    private static String formularioMovimiento() {
        return """
                <div class="tarjeta">
                  <form method="post" action="/productos/stock">
                    <label>Codigo<input name="codigo" required></label>
                    <label>Cantidad<input name="cantidad" type="number" min="1" value="1" required></label>
                    <label>Movimiento
                      <select name="movimiento">
                        <option value="entrada">Entrada</option>
                        <option value="salida">Salida</option>
                      </select>
                    </label>
                    <button type="submit">Aplicar movimiento</button>
                  </form>
                </div>
                """;
    }

    /** JSON armado a mano */
    static String json(List<Producto> productos) {
        StringBuilder salida = new StringBuilder("[");
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            salida.append(i > 0 ? "," : "")
                    .append("{\"id\":").append(p.getId())
                    .append(",\"codigo\":\"").append(escaparJson(p.getCodigo()))
                    .append("\",\"nombre\":\"").append(escaparJson(p.getNombre()))
                    .append("\",\"categoria\":\"").append(escaparJson(p.getCategoria()))
                    .append("\",\"precio\":").append(p.getPrecio())
                    .append(",\"stock\":").append(p.getStock())
                    .append(",\"stockMinimo\":").append(p.getStockMinimo())
                    .append(",\"bajoStock\":").append(p.estaBajoStock())
                    .append("}");
        }
        return salida.append("]").toString();
    }

    private static String formatear(BigDecimal valor) {
        return String.format(Locale.US, "%,.2f", valor);
    }

    /** Evita que un nombre de producto pueda inyectar HTML en la pagina. */
    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
