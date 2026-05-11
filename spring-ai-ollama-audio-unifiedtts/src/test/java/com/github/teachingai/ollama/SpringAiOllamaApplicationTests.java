package io.github.partmeai.ollama;

import io.github.partmeai.ollama.UnifiedTtsAudioSpeechModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringAiOllamaApplicationTests {

    UnifiedTtsAudioSpeechModel unifiedTtsAudioSpeechModel;
	@Test
	void contextLoads() {

        unifiedTtsAudioSpeechModel.call("hello world");

	}

}
