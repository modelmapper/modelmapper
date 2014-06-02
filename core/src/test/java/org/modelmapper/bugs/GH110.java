package org.modelmapper.bugs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/110
 */
@Test
public class GH110 extends AbstractTest {
  interface ITestTransaction {
    String getDateTimeOut();
  }

  static class TestTransactionDTO {
    String dateTimeOut;
  }

  public void test() {
    ITestTransaction transactionMock = mock(ITestTransaction.class);
    when(transactionMock.getDateTimeOut()).thenReturn("12345678");

    TestTransactionDTO response = modelMapper.map(transactionMock, TestTransactionDTO.class);

    assertEquals(response.dateTimeOut, transactionMock.getDateTimeOut());
  }
}
