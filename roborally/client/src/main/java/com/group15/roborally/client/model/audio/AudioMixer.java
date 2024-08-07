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
        MUSIC,
        UI,
        SOUND_EFFECTS
    }

    // AudioPlayers
    private final AudioPlayer backgroundMusic;

    private final AudioPlayer uiClick;
    private final AudioPlayer uiHover;

    private final AudioPlayer laserShoot;
    private final AudioPlayer laserHit;
    private final AudioPlayer playerMove;
    private final AudioPlayer playerTurn;
    private final AudioPlayer playerShutDown;
    private final AudioPlayer playerBootUp;
    private final AudioPlayer playerWin;
    private final AudioPlayer shopOpen;

    private final AudioPlayer[] clockTicks = new AudioPlayer[10];
    private final AudioPlayer clockAlarm;

    // Initializing
    public AudioMixer() {
        // Channels
        Channel musicChannel = new Channel(ChannelType.MUSIC, masterVolume);
        Channel uiChannel = new Channel(ChannelType.UI, masterVolume);
        Channel soundEffectsChannel = new Channel(ChannelType.SOUND_EFFECTS, masterVolume);
        channels.put(musicChannel.getChannelType(), musicChannel);
        channels.put(uiChannel.getChannelType(), uiChannel);
        channels.put(soundEffectsChannel.getChannelType(), soundEffectsChannel);

        // Audio players
        // Music
        backgroundMusic = musicChannel.newAudioPlayer("Ode_To_Oldfield", AudioPlayer.PlayMode.SINGLE);
        // UI
        uiClick = uiChannel.newAudioPlayer("ui_click", AudioPlayer.PlayMode.OVERLAP);
        //uiClick.setAudioVolumePercent(85);
        uiHover = uiChannel.newAudioPlayer("ui_hover", AudioPlayer.PlayMode.OVERLAP);
        clockAlarm = uiChannel.newAudioPlayer("clock_alarm", AudioPlayer.PlayMode.OVERLAP);
        for (int i = 0; i < clockTicks.length; i++) {
            clockTicks[i] = uiChannel.newAudioPlayer("clock_tick_" + i, AudioPlayer.PlayMode.OVERLAP);
        }
        // Sounds effects
        laserShoot = soundEffectsChannel.newAudioPlayer("laser_shoot", AudioPlayer.PlayMode.OVERLAP);
        laserHit = soundEffectsChannel.newAudioPlayer("laser_hit", AudioPlayer.PlayMode.OVERLAP);
        playerMove = soundEffectsChannel.newAudioPlayer("player_move", AudioPlayer.PlayMode.OVERLAP);
        playerTurn = soundEffectsChannel.newAudioPlayer("player_turn", AudioPlayer.PlayMode.OVERLAP);
        playerShutDown = soundEffectsChannel.newAudioPlayer("player_shutDown", AudioPlayer.PlayMode.OVERLAP);
        playerBootUp = soundEffectsChannel.newAudioPlayer("player_bootUp", AudioPlayer.PlayMode.OVERLAP);
        playerWin = soundEffectsChannel.newAudioPlayer("player_win", AudioPlayer.PlayMode.OVERLAP);
        shopOpen = soundEffectsChannel.newAudioPlayer("shop_open", AudioPlayer.PlayMode.OVERLAP);
    }

    // AudioPlayer playback methods
    // Music
    public void playBackgroundMusic() {
        backgroundMusic.playAudio();
    }
    // UI
    public void playUIClick() {
        uiClick.playAudio();
    }
    public void playUIHover() {
        uiHover.playAudio();
    }
    public void playClockTick(int second) {
        clockTicks[second % 10].playAudio();
    }
    public void playClockAlarm() {
        clockAlarm.playAudio();
    }
    // Board
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
    public void playShopOpen() {
        shopOpen.playAudio();
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
        private final IntegerProperty channelVolume = new SimpleIntegerProperty(100); // Channel volume in percentage (0-100)
        private final IntegerProperty finalChannelVolume = new SimpleIntegerProperty(100); // Channel volume in percentage (0-100)

        public Channel(ChannelType channelType, IntegerProperty masterVolume) {
            this.channelType = channelType;
            masterVolume.addListener((observable, oldValue, newValue) -> updateFinalChannelVolume(newValue.intValue(), channelVolume.get()));
            this.channelVolume.addListener((observable, oldValue, newValue) -> updateFinalChannelVolume(masterVolume.get(), newValue.intValue()));
            updateFinalChannelVolume(masterVolume.get(), channelVolume.get());
        }

        private void updateFinalChannelVolume(int newMasterVolume, int newChannelVolume) {
            finalChannelVolume.set((int) ((newMasterVolume / 100.0f) * (newChannelVolume / 100.0f) * 100.0));
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

        public int getChannelVolume() {
            return this.channelVolume.get();
        }

        public AudioPlayer newAudioPlayer(String fileName, AudioPlayer.PlayMode playMode) {
            return new AudioPlayer(fileName, finalChannelVolume, playMode);
        }
    }
}
