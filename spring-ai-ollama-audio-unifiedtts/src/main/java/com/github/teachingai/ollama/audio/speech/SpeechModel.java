package com.github.partmeai.ollama.audio.speech;

import com.github.partmeai.ollama.api.UnifiedTtsAudioApi;
import org.springframework.ai.model.Model;

/**
 * The {@link SpeechModel} interface provides a way to interact with the UnifiedTTS
 * Text-to-Speech (TTS) API. It allows you to convert text input into lifelike spoken
 * audio.
 */
@FunctionalInterface
public interface SpeechModel extends Model<SpeechPrompt, SpeechResponse> {

    /**
     * Generates spoken audio from the provided text message.
     * @param message the text message to be converted to audio
     * @return the resulting audio bytes
     */
    default UnifiedTtsAudioApi.SpeechResponse.AudioFile call(String message) {
        SpeechPrompt prompt = new SpeechPrompt(message);
        return call(prompt).getResult().getOutput();
    }

    /**
     * Sends a speech request to the OpenAI TTS API and returns the resulting speech
     * response.
     * @param request the speech prompt containing the input text and other parameters
     * @return the speech response containing the generated audio
     */
    SpeechResponse call(SpeechPrompt request);

}

