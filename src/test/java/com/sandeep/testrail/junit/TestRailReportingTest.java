package com.sandeep.testrail.junit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestRailIdProvider.class)
public class TestRailReportingTest {

  @Test
  @TestRail(id="725997")
  public void testCanary(){
    assertTrue(true);
  }

}

