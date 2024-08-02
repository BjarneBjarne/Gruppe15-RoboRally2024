package com.group15.roborally.client.model.audio;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

public class AudioMixer {
    // Master volume
    @Getter
    private final IntegerProperty masterVolume = new SimpleIntegerProperty(100); // Master volume in percentage (0-100)

    // Channels
    @Getter
    private final Map<ChannelType, Channel> channels = new EnumMap<>(ChannelType.class);
    public enum ChannelType {
        UI,
        BACKGROUND,
        EFFECTS
    }

    // AudioPlayers
    private final AudioPlayer uiClick;
    private final AudioPlayer uiHover;

    private final AudioPlayer laserShoot;
    private final AudioPlayer laserHit;

    private final AudioPlayer playerMove;
    private final AudioPlayer playerTurn;
    private final AudioPlayer playerShutDown;
    private final AudioPlayer playerBootUp;
    private final AudioPlayer playerWin;

    private final AudioPlayer[] clockTicks = new AudioPlayer[10];
    private final AudioPlayer clockAlarm;

    // Initializing
    public AudioMixer() {
        // Channels
        Channel uiChannel = new Channel(ChannelType.UI, masterVolume);
        Channel boardChannel = new Channel(ChannelType.UI, masterVolume);
        channels.put(uiChannel.getChannelType(), uiChannel);
        channels.put(boardChannel.getChannelType(), boardChannel);

        // Audio players
        uiClick = uiChannel.newAudioPlayer("ui_click");
        //uiClick.setAudioVolumePercent(85);
        uiHover = uiChannel.newAudioPlayer("ui_hover");
        clockAlarm = uiChannel.newAudioPlayer("clock_alarm");
        for (int i = 0; i < clockTicks.length; i++) {
            clockTicks[i] = uiChannel.newAudioPlayer("clock_tick_" + i);
        }

        laserShoot = boardChannel.newAudioPlayer("laser_shoot");
        laserHit = boardChannel.newAudioPlayer("laser_hit");
        playerMove = boardChannel.newAudioPlayer("player_move");
        playerTurn = boardChannel.newAudioPlayer("player_turn");
        playerShutDown = boardChannel.newAudioPlayer("player_shutDown");
        playerBootUp = boardChannel.newAudioPlayer("player_bootUp");
        playerWin = boardChannel.newAudioPlayer("player_win");
    }

    // AudioPlayer playback methods
    public void playUIClick() {
        uiClick.playAudio();
    }
    public void playUIHover() {
        uiHover.playAudio();
    }

    public void playLaserShoot() {
        laserShoot.playAudio();
    }
    public void playLaserHit() {
        laserHit.playAudio();
    }

    public void playPlayerMove() {
        playerMove.playAudio();
    }
    public void playPlayerTurn() {
        playerTurn.playAudio();
    }
    public void playPlayerShutDown() {
        playerShutDown.playAudio();
    }
    public void playPlayerBootUp() {
        playerBootUp.playAudio();
    }
    public void playPlayerWin() {
        playerWin.playAudio();
    }

    public void playClockTick(int second) {
        clockTicks[second % 10].playAudio();
    }
    public void playClockAlarm() {
        clockAlarm.playAudio();
    }

    /**
     * Sets the master volume for all channels and their audio players.
     * @param masterVolume Ranges from 0 to 100, where 0 is silent and 100 is the maximum volume.
     */
    public void setMasterVolumePercent(int masterVolume) {
        if (masterVolume < 0 || masterVolume > 100) {
            System.err.println("Invalid master volume: \"" + masterVolume + "\". Master volume must be between 0 and 100");
            return;
        }

        this.masterVolume.set(masterVolume);
    }

    public Channel getChannel(ChannelType channelType) {
        return channels.get(channelType);
    }

    public static class Channel {
        @Getter
        private final ChannelType channelType;
        private final IntegerProperty masterVolume;
        private final IntegerProperty channelVolume = new SimpleIntegerProperty(100); // Channel volume in percentage (0-100)
        private final IntegerProperty finalChannelVolume = new SimpleIntegerProperty(100); // Channel volume in percentage (0-100)

        public Channel(ChannelType channelType, IntegerProperty masterVolume) {
            this.channelType = channelType;
            this.masterVolume = masterVolume;
            this.masterVolume.addListener((observable, oldValue, newValue) -> updateFinalChannelVolume());
            this.channelVolume.addListener((observable, oldValue, newValue) -> updateFinalChannelVolume());
        }

        private void updateFinalChannelVolume() {
            finalChannelVolume.set((int) ((masterVolume.get() / 100.0f) * (channelVolume.get() / 100.0f) * 100));
        }

        /**
         * Sets the volume of the channel.
         * @param channelVolume Ranges from 0 to 100, where 0 is silent and 100 is the default audio volume.
         */
        public void setChannelVolumePercent(int channelVolume) {
            if (channelVolume < 0 || channelVolume > 100) {
                System.err.println("Invalid channel volume: \"" + channelVolume + "\". Channel volume must be between 0 and 100");
                return;
            }
            this.channelVolume.set(channelVolume);
        }

        public AudioPlayer newAudioPlayer(String fileName) {
            return new AudioPlayer(fileName, finalChannelVolume);
        }
    }
}
