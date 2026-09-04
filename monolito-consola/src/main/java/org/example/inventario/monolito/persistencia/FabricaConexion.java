package org.example.inventario.monolito.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Punto unico de acceso a la base de datos.
 *
 * Aqui vive la "conexion simulada" del ejercicio:
 *
 *  - Con motor=h2 se abre una conexion REAL a H2 embebido en archivo.
 *  - Con motor=mysql se carga el driver REAL de MySQL (Class.forName), se
 *    imprime su version y se intenta abrir la conexion contra el servidor
 *    configurado. Si no hay servidor, no se rompe la aplicacion: se deja
 *    constancia del intento y se atiende la peticion con H2 en modo de
 *    compatibilidad MySQL. El ProductoDaoMysql sigue ejecutando su SQL de
 *    dialecto MySQL sin enterarse.
 *
 * La conexion se mantiene abierta durante toda la vida de la JVM (el volumen
 * del ejercicio no justifica un pool).
 */
public final class FabricaConexion {

    private static final String ARCHIVO_CONFIGURACION = "/bd.properties";
    private static final String DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";
    private static final String DRIVER_H2 = "org.h2.Driver";

    private static FabricaConexion instancia;

    private final Properties configuracion;
    private final String motorSolicitado;
    private String motorEfectivo;
    private boolean simulado;
    private Connection conexion;

    private FabricaConexion() {
        this.configuracion = cargarConfiguracion();
        // Se puede forzar el motor sin recompilar: java -Dinventario.motor=mysql -jar ...
        String motor = System.getProperty("inventario.motor",
                configuracion.getProperty("motor", "h2"));
        this.motorSolicitado = motor.trim().toLowerCase();
    }

    public static synchronized FabricaConexion obtener() {
        if (instancia == null) {
            instancia = new FabricaConexion();
        }
        return instancia;
    }

    /** Devuelve la conexion viva, abriendola la primera vez o si fue cerrada. */
    public synchronized Connection obtenerConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = abrir();
            }
            return conexion;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo obtener la conexion a la base de datos", e);
        }
    }

    /** Motor que realmente esta atendiendo: "H2" o "MySQL". */
    public String motorEfectivo() {
        obtenerConexion();
        return motorEfectivo;
    }

    /** true cuando se pidio MySQL y la conexion tuvo que simularse sobre H2. */
    public boolean esSimulado() {
        obtenerConexion();
        return simulado;
    }

    /** Etiqueta lista para mostrar en cualquier vista. */
    public String descripcionMotor() {
        return esSimulado()
                ? "MySQL (conexion SIMULADA sobre H2 MODE=MySQL)"
                : motorEfectivo() + " (conexion real)";
    }

    private Connection abrir() throws SQLException {
        if ("mysql".equals(motorSolicitado)) {
            Connection real = intentarMysqlReal();
            if (real != null) {
                motorEfectivo = "MySQL";
                simulado = false;
                registrar("Conectado al servidor MySQL real.");
                return real;
            }
            // Simulacion: mismo archivo H2, pero en modo de compatibilidad MySQL.
            motorEfectivo = "MySQL";
            simulado = true;
            registrar("[SIMULADO] Se atendera con H2 en MODE=MySQL. "
                    + "El ProductoDaoMysql ejecutara su SQL de dialecto MySQL sin cambios.");
            return abrirH2();
        }
        motorEfectivo = "H2";
        simulado = false;
        Connection h2 = abrirH2();
        registrar("Conectado a H2 embebido: " + configuracion.getProperty("h2.url"));
        return h2;
    }

    /**
     * Carga el driver de MySQL de verdad y trata de conectar. Devuelve null (sin
     * propagar la excepcion) cuando no hay servidor: ese es el caso que dispara
     * la simulacion.
     */
    private Connection intentarMysqlReal() {
        String url = configuracion.getProperty("mysql.url");
        try {
            Driver driver = (Driver) Class.forName(DRIVER_MYSQL)
                    .getDeclaredConstructor().newInstance();
            registrar("Driver MySQL cargado: " + DRIVER_MYSQL
                    + " v" + driver.getMajorVersion() + "." + driver.getMinorVersion());
        } catch (ReflectiveOperationException e) {
            registrar("El driver de MySQL no esta en el classpath: " + e);
            return null;
        }

        int timeout = Integer.parseInt(configuracion.getProperty("mysql.timeoutSegundos", "2"));
        DriverManager.setLoginTimeout(timeout);
        registrar("Intentando conectar a " + url + " (timeout " + timeout + "s)...");
        try {
            return DriverManager.getConnection(url,
                    configuracion.getProperty("mysql.usuario"),
                    configuracion.getProperty("mysql.clave"));
        } catch (SQLException e) {
            registrar("MySQL no disponible: " + e.getMessage());
            return null;
        }
    }

    private Connection abrirH2() throws SQLException {
        try {
            Class.forName(DRIVER_H2);
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("El driver de H2 no esta en el classpath", e);
        }
        return DriverManager.getConnection(
                configuracion.getProperty("h2.url"),
                configuracion.getProperty("h2.usuario"),
                configuracion.getProperty("h2.clave"));
    }

    private Properties cargarConfiguracion() {
        Properties propiedades = new Properties();
        try (InputStream entrada = FabricaConexion.class.getResourceAsStream(ARCHIVO_CONFIGURACION)) {
            if (entrada == null) {
                throw new PersistenciaException("No se encontro " + ARCHIVO_CONFIGURACION + " en el classpath");
            }
            propiedades.load(entrada);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer " + ARCHIVO_CONFIGURACION, e);
        }
        return propiedades;
    }

    private void registrar(String mensaje) {
        // Silenciado para no mostrar detalles internos de la base de datos al usuario
    }
}
