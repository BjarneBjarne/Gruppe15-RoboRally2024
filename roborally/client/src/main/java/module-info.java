module com.group15.roborally.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires org.jetbrains.annotations;
    requires com.group15.roborally.server;
    requires com.google.gson;
    requires static lombok;
    requires com.google.common;
    requires javafx.swing;
    requires spring.core;
    requires spring.web;
    // Add other required modules here

    exports com.group15.roborally.client;
    opens com.group15.roborally.client to javafx.fxml;

    exports com.group15.roborally.client.model;
    opens com.group15.roborally.client.model to com.google.gson;
}
