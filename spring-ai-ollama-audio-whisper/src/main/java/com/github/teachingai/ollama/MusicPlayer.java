package com.github.partmeai.ollama;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {

    public static void main(String[] args) {
        playMusic("path_to_your_music_file.wav");
    }

    public static void playMusic(String musicFilePath) {
        try (Clip clip = AudioSystem.getClip()) {
            try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(musicFilePath))){
                clip.open(inputStream);
                clip.start();
                // 等待音乐播放完成
                while (clip.isRunning()) {
                    Thread.sleep(100);
                }

                // 释放资源
                clip.close();
            }
        } catch (IOException | InterruptedException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
}
