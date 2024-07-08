module com.group15.roborally.common {
    requires com.fasterxml.jackson.annotation;
    requires jakarta.persistence;
    requires java.sql;
    requires lombok;
    requires javafx.base;
    requires javafx.graphics;
    requires org.hibernate.orm.core;

    exports com.group15.roborally.common.model;
    exports com.group15.roborally.common.observer;

    opens com.group15.roborally.common.model to org.hibernate.orm.core, spring.core, com.google.gson;
}
