package es.cmp.clienteftp;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Clase Conexion.java
 * @author Carlos Murillo
 * @version 0.1
 */
public class Conexion {
    private  String user;
    private  String password;
    private  String db;
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/";

    public Conexion(String user, String password, String db ){
        this.user = user;
        this.password = password;
        this.db = db;
    }

    public Conexion(){}

    /**
     * Método que establece conexión con la base de datos
     * @return Objeto Connection
     */
    private Connection conectar(){
        Connection conexion = null;
        String url = URL + db;
        try{
            Class.forName(DRIVER);

            conexion = DriverManager.getConnection(url, user, password);
        }
        catch (SQLException e) {
            System.out.println("Error loading connection...");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            System.out.println("Error loading driver...");
        }
        return conexion;
    }

    /**
     * Método que inserta un registro del log de la aplicación en la base de datos.
     * @param tipo Tipo de log.
     * @param mensaje Mensaje log.
     * @param fechaHora Fecha y hora de la inserción del registro
     */
    public void insertReg(String tipo, String mensaje, LocalDateTime fechaHora) {
        String sql = "INSERT INTO log (tipo, mensaje, fecha_hora) VALUES (?, ?, ?)";

        try (
                Connection conn = conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            pstmt.setString(1, tipo);
            pstmt.setString(2, mensaje);
            pstmt.setTimestamp(3, Timestamp.valueOf(fechaHora));

            pstmt.executeUpdate();

            System.out.println("Registro insertado correctamente en la tabla log.");
        } catch (SQLException e) {
            System.out.println("Error al insertar registro en la tabla log: " + e.getMessage());
        }
    }

    /**
     * Método que elimina todos los registros de la base de datos
     */
    public void delReg(){
        try {
            Connection conn = conectar();

            Statement sentencia = conn.createStatement();

            String sql = "DELETE FROM log";
            sentencia.executeUpdate(sql);

            sentencia.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
















