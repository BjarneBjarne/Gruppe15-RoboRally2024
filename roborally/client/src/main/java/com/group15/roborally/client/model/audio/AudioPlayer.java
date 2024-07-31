package com.group15.roborally.client.model.audio;

import com.group15.roborally.client.utils.AudioUtils;
import javafx.beans.property.IntegerProperty;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayer {
    private final String fileName;
    private final IntegerProperty finalChannelVolume;
    private final ExecutorService executorService;

    // Settings
    private int audioVolume = 100; // Default volume (100%)

    public AudioPlayer(String fileName, IntegerProperty finalChannelVolume) {
        this.finalChannelVolume = finalChannelVolume;
        this.fileName = fileName;
        executorService = Executors.newSingleThreadExecutor();
    }

    // Playback
    public void playAudio() {
        executorService.submit(() -> {
            Clip clip = AudioUtils.getAudioFromName(fileName);
            if (clip != null) {
                synchronized (clip) {
                    updateAudioSettings(clip);  // Apply the current volume settings to the new clip
                    clip.start();
                }
            } else {
                printNoClipError("Can't play audio with filename: " + fileName);
            }
        });
    }

    // Settings
    private void updateAudioSettings(Clip clip) {
        updateVolume(clip);
    }

    /**
     * Sets the volume of the audio clip.
     * @param volume Ranges from 0 to 100, where 0 is silent and 100 is the default audio volume.
     */
    public void setAudioVolumePercent(int volume) {
        if (volume < 0) {
            volume = 0;
        } else if (volume > 100) {
            volume = 100;
        }
        this.audioVolume = volume;
    }

    private void updateVolume(Clip clip) {
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        float adjustedVolume = min + ((audioVolume / 100.0f) * (finalChannelVolume.get() / 100.0f) * (max - min));
        volumeControl.setValue(adjustedVolume);
    }

    private void printNoClipError(String msg) {
        System.err.println(msg + " Clip is null. Latest file name: \"" + fileName + "\".");
    }
}
