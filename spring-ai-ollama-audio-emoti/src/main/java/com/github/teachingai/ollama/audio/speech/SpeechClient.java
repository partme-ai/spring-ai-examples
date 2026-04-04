package com.github.teachingai.ollama.audio.speech;


import org.springframework.ai.model.Model;

/**
 * The {@link SpeechClient} interface provides a way to interact with the ChatTTS
 * Text-to-Speech (TTS) API. It allows you to convert text input into lifelike spoken
 * audio.
 *
 * @since 2026.06.28
 */
@FunctionalInterface
public interface SpeechClient extends Model<SpeechPrompt, SpeechResponse> {

    /**
     * Generates spoken audio from the provided text message.
     * @param message the text message to be converted to audio
     * @return the resulting audio bytes
     */
    default byte[] call(String message) {
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
