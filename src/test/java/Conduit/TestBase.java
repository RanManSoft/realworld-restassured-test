package Conduit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.File;
import java.net.URL;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class TestBase {
    public static String baseURL;
    private static Map<String, Map<String, String>> testDatas = new HashMap<>();


    public TestBase() {
        String env = System.getProperty("env");
        Config config = ConfigFactory.load("conf/common.conf").getConfig(env);
        String TEST_DATA_FILE_PATH = "data/" + env + "/test_datas.csv";
        initialTestData(TEST_DATA_FILE_PATH);
        baseURL = config.getString("BaseURL");
    }
    public File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
    }

    public void initialTestData(String testDataCSVFilePATH) {
        try (Reader in = new FileReader(getFileFromResources(testDataCSVFilePATH))) {
            Iterable<CSVRecord> testCVSDatas = CSVFormat.RFC4180.withFirstRecordAsHeader().withIgnoreSurroundingSpaces().parse(in);
            for (CSVRecord testCVSData : testCVSDatas) {
                testDatas.putIfAbsent(testCVSData.get("UserName"), testCVSData.toMap());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getUserwithName(String name) {
        return new HashMap<>(testDatas.get(name));
    }

    public RequestTemplate addTemplateVarsWithAccounts(RequestTemplate requestTemplate, Map<String, String> requestVariables) {
        for (Map.Entry<String, String> variable : requestVariables.entrySet()) {
            requestTemplate.addVariable(variable.getKey(), variable.getValue());
        }
        return requestTemplate;
    }
    public String generateRequestBody(Map<String, String> requestVariables, String requestTemplateFile) {
        RequestTemplate requestTemplate = new RequestTemplate();
        requestTemplate = addTemplateVarsWithAccounts(requestTemplate, requestVariables);
        return requestTemplate.getBodyFromTemplate(requestTemplateFile);
    }
}
