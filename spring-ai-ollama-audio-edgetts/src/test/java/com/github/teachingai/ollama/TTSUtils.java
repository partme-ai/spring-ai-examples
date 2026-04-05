package com.github.partmeai.ollama;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
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
        // `edge-tts -l` 查看可用的声音，
        // 普通话-男：zh-CN-YunyangNeural
        // 普通话-女：zh-CN-XiaoxiaoNeural、zh-CN-XiaoyiNeural
        // 辽宁方言-女：zh-CN-liaoning-XiaobeiNeural
        // 陕西方言-女：zh-CN-shaanxi-XiaoniNeural
        TTSUtils.edgeTts("阿里云创立于2009年，是全球领先的云计算及人工智能科技公司，致力于以在线公共服务的方式，提供安全、可靠的计算和数据处理能力，让计算和人工智能成为普惠科技。",
                "zh-CN-YunyangNeural",
                "-20%",
                "+50%",
                "E://t1.mp3", new ExecuteResultHandler() {
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
        // 使用 --pitch 参数指定频率
        if (StringUtils.hasText(pitch)) {
            cmdLine.addArgument("--pitch=" + pitch);
        }
        // 使用 --rate 参数指定语速
        if (StringUtils.hasText(rate)) {
            cmdLine.addArgument("--rate=" + rate);
        }
        // 使用 --volume 参数指定音量
        if (StringUtils.hasText(volume)) {
            cmdLine.addArgument("--volume=" + volume);
        }
        // 使用 --voice 参数指定选择的语音
        cmdLine.addArgument("--voice");
        cmdLine.addArgument(voice);
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
