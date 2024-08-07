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
    requires spring.tx;

    opens com.group15.roborally.server to spring.core, spring.beans, spring.context;
    opens com.group15.roborally.server.controller to spring.core, spring.beans, spring.context, spring.web;

    exports com.group15.roborally.server;
    exports com.group15.roborally.server.controller;
    exports com.group15.roborally.server.repository;
}
