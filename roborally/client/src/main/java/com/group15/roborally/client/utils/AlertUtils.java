package com.group15.roborally.client.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.Optional;

public class AlertUtils {
    @Setter
    private static Stage primaryStage;

    public static Optional<ButtonType> showConfirmationAlert(String title, String content) {
        return showAlert(Alert.AlertType.CONFIRMATION, title, null, content);
    }

    public static Optional<ButtonType> showErrorAlert(String title, String header, String content) {
        return showAlert(Alert.AlertType.ERROR, title, header, content);
    }

    public static Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        if (header != null) {
            alert.setHeaderText(header);
        }
        alert.setContentText(content);
        alert.initOwner(primaryStage);
        alert.initModality(Modality.WINDOW_MODAL);

        return alert.showAndWait();
    }

    public static Optional<ButtonType> showDialog(String title, String header, ButtonType... buttonTypes) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypes);

        // Set the primary stage as the owner
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.WINDOW_MODAL);

        return dialog.showAndWait();
    }
}
