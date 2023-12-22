module com.example.airplaneshoot {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.media;

    opens com.example.airplaneshoot to javafx.fxml;
    exports com.example.airplaneshoot;
}