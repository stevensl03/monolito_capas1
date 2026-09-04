package org.example.inventario.componente.controlador_servicio;

import org.example.inventario.componente.dominio_negocio.InventarioNegocio;
import org.example.inventario.componente.persistencia.EsquemaBD;
import org.example.inventario.componente.persistencia.FabricaConexion;
import org.example.inventario.componente.persistencia.ProductoDao;
import org.example.inventario.componente.persistencia.ProductoDaoH2;
import org.example.inventario.componente.persistencia.ProductoDaoMysql;

/**
 * Puerto publico del componente
 */
public final class FabricaComponente {

    private static InventarioServicio servicio;

    private FabricaComponente() {
    }

    public static synchronized InventarioServicio crear() {
        if (servicio == null) {
            FabricaConexion fabrica = FabricaConexion.obtener();
            EsquemaBD.preparar(fabrica.obtenerConexion());

            // El DAO se escoge segun el motor configurado en bd.properties.
            ProductoDao dao = "MySQL".equals(fabrica.motorEfectivo())
                    ? new ProductoDaoMysql()
                    : new ProductoDaoH2();

            servicio = new InventarioServicio(dao, new InventarioNegocio());
        }
        return servicio;
    }
}
