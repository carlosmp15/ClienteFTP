package es.cmp.clienteftp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * Clase HelloController.java
 * @author Carlos Murillo
 * @version 0.1
 */
public class HelloController implements Initializable{
    @FXML
    private TextField tfServidor;
    @FXML
    private TextField tfUsuario;
    @FXML
    private PasswordField pfContras;
    @FXML
    private TextField tfOrigen;
    @FXML
    private TextField tfDestino;
    @FXML
    private TextArea taLog;
    @FXML
    private TreeView<String> tvLocal;
    @FXML
    private TreeView<String> tvRemoto;
    @FXML
    private RadioButton rbGet;
    @FXML
    private RadioButton rbPost;

    private final ClienteFTP cliente = new ClienteFTP();
    private final Stack<String> remoteDirectoryStack = new Stack<>();
    private String currentRemoteDirectory = "/"; // Directorio raíz por defecto

    @FXML
    private void onConectarClick() {
        if (!tfServidor.getText().isEmpty() && !tfUsuario.getText().isEmpty() && !pfContras.getText().isEmpty()) {
            if(cliente.connect(tfServidor.getText(), tfUsuario.getText(), pfContras.getText(), taLog)){
                mostrarDirectorioLocal();
                mostrarDirectorioRemoto();
                showAlert("CONFIRMATION_MESSAGE", "Autenticacion existosa.",
                        "La autenticación introducida es correcta para el servidor ftp " + tfServidor.getText() + ".",
                        Alert.AlertType.INFORMATION);
            } else{
                showAlert("ERROR_MESSAGE", "Utenticación incorrecta.",
                        "La autenticación de usuario y contraseña introducida para el servidor "
                                + tfServidor.getText() + " no ha podido resolverse.",
                        Alert.AlertType.ERROR);
            }
        } else {
            showAlert("WARNING_MESSAGE", "Campos vacíos", "Por favor, rellene todos los campos.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onCleanTaClick() {
        taLog.clear();
        Conexion c = new Conexion("root", "", "ejemplo");
        c.delReg();
    }

    @FXML
    private void onCleanFieldsClick(){
        tfServidor.clear();
        tfUsuario.clear();
        pfContras.clear();
    }

    @FXML
    private void onOperacionClick(){
        if(!tfOrigen.getText().isEmpty() && !tfDestino.getText().isEmpty()){
            if(cliente.isConnected()){
                if(rbGet.isSelected()){
                    cliente.get(tfServidor.getText(), tfOrigen.getText(), tfUsuario.getText(), pfContras.getText(), tfDestino.getText(), taLog);
                } else if(rbPost.isSelected()){
                    cliente.post(tfServidor.getText(), tfOrigen.getText(), tfUsuario.getText(), pfContras.getText(), tfDestino.getText(),taLog);
                }
            } else{
                showAlert("ERROR_MESSAGE", "Conexión FTP nula", "No se pudo establecer conexión con el servidor FTP. Por favor, verifica tu configuración de conexión e intenta nuevamente.", Alert.AlertType.ERROR);
            }
        } else{
            showAlert("WARNING_MESSAGE", "Campos vacíos", "Por favor, rellene todos los campos.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onGetClick(){
        if(rbGet.isSelected()){
            rbPost.setSelected(false);
        }
    }

    @FXML
    private void onPostClick(){
        if(rbPost.isSelected()){
            rbGet.setSelected(false);
        }
    }

    @FXML
    private void onBorrarClick(){
        tfOrigen.clear();
        tfDestino.clear();
    }

    @FXML
    private void onHelpClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("help.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Help - Cliente FTP");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            // Establecer la ventana principal como propietario de la ventana emergente
            Stage primaryStage = (Stage) tfServidor.getScene().getWindow(); // Reemplaza 'tfServidor' con cualquier nodo de tu ventana principal
            stage.initOwner(primaryStage);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogoutClick(){
        if(cliente.isConnected()){
            cliente.closeConnection(taLog);
            tvLocal.setRoot(null);
            tvRemoto.setRoot(null);
        }
    }

    @FXML
    private void onGenInformeClick(){
        mostrarInforme("Invoice.jrxml", "127.0.0.1", "ejemplo", "root", "");
    }

    private void mostrarInforme(String reportFile, String host, String db, String user, String passwd ){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + db, user, passwd);
            JasperReport jr = JasperCompileManager.compileReport(reportFile);
            JasperPrint jp = JasperFillManager.fillReport(jr, null, connection);
            JasperViewer.viewReport(jp);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void mostrarDirectorioLocal() {
        File localRoot = new File(System.getProperty("user.dir"));
        TreeItem<String> rootItem = new TreeItem<>(localRoot.getName());
        populateTreeView(localRoot, rootItem);
        tvLocal.setRoot(rootItem);
    }

    private void populateTreeView(File file, TreeItem<String> parentItem) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File childFile : files) {
                    TreeItem<String> newItem = new TreeItem<>(childFile.getName());
                    parentItem.getChildren().add(newItem);
                    populateTreeView(childFile, newItem);
                }
            }
        }
    }

    private void mostrarDirectorioRemoto() {
        if (cliente != null && cliente.isConnected()) {
            cargarDirectorioRemoto(currentRemoteDirectory);
        } else {
            showAlert("Error", "No conectado", "No se ha establecido una conexión FTP válida.", Alert.AlertType.ERROR);
        }
    }

    private void cargarDirectorioRemoto(String path) {
        try {
            FTPFile[] files = cliente.getFTPClient().listFiles(path);
            TreeItem<String> rootItem = new TreeItem<>(path);
            for (FTPFile file : files) {
                TreeItem<String> newItem = new TreeItem<>(file.getName());
                rootItem.getChildren().add(newItem);
                if (file.isDirectory()) {
                    newItem.getChildren().add(new TreeItem<>());
                }
            }
            tvRemoto.setRoot(rootItem);
            currentRemoteDirectory = path;
            remoteDirectoryStack.push(path);
        } catch (IOException e) {
            showAlert("ERROR_MESSAGE", "Error al obtener archivos remotos", "No se pudieron obtener los archivos remotos.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Conexion c = new Conexion("root", "", "ejemplo");
        c.delReg();
    }
}
