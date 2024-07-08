module com.group15.roborally.common {
    requires com.fasterxml.jackson.annotation;
    requires jakarta.persistence;
    requires java.sql;
    requires lombok;
    requires javafx.base;
    requires javafx.graphics;

    exports com.group15.roborally.common.model;
    exports com.group15.roborally.common.observer;
}
