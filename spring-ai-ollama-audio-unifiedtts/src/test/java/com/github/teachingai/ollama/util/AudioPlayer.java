package com.github.partmeai.ollama.util;


import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer {

    public static void playMP3(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            Player player = new Player(fis);
            player.play();
            System.out.println("Playing " + fileName);
        } catch (IOException | JavaLayerException e) {
            System.out.println("Error playing " + fileName + ": " + e.getMessage());
        }
    }

    public static void playMP3(InputStream stream) {
        try (InputStream fis = stream) {
            Player player = new Player(fis);
            player.play();
        } catch (IOException | JavaLayerException e) {
            System.out.println("Error ：" + e.getMessage());
        }
    }


    public static void playWav(InputStream wavStream) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioFormat format = clip.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            // Wrap the InputStream in a BufferedInputStream
            try (BufferedInputStream bufferedStream = new BufferedInputStream(wavStream);
                 AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedStream)){

                bufferedStream.mark(wavStream.available());

                clip = (Clip) AudioSystem.getLine(info);
                clip.open(ais);

                clip.start();
                Thread.sleep(clip.getMicrosecondLength()/1808);
                clip.drain();
                clip.stop();
            } finally {
                clip.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
