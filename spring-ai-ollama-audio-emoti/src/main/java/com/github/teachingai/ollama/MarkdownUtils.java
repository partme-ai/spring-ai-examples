package com.github.partmeai.ollama;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

/**
 * @author hzz
 * @createtime 2024/01/31
 * @function 去除markdown格式，将markdown转成纯文本
 */
public class MarkdownUtils {
    public static String removeMarkdownTags(String markdownText) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        // HtmlRenderer renderer = HtmlRenderer.builder().build(); //可以markdown转html
        TextContentRenderer renderer = TextContentRenderer.builder().build();
        return renderer.render(document);
    }

    public static String convertChinesePunctuationToEnglish(String text) {
        String[] chinesePunctuations = {"，", "。", "！", "？", "：", "；", "“", "”", "‘", "’", "（", "）", "【", "】", "—", "…"};
        String[] englishPunctuations = {",", ".", "!", "?", ":", ";", "\"", "\"", "'", "'", "(", ")", "[", "]", "-", "..."};

        for (int i = 0; i < chinesePunctuations.length; i++) {
            text = text.replace(chinesePunctuations[i], englishPunctuations[i]);
        }
        return text;
    }

}