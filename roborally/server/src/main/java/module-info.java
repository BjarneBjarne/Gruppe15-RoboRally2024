module com.group15.roborally.server {
    requires com.group15.roborally.common;
    requires jakarta.persistence;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.web;

    exports com.group15.roborally.server.controller to spring.beans, spring.web;

    opens com.group15.roborally.server to spring.core, spring.beans, spring.context;
}
