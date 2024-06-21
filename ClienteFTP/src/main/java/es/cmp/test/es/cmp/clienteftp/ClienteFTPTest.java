/*package es.cmp.clienteftp;

import javafx.scene.control.TextArea;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteFTPTest {

    private ClienteFTP clienteFTP;
    private TextArea textArea;

    @BeforeEach
    void setUp() {
        clienteFTP = new ClienteFTP();
        textArea = new TextArea();
    }

    @AfterEach
    void tearDown() {
        clienteFTP = null;
        textArea = null;
    }

    @Test
    void testConnect() {
        assertTrue(clienteFTP.connect("127.0.0.1", "pepe", "1234", textArea));
    }

    @Test
    void testIsConnected() {
        clienteFTP.connect("127.0.0.1", "pepe", "1234", textArea);
        assertTrue(clienteFTP.isConnected());
    }

    @Test
    void testGet() {
        clienteFTP.connect("127.0.0.1", "pepe", "1234", textArea);
        clienteFTP.get("127.0.0.1", "/cruduser/pom.xml", "username", "password", "C:\\Users\\Carlos\\Desktop", textArea);
    }

    @Test
    void testPost() {
        clienteFTP.connect("example.com", "username", "password", textArea);
        clienteFTP.post("example.com", "C:\\Users\\Carlos\\Desktop\\ejemplo.txt", "username", "password", "/", textArea);
    }

    @Test
    void testCloseConnection() {
        clienteFTP.connect("127.0.0.1", "pepe", "1234", textArea);
        clienteFTP.closeConnection(textArea);
        assertFalse(clienteFTP.isConnected());
    }
}*/
