package com.example.jsontool.service;

import com.example.jsontool.flyweight.JsonToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Підсвічує JSON за допомогою Flyweight-токенів.
 * Результат — безпечний HTML: увесь текст користувача екранується.
 */
@Service
public class SyntaxHighlighterService {

    private static final String STRING = "\"(?:\\\\.|[^\"\\\\])*\"";

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(" + STRING + ")(\\s*:)"                                   // 1, 2: ключ і двокрапка
                    + "|(" + STRING + ")"                               // 3: рядок
                    + "|(-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)" // 4: число
                    + "|(\\btrue\\b|\\bfalse\\b)"                       // 5: boolean
                    + "|(\\bnull\\b)"                                   // 6: null
                    + "|([{}\\[\\],])");                                // 7: розділювачі

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String highlightJson(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            return "";
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(rawJson);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

            return tokenizeAndColor(prettyJson);

        } catch (JsonProcessingException e) {
            return "<span style='color:red'>Invalid JSON: " + HtmlUtils.htmlEscape(e.getOriginalMessage()) + "</span>";
        }
    }

    private String tokenizeAndColor(String json) {
        StringBuilder htmlBuilder = new StringBuilder();
        Matcher matcher = TOKEN_PATTERN.matcher(json);

        int lastIndex = 0;

        while (matcher.find()) {
            htmlBuilder.append(HtmlUtils.htmlEscape(json.substring(lastIndex, matcher.start())));

            if (matcher.group(1) != null) {
                htmlBuilder.append(new JsonToken(matcher.group(1), "KEY").render())
                        .append(matcher.group(2));
            } else if (matcher.group(3) != null) {
                htmlBuilder.append(new JsonToken(matcher.group(3), "STRING").render());
            } else if (matcher.group(4) != null) {
                htmlBuilder.append(new JsonToken(matcher.group(4), "NUMBER").render());
            } else if (matcher.group(5) != null) {
                htmlBuilder.append(new JsonToken(matcher.group(5), "BOOLEAN").render());
            } else if (matcher.group(6) != null) {
                htmlBuilder.append(new JsonToken(matcher.group(6), "NULL").render());
            } else {
                htmlBuilder.append(matcher.group());
            }

            lastIndex = matcher.end();
        }
        htmlBuilder.append(HtmlUtils.htmlEscape(json.substring(lastIndex)));

        return htmlBuilder.toString();
    }
}
