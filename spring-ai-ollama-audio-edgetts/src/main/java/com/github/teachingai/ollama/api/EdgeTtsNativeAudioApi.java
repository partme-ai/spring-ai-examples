package com.github.partmeai.ollama.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.UUID;

@Slf4j
public class EdgeTtsNativeAudioApi {

    protected static DefaultExecutor executor = new DefaultExecutor();

    /**
     * Request to generates audio from the input text.
     * @param text The input text to convert to speech.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SpeechRequest(
            @JsonProperty("text") String text,
            @JsonProperty("voice") String voice,
            @JsonProperty("rate") String rate,
            @JsonProperty("volume") String volume,
            @JsonProperty("pitch") String pitch,
            @JsonProperty("words_in_cue") Integer wordsInCue,
            @JsonProperty("write_subtitles") String writeSubtitles,
            @JsonProperty("proxy") String proxy,
            @JsonProperty("output") String output) {

        public SpeechRequest(String text) {
            this(text, null, null, null, null, null, null, null, null);
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SpeechResponse(
            @JsonProperty("code") Integer code,
            @JsonProperty("msg") String msg,
            @JsonProperty("filename") String filename) {

    }

    /**
     * Request to generates audio from the input text.
     * @param speechRequest The request body.
     * @return Response entity containing the audio SpeechResponse.
     */
    public SpeechResponse createSpeech(SpeechRequest speechRequest) {

        Assert.notNull(speechRequest, "The request body can not be null.");
        Assert.notNull(speechRequest.text(), "The request.text can not be null.");
        Assert.notNull(speechRequest.output(), "The request.output can not be null.");

        /*
        usage: edge-tts [-h] [-t TEXT] [-f FILE] [-v VOICE] [-l] [--rate RATE]
                [--volume VOLUME] [--pitch PITCH]
                [--words-in-cue WORDS_IN_CUE] [--write-media WRITE_MEDIA]
                [--write-subtitles WRITE_SUBTITLES] [--proxy PROXY]
         */
        CommandLine cmdLine = new CommandLine("edge-tts");
        // 使用 --rate 参数指定语速
        if (StringUtils.hasText(speechRequest.rate())) {
            cmdLine.addArgument("--rate=" + speechRequest.rate());
        }
        // 使用 --volume 参数指定音量
        if (StringUtils.hasText(speechRequest.volume())) {
            cmdLine.addArgument("--volume=" + speechRequest.volume());
        }
        // 使用 --pitch 参数指定频率
        if (StringUtils.hasText(speechRequest.pitch())) {
            cmdLine.addArgument("--pitch=" + speechRequest.pitch());
        }
        // 使用 --voice 参数指定选择的语音
        if (StringUtils.hasText(speechRequest.voice())) {
            cmdLine.addArgument("--voice").addArgument(speechRequest.voice());
        }
        // 使用 --text 参数指定需要转换的文本
        cmdLine.addArgument("--text").addArgument(speechRequest.text());
        // 使用 --write-media 参数指定输出文件
        cmdLine.addArgument("--write-media");
        File outputDir = new File(speechRequest.output());
        if(outputDir.exists() && !outputDir.isDirectory()){
            throw new IllegalArgumentException("The output directory is not a directory.");
        } else {
            outputDir.mkdirs();
        }
        String filename = speechRequest.output() + File.separator + UUID.randomUUID() + ".mp3";
        cmdLine.addArgument(filename);
        String cmd = cmdLine.getExecutable() + " " + String.join(" ", cmdLine.getArguments());
        System.out.println("Executing command: " + cmd);
        SpeechResponse speechResponse;
        try {
            executor.execute(cmdLine);
            speechResponse = new SpeechResponse(0, "Success", filename);
        } catch (Exception e) {
            speechResponse = new SpeechResponse(1, "Failed", null);
            log.error("Error occurred while executing the command: " + cmd, e);
        }
        return speechResponse;
    }

}
