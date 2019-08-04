package com.sandeep.testrail.junit;

import com.codepine.api.testrail.model.Project;
import com.codepine.api.testrail.model.Result;
import com.codepine.api.testrail.model.ResultField;
import com.codepine.api.testrail.model.Run;
import com.codepine.api.testrail.TestRail;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class TestRailReport {

  private static List<Result> results = new ArrayList<>();

  public static void addResult(Result result){
    results.add(result);
  }

  public static void reportResults(){

    Properties properties = loadProperties();
    String url = properties.getProperty("testrail_url").trim();
    String userId = properties.getProperty("testrail_userId").trim();
    String pwd = properties.getProperty("testrail_pwd").trim();
    String projectId = properties.getProperty("testrail_projectId").trim();
    TestRail testRail = TestRail.builder(url, userId, pwd).build();

    Project project = testRail.projects().get(Integer.valueOf(projectId)).execute();
    Run run = testRail.runs()
        .add(project.getId(),
            new Run().setName("TestRail unit test reports ")
                .setIncludeAll(false)
                .setCaseIds(results.stream()
                    .map(k -> k.getCaseId()).collect(Collectors.toList()))
        ).execute();
    List<ResultField> customResultFields = testRail.resultFields().list().execute();
    testRail.results().addForCases(run.getId(),results,customResultFields).execute();
    testRail.runs().close(run.getId()).execute();
  }

  public static Properties loadProperties(){
    String env = Optional.ofNullable(System.getProperty("env")).orElse("localhost");
    Properties properties = new Properties();
    InputStream is = ClassLoader.class.getResourceAsStream("/" +env + ".properties");
    try {
      properties.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return properties;
  }

}
