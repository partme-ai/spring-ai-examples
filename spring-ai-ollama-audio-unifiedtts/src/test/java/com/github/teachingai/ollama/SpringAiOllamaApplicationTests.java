package com.github.partmeai.ollama;

import com.github.partmeai.ollama.UnifiedTtsAudioSpeechModel;
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
