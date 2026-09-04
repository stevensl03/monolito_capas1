package org.example.inventario.monolito.controlador_servicio;

import org.example.inventario.monolito.dominio_negocio.InventarioNegocio;
import org.example.inventario.monolito.persistencia.EsquemaBD;
import org.example.inventario.monolito.persistencia.FabricaConexion;
import org.example.inventario.monolito.persistencia.ProductoDao;
import org.example.inventario.monolito.persistencia.ProductoDaoH2;
import org.example.inventario.monolito.persistencia.ProductoDaoMysql;

/**
 * Ensamblaje INTERNO del monolito.

 */
public final class EnsambladorCapas {

    private static InventarioServicio servicio;

    private EnsambladorCapas() {
    }

    public static synchronized InventarioServicio montar() {
        if (servicio == null) {
            FabricaConexion fabrica = FabricaConexion.obtener();
            EsquemaBD.preparar(fabrica.obtenerConexion());

            ProductoDao dao = "MySQL".equals(fabrica.motorEfectivo())
                    ? new ProductoDaoMysql()
                    : new ProductoDaoH2();

            servicio = new InventarioServicio(dao, new InventarioNegocio());
        }
        return servicio;
    }
}
