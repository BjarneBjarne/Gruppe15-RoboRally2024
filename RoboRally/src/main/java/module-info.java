module gruppe15.roborally {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires com.google.gson;
    requires guava;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.graphics;

    exports gruppe15.roborally;
    exports gruppe15.roborally.view;
    exports gruppe15.roborally.controller;

    opens gruppe15.roborally to javafx.fxml;
    opens gruppe15.roborally.view to javafx.fxml;
}
