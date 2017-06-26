package org.modelmapper.inheritance;

import java.util.List;

public class CcDTO {

  private List<BaseDest> bases;

  public List<BaseDest> getBases() {
    return bases;
  }

  public void setBases(List<BaseDest> bases) {
    this.bases = bases;
  }
}
