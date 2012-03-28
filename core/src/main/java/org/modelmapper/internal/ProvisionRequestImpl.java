/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modelmapper.internal;

import org.modelmapper.Provider.ProvisionRequest;

/**
 * @param <T> requested type
 * 
 * @author Jonathan Halterman
 */
class ProvisionRequestImpl<T extends Object> implements ProvisionRequest<T> {
  private final Class<T> requestedType;
  private final Object source;

  ProvisionRequestImpl(final Class<T> requestedType, final Object source) {
    this.requestedType = requestedType;
	this.source = source;
  }

  @Override
  public Class<T> getRequestedType() {
    return requestedType;
  }
  
  @Override
  public Object getSource() {
	  return source;
  };
}