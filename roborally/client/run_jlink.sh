#!/bin/bash

jlink \
    --strip-debug \
    --compress 2 \
    --module-path ${JAVA_HOME}/jmods:target/dependency:target \
    --add-modules com.group15.roborally.client,javafx.controls,javafx.fxml,jakarta.transaction,jakarta.interceptor \
    --add-exports=java.base/jdk.internal.loader=ALL-UNNAMED \
    --add-exports=java.base/jdk.internal.module=ALL-UNNAMED \
    --add-exports=java.base/sun.security.util=ALL-UNNAMED \
    --add-exports=spring.beans/org.springframework.beans.propertyeditors=com.group15.roborally.client \
    --output target/image
