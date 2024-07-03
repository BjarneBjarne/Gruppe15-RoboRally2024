module com.group15.roborally.server {
    requires java.logging;
    requires org.slf4j;
    requires spring.data.jpa;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires static lombok;
    requires jakarta.persistence;
    requires com.fasterxml.jackson.annotation;
    requires spring.data.commons;
    // Add other required modules here

    exports com.group15.roborally.server;
    opens com.group15.roborally.server to javafx.base;
    exports com.group15.roborally.server.model; // Adjust based on actual usage
}