/*package es.cmp.clienteftp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


class ConexionTest {

    private Conexion conexion;

    @BeforeEach
    void setUp() {
        conexion = new Conexion("root", "", "ejemplo");
    }

    @AfterEach
    void tearDown() {
        conexion.delReg();
    }

    @Test
    void testInsertReg() {
        LocalDateTime fechaHora = LocalDateTime.now();
        conexion.insertReg("INFO", "Prueba de inserción de registro", fechaHora);
    }

    @Test
    void testDelReg() {
        LocalDateTime fechaHora = LocalDateTime.now();
        conexion.insertReg("INFO", "Prueba de inserción de registro", fechaHora);

        conexion.delReg();

    }
}*/
