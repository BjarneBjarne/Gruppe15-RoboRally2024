module com.group15.roborally.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.slf4j;
    requires ch.qos.logback.classic;
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
}
