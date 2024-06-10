module gruppe15.roborally {
    requires javafx.controls;
    requires javafx.fxml;
    requires static org.jetbrains.annotations;
    requires com.google.gson;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.graphics;
    requires com.google.common;
    requires org.checkerframework.checker.qual;
    requires org.slf4j;
    requires jdk.unsupported.desktop;

    exports gruppe15.roborally;
    exports gruppe15.roborally.view;
    exports gruppe15.roborally.exceptions;
    exports gruppe15.roborally.model;
    exports gruppe15.roborally.model.upgrade_cards;

    opens gruppe15.roborally.model to com.google.gson;
    opens gruppe15.roborally.templates to com.google.gson;
    opens gruppe15.roborally.model.boardelements to com.google.gson;
    opens gruppe15.roborally.model.upgrade_cards to com.google.gson;

    opens gruppe15.roborally to javafx.fxml;
    opens gruppe15.roborally.view to javafx.fxml;

    exports gruppe15.roborally.controller to javafx.fxml;
    opens gruppe15.roborally.controller to javafx.fxml;
    exports gruppe15.roborally.coursecreator to javafx.fxml;
    opens gruppe15.roborally.coursecreator to javafx.fxml;
    exports gruppe15.roborally.communication;
    opens gruppe15.roborally.communication to javafx.fxml;
}
