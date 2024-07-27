module com.group15.roborally.server {
    requires jakarta.persistence;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires lombok;
    requires com.group15.roborally.common;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.web;
    requires spring.core;
    requires spring.beans;
    requires spring.context;

    exports com.group15.roborally.server.controller to spring.beans, spring.web;
    exports com.group15.roborally.server.repository to spring.beans, spring.web;

    opens com.group15.roborally.server to spring.core, spring.beans, spring.context;
}
