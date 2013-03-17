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
package org.modelmapper.convention;

import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NameableType;

/**
 * {@link NameTransformer} implementations.
 * 
 * @author Jonathan Halterman
 */
public class NameTransformers {
	
	/**
	 * Utility method to take a string and convert it to normal Java variable
	 * name capitalization.  This normally means converting the first
	 * character from upper case to lower case, but in the (unusual) special
	 * case when there is more than one character and both the first and
	 * second characters are upper case, we leave it alone.
	 * <p>
	 * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays
	 * as "URL".
	 *
	 * @param  name The string to be decapitalized.
	 * @return  The decapitalized version of the string.
	 */
	private static String decapitalize(String name) {
	    if (name == null || name.length() == 0) {
	        return name;
	    }
	    if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
	                    Character.isUpperCase(name.charAt(0))){
	        return name;
	    }
	    char chars[] = name.toCharArray();
	    chars[0] = Character.toLowerCase(chars[0]);
	    return new String(chars);
	}
    
  /**
   * Transforms accessor names to their simple property name according to the JavaBeans convention.
   * Class and field names are unchanged.
   */
  public static final NameTransformer JAVABEANS_ACCESSOR = new NameTransformer() {
    public String transform(String name, NameableType nameableType) {
      if (NameableType.METHOD.equals(nameableType)) {
        if (name.startsWith("get"))
          return NameTransformers.decapitalize(name.substring(3));
        else if (name.startsWith("is"))
          return NameTransformers.decapitalize(name.substring(2));
      }

      return name;
    }

    @Override
    public String toString() {
      return "Javabeans Accessor";
    }
  };

  /**
   * Transforms mutator names to their simple property name according to the JavaBeans convention.
   * Class and field names are unchanged.
   */
  public static final NameTransformer JAVABEANS_MUTATOR = new NameTransformer() {
    public String transform(String name, NameableType nameableType) {
      if (NameableType.METHOD.equals(nameableType) && name.startsWith("set"))
        return NameTransformers.decapitalize(name.substring(3));
      return name;
    }

    @Override
    public String toString() {
      return "Javabeans Mutator";
    }
  };
}
