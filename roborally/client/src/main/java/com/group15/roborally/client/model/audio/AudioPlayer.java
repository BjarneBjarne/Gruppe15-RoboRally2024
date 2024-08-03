package com.group15.roborally.client.model.audio;

import com.group15.roborally.client.utils.AudioUtils;
import javafx.beans.property.IntegerProperty;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayer {
    public enum PlayMode {
        OVERLAP,
        SINGLE
    }

    private final String fileName;
    private final IntegerProperty finalChannelVolume;
    private final ExecutorService executorService;
    private final PlayMode playMode;

    // Settings
    private int audioVolume = 100; // Default volume (100%)
    private Clip clip; // For SINGLE mode

    public AudioPlayer(String fileName, IntegerProperty finalChannelVolume, PlayMode playMode) {
        this.fileName = fileName;
        this.finalChannelVolume = finalChannelVolume;
        this.playMode = playMode;
        this.executorService = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });

        if (playMode == PlayMode.SINGLE) {
            initializeClip();
        }
    }

    private void initializeClip() {
        executorService.submit(() -> {
            clip = AudioUtils.getAudioFromName(fileName);
            if (clip != null) {
                updateAudioSettings(clip);
            } else {
                clipError("Can't load audio with filename: " + fileName);
            }
        });
    }

    // Playback
    public void playAudio() {
        if (playMode == PlayMode.OVERLAP) {
            executorService.submit(() -> {
                Clip newClip = AudioUtils.getAudioFromName(fileName);
                if (newClip != null) {
                    synchronized (newClip) {
                        updateAudioSettings(newClip);
                        newClip.start();
                    }
                } else {
                    clipError("Can't play audio with filename: " + fileName);
                }
            });
        } else {
            executorService.submit(() -> {
                if (clip != null) {
                    synchronized (clip) {
                        clip.setMicrosecondPosition(0); // Start from the beginning
                        clip.start();
                    }
                } else {
                    clipError("Can't play audio with filename: " + fileName);
                }
            });
        }
    }

    public void pauseAudio() {
        if (playMode == PlayMode.SINGLE) {
            executorService.submit(() -> {
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                }
            });
        }
    }

    public void resumeAudio() {
        if (playMode == PlayMode.SINGLE) {
            executorService.submit(() -> {
                if (clip != null && !clip.isRunning()) {
                    clip.start();
                }
            });
        }
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
        if (playMode == PlayMode.SINGLE) {
            updateAudioSettings(clip);
        }
    }

    private void updateVolume(Clip clip) {
        if (clip != null) {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();

            System.out.println("Playing clip: " + fileName);
            System.out.println("finalChannelVolume.get(): " + finalChannelVolume.get());
            // Converting the volume from percentage to decibels
            float linearVolume = (audioVolume / 100.0f) * (finalChannelVolume.get() / 100.0f);
            float dBVolume;
            if (linearVolume == 0) {
                dBVolume = min;
            } else {
                dBVolume = (float) (Math.log10(linearVolume) * 20);
                dBVolume = Math.max(min, Math.min(max, dBVolume));
            }

            volumeControl.setValue(dBVolume);
        }
    }

    private void clipError(String msg) {
        System.err.println(msg + " Clip is null. Latest file name: \"" + fileName + "\".");
    }
}
