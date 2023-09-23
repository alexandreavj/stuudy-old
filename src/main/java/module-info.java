module com.stuudy.stuudy {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires javafx.media;

    opens com.stuudy.stuudy to javafx.fxml;
    exports com.stuudy.stuudy;
}