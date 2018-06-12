package org.modelmapper.internal;

import java.util.List;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;

/**
 *  An internal mappings that defines internal methods for mappings
 *
 *  @author Chun Han Hsiao
 */
interface InternalMapping extends Mapping {
  /**
   * Creates a merged mapping whose source path begins with the {@code mergedAccessors} and
   * destination path begins with the {@code mergedMutators}.
   */
  InternalMapping createMergedCopy(List<? extends PropertyInfo> mergedAccessors,
      List<? extends PropertyInfo> mergedMutators);

  /**
   * Returns whether the mapping is explicit or implicit.
   */
  boolean isExplicit();
}
