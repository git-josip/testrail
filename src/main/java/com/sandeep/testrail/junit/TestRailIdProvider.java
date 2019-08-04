package com.sandeep.testrail.junit;

import com.codepine.api.testrail.model.Result;
import java.util.Optional;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestWatcher;


public class TestRailIdProvider implements TestWatcher, BeforeAllCallback {
  private static boolean started = false;
  private static final String TESTRAIL_REPORT = "TEST_RAIL";

  @Override
  public void testDisabled(ExtensionContext extensionContext, Optional<String> optional) {
    addResult(extensionContext, TestRailStatus.UNTESTED);
  }

  @Override
  public void testSuccessful(ExtensionContext extensionContext) {
    addResult(extensionContext,TestRailStatus.PASSED);
  }

  @Override
  public void testAborted(ExtensionContext extensionContext, Throwable throwable) {
    addResult(extensionContext,TestRailStatus.RETEST);
  }

  @Override
  public void testFailed(ExtensionContext extensionContext, Throwable throwable) {
    addResult(extensionContext, TestRailStatus.FAILED);
  }

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception {
    if(!started) {
      getStore(extensionContext).put(TESTRAIL_REPORT, new CloseableOnlyOnceResource());
      started = true;
    }

  }

  private static class CloseableOnlyOnceResource implements
      ExtensionContext.Store.CloseableResource {
    @Override
    public void close() {
      //After all tests run hook.
      //Any additional desired action goes here
      TestRailReport.reportResults();
    }
  }

  private void addResult(ExtensionContext extensionContext, TestRailStatus status){

    if(extensionContext.getElement().isPresent() && extensionContext.getElement().get().isAnnotationPresent(
        TestRail.class)){
      TestRail element = extensionContext.getElement().get().getAnnotation(TestRail.class);
      Result result = new Result().setTestId(Integer.valueOf(element.id())).setStatusId(status.getId())
          .setCaseId(Integer.valueOf(element.id()));
        TestRailReport.addResult(result);
    }
  }

  private Store getStore(ExtensionContext context) {
    return context.getRoot().getStore(Namespace.GLOBAL);
  }


}
