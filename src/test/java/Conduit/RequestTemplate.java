package Conduit;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.text.StringSubstitutor;

public class RequestTemplate {

    private final String REQUEST_TEMPLATES_PATH = "templates/requests/";
    private Map<String, Object> requestVariables = new HashMap<>();
    private Pattern compile = Pattern.compile("\\$\\{([a-zA-Z]+)\\}");

    public void addVariable(String key, Object value) {
        requestVariables.put(key, value);
    }

    public String getBodyFromTemplate(String fileName) {

        String requestBodyTemplate;

        List<String> lines = null;
        try {
            lines = Files.lines(Paths.get(getClass()
                            .getClassLoader()
                            .getResource(REQUEST_TEMPLATES_PATH + fileName).toURI()))
                    .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        requestBodyTemplate = buildResult(lines);

        StringSubstitutor sub = new StringSubstitutor(requestVariables);
        return sub.replace(requestBodyTemplate);
    }

    private String buildResult(List<String> lines) {
        String requestBodyTemplate;
        List<String> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String currentLine = lines.get(i);
            if (isKeep(currentLine)) {
                result.add(currentLine);
            } else if (i == (lines.size() - 2)) {
                Matcher matcher = compile.matcher(currentLine);
                if (matcher.find()) {
                    String trim = result.get(result.size() - 1).trim();
                    result.set(result.size() - 1, trim.substring(0, trim.length() - 1));
                }
            }
        }
        requestBodyTemplate = result.stream().collect(joining());
        return requestBodyTemplate;
    }

    private boolean isKeep(String currentLine) {
        return requestVariables.keySet()
                .stream()
                .filter(isLineContainsVariable(currentLine))
                .findAny()
                .isPresent();
    }

    private Predicate<String> isLineContainsVariable(String line) {
        return var -> {
            Matcher matcher = compile.matcher(line);
            if (matcher.find()) {
                return matcher.group(1).equals(var);
            } else {
                return true;
            }
        };
    }
}