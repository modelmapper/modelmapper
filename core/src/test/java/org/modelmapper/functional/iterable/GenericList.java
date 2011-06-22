package org.modelmapper.functional.iterable;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.testng.annotations.Test;

@Test
public class GenericList {
  public static class AccountDto {
    private String accountName;

    public String getAccountName() {
      return accountName;
    }

    public void setAccountName(String accountName) {
      this.accountName = accountName;
    }
  }

  public static class AccountBo {
    private String accountName;

    public String getAccountName() {
      return accountName;
    }

    public void setAccountName(String accountName) {
      this.accountName = accountName;
    }
  }

  public static class ListWrapper<T> {
    private List<T> entries = new ArrayList<T>();

    public List<T> getEntries() {
      return entries;
    }

    public void setEntries(List<T> entries) {
      this.entries = entries;
    }
  }

  public static class DtoListWrapper extends ListWrapper<AccountDto> {
  }

  public void shouldMapBoToDto() {
    AccountBo accountA = new AccountBo();
    accountA.setAccountName("Person A");
    AccountBo accountB = new AccountBo();
    accountB.setAccountName("Person B");

    ListWrapper<AccountBo> listWrapperBo = new ListWrapper<AccountBo>();
    listWrapperBo.getEntries().add(accountA);
    listWrapperBo.getEntries().add(accountB);

    ModelMapper modelMapper = new ModelMapper();
    DtoListWrapper wrapper = modelMapper.map(listWrapperBo, DtoListWrapper.class);
    int i = 0;
  }
}
