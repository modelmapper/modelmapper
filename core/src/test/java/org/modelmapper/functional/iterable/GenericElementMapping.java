package org.modelmapper.functional.iterable;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Adapted from http://stackoverflow.com/questions/6123759/
 * 
 * @author Jonathan Halterman
 */
@Test
public class GenericElementMapping extends AbstractTest {
  ListWrapper<AccountBo> listWrapper;

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

  public static class DtoListWrapperWrapper {
    private DtoListWrapper dtoListWrapper;

    public void setWrapper(DtoListWrapper dtoListWrapper) {
      this.dtoListWrapper = dtoListWrapper;
    }

    public DtoListWrapper getWrapper() {
      return dtoListWrapper;
    }
  }

  @BeforeMethod
  public void initMemberContext() {
    AccountBo accountA = new AccountBo();
    accountA.setAccountName("Person A");
    AccountBo accountB = new AccountBo();
    accountB.setAccountName("Person B");
    listWrapper = new ListWrapper<AccountBo>();
    listWrapper.getEntries().add(accountA);
    listWrapper.getEntries().add(accountB);
  }

  public void shouldMapExplicitGenericElements() {
    modelMapper.addMappings(new PropertyMap<ListWrapper<AccountBo>, DtoListWrapper>() {
      @Override
      protected void configure() {
        map(source.getEntries()).setEntries(null);
      }
    });

    DtoListWrapper wrapper = modelMapper.map(listWrapper, DtoListWrapper.class);
    assertEquals(wrapper.getEntries().get(0).getAccountName(), "Person A");
    assertEquals(wrapper.getEntries().get(1).getAccountName(), "Person B");
  }

  public void shouldMapDeeplyMappedExplicitGenericElements() {
    modelMapper.addMappings(new PropertyMap<ListWrapper<AccountBo>, DtoListWrapperWrapper>() {
      @Override
      protected void configure() {
        map(source.getEntries()).getWrapper().setEntries(null);
      }
    });

    DtoListWrapperWrapper wrapper = modelMapper.map(listWrapper, DtoListWrapperWrapper.class);
    assertEquals(wrapper.getWrapper().getEntries().get(0).getAccountName(), "Person A");
    assertEquals(wrapper.getWrapper().getEntries().get(1).getAccountName(), "Person B");
  }

  public void shouldMapImplicitGenericElements() {
    AccountBo accountA = new AccountBo();
    accountA.setAccountName("Person A");
    AccountBo accountB = new AccountBo();
    accountB.setAccountName("Person B");

    ListWrapper<AccountBo> listWrapperBo = new ListWrapper<AccountBo>();
    listWrapperBo.getEntries().add(accountA);
    listWrapperBo.getEntries().add(accountB);

    ModelMapper modelMapper = new ModelMapper();
    DtoListWrapper wrapper = modelMapper.map(listWrapperBo, DtoListWrapper.class);
    assertEquals(wrapper.getEntries().get(0).getAccountName(), "Person A");
    assertEquals(wrapper.getEntries().get(1).getAccountName(), "Person B");
  }
}
