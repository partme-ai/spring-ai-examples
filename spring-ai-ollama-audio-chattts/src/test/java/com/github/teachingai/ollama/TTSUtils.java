package com.github.partmeai.ollama;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class TTSUtils {

    protected static DefaultExecutor executor = new DefaultExecutor();
    protected static ExecuteResultHandler DEFAULT_RESULT_HANDLER = new ExecuteResultHandler() {
        @Override
        public void onProcessComplete(int exitValue) {
            System.out.println("音频转换成功!");
        }

        @Override
        public void onProcessFailed(ExecuteException e) {
            System.out.println("音频转换失败!");
        }
    };

    public static void main(String[] args) throws Exception {

        TTSUtils.edgeTts("你好，有什么可以帮助你的吗", "zh-CN-XiaoyiNeural", "+50%", "E://t1.mp3", new ExecuteResultHandler() {
            @Override
            public void onProcessComplete(int exitValue) {
                System.out.println("音频转换成功!");
                AudioPlayer.playMP3("E://t1.mp3");
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                System.out.println("音频转换失败!");
            }
        });

    }

    public static void edgeTts(String text, String voice, String output) throws IOException {
        edgeTts(text, voice, null, null, null, output, null);
    }

    public static void edgeTts(String text, String voice, String volume, String output) throws IOException {
        edgeTts(text, voice, null, volume, null, output, null);
    }

    public static void edgeTts(String text, String voice, String rate, String volume, String output) throws IOException {
        edgeTts(text, voice, rate, volume, null, output, null);
    }

    public static void edgeTts(String text, String voice, String output, ExecuteResultHandler resultHandler) throws IOException {
        edgeTts(text, voice, null, null, null, output, resultHandler);
    }

    public static void edgeTts(String text, String voice, String volume, String output, ExecuteResultHandler resultHandler) throws IOException {
        edgeTts(text, voice, null, volume, null, output, resultHandler);
    }

    public static void edgeTts(String text, String voice, String rate, String volume, String output, ExecuteResultHandler resultHandler) throws IOException {
        edgeTts(text, voice, rate, volume, null, output, resultHandler);
    }

    /**
     * 简单的调用
     * @param text 需要转换的文本
     * @param voice
     * @param rate 语速 , eg: 50%
     * @param volume 语速 , eg: 50%
     * @param pitch 频率 , eg: -50Hz
     * @param resultHandler 回调
     * @param output 输出文件，一般是MP3文件
     */
    public static void edgeTts(String text, String voice, String rate, String volume, String pitch, String output, ExecuteResultHandler resultHandler) throws IOException {
        /*
        usage: edge-tts [-h] [-t TEXT] [-f FILE] [-v VOICE] [-l] [--rate RATE]
                [--volume VOLUME] [--pitch PITCH]
                [--words-in-cue WORDS_IN_CUE] [--write-media WRITE_MEDIA]
                [--write-subtitles WRITE_SUBTITLES] [--proxy PROXY]
         */
        CommandLine cmdLine = new CommandLine("edge-tts");
        // 使用 --voice 参数指定选择的语音
        cmdLine.addArgument("--voice");
        cmdLine.addArgument(voice);
        // 使用 --rate 参数指定语速
        if (StrUtil.isNotBlank(rate)) {
            cmdLine.addArgument("--rate");
            cmdLine.addArgument(rate);
        }
        // 使用 --volume 参数指定音量
        if (StrUtil.isNotBlank(volume)) {
            cmdLine.addArgument("--volume");
            cmdLine.addArgument(String.format("\"%s\"", volume));
        }
        // 使用 --pitch 参数指定频率
        if (StrUtil.isNotBlank(pitch)) {
            cmdLine.addArgument("--pitch");
            cmdLine.addArgument(volume);
        }
        // 使用 --text 参数指定需要转换的文本
        cmdLine.addArgument("--text");
        cmdLine.addArgument(text);
        // 使用 --write-media 参数指定输出文件
        cmdLine.addArgument("--write-media");
        cmdLine.addArgument(output);
        System.out.println(cmdLine.getExecutable() + " " + String.join(" ", cmdLine.getArguments()));
        // 执行命令
        if(Objects.nonNull(resultHandler)){
            executor.execute(cmdLine, resultHandler);
        } else {
            executor.execute(cmdLine, DEFAULT_RESULT_HANDLER);
        }

    }
}
