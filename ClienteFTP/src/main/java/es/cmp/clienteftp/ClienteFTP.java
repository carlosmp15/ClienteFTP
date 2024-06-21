package es.cmp.clienteftp;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.time.LocalDateTime;

import static es.cmp.clienteftp.HelloController.showAlert;

/**
 * Clase ClienteFTP.java
 * @author Carlos Murillo
 * @version 0.1
 */
public class ClienteFTP {

    private final FTPClient clienteFTP;

    public ClienteFTP() {
        clienteFTP = new FTPClient();
    }

    /**
     * Método que realiza la conexión con el servidor FTP.
     *
     * @param host Dirección IP del servidor.
     * @param usuario Usuario con el que nos autenticaremos en el login.
     * @param constras Contraseña con la que nos autenticaremos junto al usuario.
     * @param textArea Salida de los mensajes.
     */
    public boolean connect(String host, String usuario, String constras, TextArea textArea) {
        Conexion c = new Conexion("root", "", "ejemplo");
        boolean resultado = false;
        try {
            clienteFTP.connect(host);
            int codResp = clienteFTP.getReplyCode();
            if (!FTPReply.isPositiveCompletion(codResp)) {
                c.insertReg("ERROR", "Conexión rechazada con código de respuesta", LocalDateTime.now());

                textArea.appendText("ERROR: Conexión rechazada con código de respuesta " + codResp + "\n");
                System.exit(2);
            }

            clienteFTP.enterLocalPassiveMode();
            clienteFTP.setFileType(FTP.BINARY_FILE_TYPE);

            if (usuario != null && constras != null) {
                boolean loginOK = clienteFTP.login(usuario, constras);
                if (loginOK) {
                    c.insertReg("INFO", "Login con usuario " + usuario + " realizado", LocalDateTime.now());
                    c.insertReg("INFO", "Conexión establecida", LocalDateTime.now());
                    c.insertReg("INFO", "Directorio actual en servidor: " + clienteFTP.printWorkingDirectory(), LocalDateTime.now());

                    textArea.appendText("INFO: Login con usuario " + usuario + " realizado.\n");
                    textArea.appendText("INFO: Conexión establecida.\n");
                    textArea.appendText("INFO: Directorio actual en servidor: " + clienteFTP.printWorkingDirectory() + "\n");
                    resultado = true;
                }
            }
        } catch (IOException e) {
            c.insertReg("ERROR", "Conectando al servidor", LocalDateTime.now());
            textArea.appendText("ERROR: conectando al servidor");
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Método que comprueba si la conexion con el servidor se encuentra activa o no.
     * @return si la conexion con el servidor es true o false.
     */
    public boolean isConnected() {
        return clienteFTP.isConnected();
    }

    /**
     * Método que obtiene la propiedad clienteFTP de la clase.
     * @return clienteFTP.
     */
    public FTPClient getFTPClient() {
        return clienteFTP;
    }

    /**
     * Método para obtener ficheros del servidor FTP.
     *
     * @param fichServidor Fichero a obtener del servidor.
     * @param usuario Usuario con el que nos autenticaremos en el login.
     * @param constras Contraseña con la que nos autenticaremos junto al usuario.
     * @param host Dirección IP del servidor.
     * @param rutaLocal Ruta del cliente donde se guardará el archivo del servidor.
     * @param textArea Salida de los mensajes.
     */
    public void get(String host, String fichServidor, String usuario, String constras, String rutaLocal, TextArea textArea) {
        Conexion c = new Conexion("root", "", "ejemplo");
        try {
            connect(host, usuario, constras, textArea);

            if (!clienteFTP.isConnected()) {
                c.insertReg("ERROR", "No se pudo conectar al servidor FTP", LocalDateTime.now());

                textArea.appendText("ERROR: No se pudo conectar al servidor FTP.");
                return;
            }

            String tamFichEnServidor = clienteFTP.getSize(fichServidor);
            if (tamFichEnServidor == null) {
                c.insertReg("ERROR", "Fichero " + fichServidor + " no existe en servidor", LocalDateTime.now());

                textArea.appendText("ERROR: Fichero " + fichServidor + " no existe en servidor.\n");
                return;
            }

            String nomFichLocal = rutaLocal + File.separator + fichServidor.substring(fichServidor.lastIndexOf('/') + 1);
            try (FileOutputStream fos = new FileOutputStream(nomFichLocal)) {
                clienteFTP.retrieveFile(fichServidor, fos);
            }
            c.insertReg("INFO", "Se ha intentado copiar fichero " + fichServidor + " a fichero local " + nomFichLocal, LocalDateTime.now());
            c.insertReg("INFO", "Respuesta del servidor: " + clienteFTP.getReplyString(), LocalDateTime.now());

            textArea.appendText("INFO: Se ha intentado copiar fichero " + fichServidor + " a fichero local " + nomFichLocal + ".\n");
            textArea.appendText("INFO: Respuesta del servidor:\n====\n" + clienteFTP.getReplyString() + " ====\n");

            int codResp = clienteFTP.getReplyCode();
            if (FTPReply.isPositiveCompletion(codResp)) {
                c.insertReg("INFO", "Servidor informa de que se ha completado satisfactoriamente la acción", LocalDateTime.now());

                textArea.appendText("INFO: Servidor informa de que se ha completado satisfactoriamente la acción.");
                showAlert("INFORMATION_MESSAGE", "Transferencia OK.", "Se ha completado de forma exitosa la transferencia del archivo.", Alert.AlertType.INFORMATION);
            } else {
                c.insertReg("ERROR", "Servidor informa de que NO se ha completado satisfactoriamente la acción", LocalDateTime.now());

                textArea.appendText("ERROR: Servidor informa de que NO se ha completado satisfactoriamente la acción.");
            }
        } catch (IOException e) {
            c.insertReg("ERROR", "Conectando al servidor", LocalDateTime.now());

            textArea.appendText("ERROR: Conectando al servidor");
            e.printStackTrace();
        }
    }

    /**
     * Método para enviar ficheros al servidor FTP.
     *
     * @param host Dirección IP del servidor.
     * @param fichLocal Fichero a enviar al servidor.
     * @param usuario Usuario con el que nos autenticaremos en el login.
     * @param contras Contraseña con la que nos autenticaremos junto al usuario.
     * @param rutaRemota Ruta a guardar el archivo local en el servidor remoto ftp.
     * @param textArea Salida de los mensajes.
     */
    public void post(String host, String fichLocal, String usuario, String contras, String rutaRemota, TextArea textArea) {
        File fLocal = new File(fichLocal);
        Conexion c = new Conexion("root", "", "ejemplo");

        if (!fLocal.exists() || !fLocal.isFile()) {
            c.insertReg("ERROR", "Fichero " + fichLocal + " no existe", LocalDateTime.now());

            textArea.appendText("ERROR: Fichero " + fichLocal + " no existe.\n");
            return;
        }
        try {
            connect(host, usuario, contras, textArea);

            String nomFichRemoto = rutaRemota + "/" + fLocal.getName();
            clienteFTP.storeFile(nomFichRemoto, new FileInputStream(fichLocal));

            c.insertReg("INFO", "Se ha intentado copiar fichero local al servidor, con nombre " + fichLocal, LocalDateTime.now());
            c.insertReg("INFO", "Respuesta del servidor: " + clienteFTP.getReplyString(), LocalDateTime.now());

            textArea.appendText("INFO: Se ha intentado copiar fichero local al servidor, con nombre " + fichLocal + ".\n");
            textArea.appendText("INFO: Respuesta del servidor:\n====\n " + clienteFTP.getReplyString() + " ====\n");

            int codResp = clienteFTP.getReplyCode();
            if (FTPReply.isPositiveCompletion(codResp)) {
                c.insertReg("INFO", "Servidor informa de que se ha completado satisfactoriamente la acción", LocalDateTime.now());

                textArea.appendText("INFO: Servidor informa de que se ha completado satisfactoriamente la acción.");
                showAlert("INFORMATION_MESSAGE", "Transferencia OK.", "Se ha completado de forma exitosa la subida del archivo al servidor.", Alert.AlertType.INFORMATION);
            } else {
                c.insertReg("ERROR", "Servidor informa de que NO se ha completado satisfactoriamente la acción", LocalDateTime.now());

                textArea.appendText("ERROR: Servidor informa de que NO se ha completado satisfactoriamente la acción.");
            }

        } catch (IOException e) {
            c.insertReg("ERROR", "Conectando al servidor", LocalDateTime.now());

            textArea.appendText("ERROR: Conectando al servidor");
            e.printStackTrace();
        }
    }

    /**
     * Método que cierra la conexión con el servidor ftp.
     * @param textArea Salida de los mensajes.
     */
    public void closeConnection(TextArea textArea){
        Conexion c = new Conexion("root", "", "ejemplo");
        try {
            clienteFTP.disconnect();
            c.insertReg("INFO", "Conexion cerrada", LocalDateTime.now());

            textArea.appendText("INFO: conexión cerrada.");
            showAlert("INFORMATION_MESSAGE", "Conexión cerrada.", "Se ha cerrado la conexión al servidor ftp de forma existosa.", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            c.insertReg("ERROR", "No se pudo cerrar la conexión", LocalDateTime.now());

            textArea.appendText("ERROR: No se pudo cerrar la conexión.");
        }
    }


}
