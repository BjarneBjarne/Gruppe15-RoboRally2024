module gruppe15.roborally {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;

    opens gruppe15.roborally to javafx.fxml;
    exports gruppe15.roborally;
}
