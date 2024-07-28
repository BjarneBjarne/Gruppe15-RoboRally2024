package com.group15.roborally.client.utils;

import com.group15.roborally.client.RoboRally;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AudioUtils {

    public static Clip getAudioFromName(String fileName) {
        String audioFilePath = "/com/group15/roborally/client/audio/" + fileName + ".wav";
        try {
            URL audioUrl = RoboRally.class.getResource(audioFilePath);
            if (audioUrl == null) {
                System.out.println("Error: Audio file not found with path: " + audioFilePath);
                return null;
            }
            AudioInputStream originalStream = AudioSystem.getAudioInputStream(audioUrl);

            AudioFormat baseFormat = originalStream.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false // Little endian
            );

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(targetFormat, originalStream);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error loading audio with path: " + audioFilePath);
            e.printStackTrace();
            return null;
        }
    }
}
