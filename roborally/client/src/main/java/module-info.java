module com.group15.roborally.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires com.google.common;
    requires com.google.gson;
    requires org.jetbrains.annotations;
    requires lombok;
    requires com.group15.roborally.common;
    requires spring.core;
    requires spring.web;
    requires java.desktop;

    exports com.group15.roborally.client;
    exports com.group15.roborally.client.utils;
    exports com.group15.roborally.client.model.upgrade_cards;
    exports com.group15.roborally.client.model to com.google.gson;
    exports com.group15.roborally.client.coursecreator to javafx.fxml;

    opens com.group15.roborally.client to javafx.fxml;
    opens com.group15.roborally.client.view to javafx.fxml;
    opens com.group15.roborally.client.coursecreator to javafx.fxml;
    exports com.group15.roborally.client.model.audio;
}
