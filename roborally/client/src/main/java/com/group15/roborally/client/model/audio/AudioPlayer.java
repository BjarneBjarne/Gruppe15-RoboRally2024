package com.group15.roborally.client.model.audio;

import com.group15.roborally.client.utils.AudioUtils;
import javafx.beans.property.IntegerProperty;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioPlayer {
    private String latestFileName = "NO_FILE";
    private Clip clip = null;
    private int audioVolume = 100; // Default volume (100%)
    private final IntegerProperty finalChannelVolume;

    public AudioPlayer(String fileName, IntegerProperty finalChannelVolume) {
        this.finalChannelVolume = finalChannelVolume;
        this.finalChannelVolume.addListener((observable, oldValue, newValue) -> updateVolume());
        setAudio(fileName);
    }

    public void setAudio(String fileName) {
        if (clip != null) {
            clip.close();
            clip = null;
        }
        if (fileName != null && !fileName.isBlank()) {
            clip = AudioUtils.getAudioFromName(fileName);
            latestFileName = fileName;
            updateAudioSettings();
        } else {
            latestFileName = "NO_FILE";
        }
    }

    // Playback
    public void playAudio() {
        if (clip != null) {
            clip.start();
        } else {
            printNoClipError("Can't play audio.");
        }
    }
    public void pauseAudio() {
        if (clip != null) {
            clip.stop();
        } else {
            printNoClipError("Can't pause audio.");
        }
    }
    public void rewindAudio() {
        if (clip != null) {
            clip.setMicrosecondPosition(0);
        } else {
            printNoClipError("Can't rewind audio.");
        }
    }
    public void rewindAndPlayAudio() {
        if (clip != null) {
            clip.stop();
            clip.setMicrosecondPosition(0);
            clip.start();
        } else {
            printNoClipError("Can't rewind and play audio.");
        }
    }

    // Settings
    public void updateAudioSettings() {
        updateVolume();
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
        updateVolume();
    }

    private void updateVolume() {
        if (clip != null) {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float adjustedVolume = min + ((audioVolume / 100.0f) * (finalChannelVolume.get() / 100.0f) * (max - min));
            volumeControl.setValue(adjustedVolume);
        }
    }

    private void printNoClipError(String msg) {
        System.err.println(msg + " Clip is null. Latest file name: \"" + latestFileName + "\".");
    }
}
