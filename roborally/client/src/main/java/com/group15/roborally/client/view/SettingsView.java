package com.group15.roborally.client.view;

import com.group15.roborally.client.ApplicationSettings;
import com.group15.roborally.client.RoboRally;
import com.group15.roborally.client.model.audio.AudioMixer;
import com.group15.roborally.client.utils.TextUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class SettingsView extends AnchorPane {
    private final Button backButton = new Button();
    private int index = 0;

    public SettingsView(Map<AudioMixer.ChannelType, AudioMixer.Channel> channels) {
        Font h1Font = TextUtils.loadFont("OCRAEXT.TTF", 96);
        Font h2Font = TextUtils.loadFont("OCRAEXT.TTF", 40);

        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        // Sound
        Label soundLabel = new Label("Sound");
        soundLabel.setTextAlignment(TextAlignment.CENTER);
        soundLabel.setAlignment(Pos.CENTER);
        soundLabel.setFont(h1Font);
        grid.add(soundLabel, 0, nextIndex());

        for (int i = 0; i < AudioMixer.ChannelType.values().length + 1; i++) {
            String sliderName;
            if (i == 0) {
                sliderName = "Master";
            } else {
                AudioMixer.Channel channel = channels.values().stream().toList().get(i - 1);
                sliderName = channel.getChannelType().name();
            }
            Label sliderLabel = new Label(sliderName);
            sliderLabel.setTextAlignment(TextAlignment.CENTER);
            sliderLabel.setAlignment(Pos.CENTER);
            sliderLabel.setFont(h2Font);
            Slider slider = initializeSlider();

            if (i == 0) {
                slider.valueProperty().addListener((obs, oldValue, newValue) -> {
                    int newVolume = newValue.intValue();
                    if (newVolume != oldValue.intValue()) {
                        RoboRally.audioMixer.setMasterVolumePercent(newVolume);
                        updateSliderBackground(slider, newVolume);
                    }
                });
                Platform.runLater(() -> slider.setValue(RoboRally.audioMixer.getMasterVolume().get()));
            } else {
                AudioMixer.Channel channel = channels.values().stream().toList().get(i - 1);
                slider.valueProperty().addListener((obs, oldValue, newValue) -> {
                    int newVolume = newValue.intValue();
                    if (newVolume != oldValue.intValue()) {
                        channel.setChannelVolumePercent(newVolume);
                        updateSliderBackground(slider, newVolume);
                    }
                });
                Platform.runLater(() -> slider.setValue(channel.getChannelVolume()));
            }

            grid.add(sliderLabel, 0, nextIndex());
            grid.add(slider, 0, nextIndex());
        }
        index++;

        // Video
        Label videoLabel = new Label("Video");
        videoLabel.setTextAlignment(TextAlignment.CENTER);
        videoLabel.setAlignment(Pos.CENTER);
        videoLabel.setFont(h1Font);
        grid.add(videoLabel, 0, nextIndex());

        Label fullscreenLabel = new Label("Fullscreen");
        fullscreenLabel.setTextAlignment(TextAlignment.CENTER);
        fullscreenLabel.setAlignment(Pos.CENTER);
        fullscreenLabel.setFont(h2Font);
        grid.add(fullscreenLabel, 0, nextIndex());

        CheckBox fullscreenCheckBox = new CheckBox();
        fullscreenCheckBox.setStyle("-fx-font-size: 35px;");
        fullscreenCheckBox.setSelected(ApplicationSettings.getFULLSCREEN().get());
        fullscreenCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
            ApplicationSettings.getFULLSCREEN().set(newValue);
        });
        grid.add(fullscreenCheckBox, 0, nextIndex());

        // Back button
        Label backButtonLabel = new Label("Back");
        backButtonLabel.setTextAlignment(TextAlignment.CENTER);
        backButtonLabel.setAlignment(Pos.CENTER);
        backButtonLabel.setFont(h1Font);

        backButton.setStyle(
                "-fx-background-color: #ffffff20;" +
                        "-fx-background-radius: 15px; " +
                        "-fx-border-radius: 15px; " +
                        "-fx-border-color: #ffffff; " +
                        "-fx-border-width: 1px; "
        );
        backButton.setGraphic(backButtonLabel);
        backButton.setPrefSize(276, 124);

        StackPane stackPane = new StackPane(grid);
        StackPane.setAlignment(grid, Pos.CENTER);
        this.getChildren().addAll(stackPane, backButton);
        AnchorPane.setTopAnchor(stackPane, 0.0);
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setRightAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
        AnchorPane.setLeftAnchor(backButton, 128.0);
        AnchorPane.setBottomAnchor(backButton, 60.0);
    }

    private int nextIndex() {
        int nextIndex = index;
        index++;
        return nextIndex;
    }

    private Slider initializeSlider() {
        Slider slider = new Slider(0, 100, 100);
        slider.setPrefWidth(500);
        String styles = RoboRally.getStyles();
        if (styles != null) {
            slider.getStylesheets().add(styles);
        }

        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);

        slider.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                StackPane thumb = (StackPane) slider.lookup(".thumb");
                if (thumb != null) {
                    thumb.setPadding(new Insets(10));
                }
                updateSliderBackground(slider, slider.getValue());
            }
        });

        return slider;
    }

    private void updateSliderBackground(Slider slider, double value) {
        double percentage = value / slider.getMax() * 100;
        Platform.runLater(() -> {
            StackPane track = (StackPane) slider.lookup(".track");
            if (track != null) {
                track.setStyle(String.format(
                        Locale.US,
                        "-fx-background-color: linear-gradient(to right, #235d23 %.2f%%, #540d0d %.2f%%);",
                        percentage,
                        percentage
                ));
            }
        });
    }

    public void setBackButton(Runnable backButtonMethod) {
        backButton.setOnAction(e -> backButtonMethod.run());
    }
}
