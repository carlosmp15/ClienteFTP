module es.cmp.clienteftp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.apache.commons.net;
    requires jasperreports;
    requires org.testng;
    requires mysql.connector.j;

    opens es.cmp.clienteftp to javafx.fxml;
    exports es.cmp.clienteftp;
}