# Inventario de Productos 

Cuatro proyectos Maven que muestran la diferencia entre un **monolito con capas lógicas** y un **componente reutilizable**.


## Proyectos

| # | Carpeta | Descripción | Artefacto |
|---|---------|-------------|-----------|
| 1 | `monolito-consola/` | Monolito completo con vista por consola | `.jar` ejecutable |
| 2 | `componente-capas/` | Capas de negocio sin vista (librería) | `.jar` librería |
| 3 | `app-web/`          | Proyecto 2 + vista Web MVC             | `.jar` ejecutable |
| 4 | `app-escritorio/`   | Proyecto 2 + vista Swing               | `.jar` ejecutable |

El proyecto 1 duplica deliberadamente las capas del proyecto 2: esa es la lección central. En el monolito las capas son separación lógica (paquetes); en el componente son un contrato publicado (`FabricaComponente.crear()`) que los proyectos 3 y 4 reutilizan sin copiar código.

---

## Construir

Requiere **JDK 21** y **Maven 3.9+**. El componente debe instalarse primero en `~/.m2`.

```bash
mvn -f componente-capas/pom.xml  clean install
mvn -f monolito-consola/pom.xml  clean package
mvn -f app-web/pom.xml           clean package
mvn -f app-escritorio/pom.xml    clean package
```


---

## Ejecutar

Los tres ejecutables son *fat-jars*; no requieren instalación adicional.

```bash
java -jar dist/monolito-consola-1.0.0.jar      
java -jar dist/app-web-1.0.0.jar               # http://localhost:8080
java -jar dist/app-escritorio-1.0.0.jar        
```

> **Importante:** ejecutar desde la raíz del repositorio. La base de datos usa la ruta relativa `./datos/inventario`.



## Base de datos

Configuración en `src/main/resources/bd.properties` (una copia en el proyecto 1 y otra en el proyecto 2).

### H2 (valor por omisión)

Base de datos embebida en archivo. `AUTO_SERVER=TRUE` permite que las tres aplicaciones abran la misma base simultáneamente desde JVMs distintas.

### MySQL (simulado o real)

```bash
java -Dinventario.motor=mysql -jar dist/monolito-consola-1.0.0.jar
```

`FabricaConexion` carga el driver MySQL real e intenta conectar a `jdbc:mysql://localhost:3306/inventariodb`. Si no hay servidor disponible, atiende la conexión con H2 en `MODE=MySQL`, que acepta el mismo dialecto SQL.

---

## Capas y clases

### Dominio / Negocio

| Clase | Responsabilidad |
|-------|----------------|
| `Producto` | Entidad: `valorEnInventario()`, `estaBajoStock()` |
| `InventarioNegocio` | `validar`, `aplicarEntrada`, `aplicarSalida`, `valorTotal`, `filtrarBajoStock` |
| `NegocioException` | `ProductoInvalido`, `ProductoNoEncontrado`, `StockInsuficiente` |

### Persistencia (JDBC)

| Clase | Responsabilidad |
|-------|----------------|
| `ProductoDao` | Contrato DAO |
| `ProductoDaoJdbc` | Mecánica JDBC común |
| `ProductoDaoH2` / `ProductoDaoMysql` | SQL por dialecto |
| `FabricaConexion` | Abre la conexión; gestiona la simulación de MySQL |
| `EsquemaBD` | Crea la tabla y siembra datos iniciales (idempotente) |

### Controlador / Servicio

| Clase | Responsabilidad |
|-------|----------------|
| `InventarioServicio` | Fachada: `registrarProducto`, `registrarEntrada`, `registrarSalida`, `eliminar`, `valorTotalInventario`, `productosBajoStock` |
| `FabricaComponente` (proyecto 2) | Puerto público del componente |
| `EnsambladorCapas` (proyecto 1) | Cableado interno del monolito |

### Vistas

| Proyecto | Tecnología |
|----------|-----------|
| 1 — `MainConsola` | `System.out` + `Scanner` |
| 3 — `ServidorWeb`, `ProductoControladorWeb`, `PlantillaHtml` | `com.sun.net.httpserver` |
| 4 — `AppEscritorio`, `VentanaInventario`, `ProductoTableModel` | Swing |

La vista web implementa MVC y POST-Redirect-GET. Expone además `GET /api/productos` (JSON).

---

## Dependencias

| Artefacto | Versión | Uso |
|-----------|---------|-----|
| `com.h2database:h2` | 2.2.224 | Motor embebido y compatibilidad MySQL |
| `com.mysql:mysql-connector-j` | 8.3.0 | Driver JDBC de MySQL |

