package com.example.jsontool.service;

import com.example.jsontool.flyweight.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SyntaxHighlighterService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String highlightJson(String rawJson) {
        try {
            Object jsonObject = objectMapper.readValue(rawJson, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

            return tokenizeAndColor(prettyJson);

        } catch (Exception e) {

            return "<span style='color:red'>Invalid JSON: " + e.getMessage() + "</span>";
        }
    }

    private String tokenizeAndColor(String json) {
        StringBuilder htmlBuilder = new StringBuilder();

        String regex = "(\"[^\"]*\"\\s*:)|(\"[^\"]*\")|(\\b-?(?:0|[1-9]\\d*)(?:\\.\\d+)?\\b)|(\\btrue\\b|\\bfalse\\b|\\bnull\\b)|([\\{\\}\\[\\],:])";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);

        int lastIndex = 0;

        while (matcher.find()) {

            String whitespace = json.substring(lastIndex, matcher.start());
            htmlBuilder.append(whitespace);

            String match = matcher.group();
            JsonToken token;

            if (matcher.group(1) != null) {
                String keyText = match.substring(0, match.lastIndexOf(':')).trim();
                token = new JsonToken(keyText, "KEY");
                htmlBuilder.append(token.render()).append(":");
            } else if (matcher.group(2) != null) {
                token = new JsonToken(match, "STRING");
                htmlBuilder.append(token.render());
            } else if (matcher.group(3) != null) {

                token = new JsonToken(match, "NUMBER");
                htmlBuilder.append(token.render());
            } else if (matcher.group(4) != null) {

                token = new JsonToken(match, "BOOLEAN");
                htmlBuilder.append(token.render());
            } else {
                htmlBuilder.append(match);
            }

            lastIndex = matcher.end();
        }

        return htmlBuilder.toString();
    }
}