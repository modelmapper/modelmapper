package org.modelmapper.functional.converter;

import static org.testng.Assert.fail;

import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests mapping between properties where implicit conversion of unknown types
 * is required, eg: from &lt;? extends Object> to java.lang.Number.
 */
@Test(groups = "functional")
public class ImplicitUnknownTypeConversionTest {

	protected ModelMapper strictModelMapper;

	protected ModelMapper standardModelMapper;

	@BeforeMethod
	protected void initContext() {
		strictModelMapper = new ModelMapper();
		strictModelMapper.getConfiguration().setFullTypeMatchingRequired(true);
		standardModelMapper = new ModelMapper();
	}

	static class Property {
		private final long value = 123;

		public long getValue() {
			return value;
		}
	}

	static class EntityWithObjectProperty {
		private Property property;

		public Property getProperty() {
			return property;
		}

		public void setProperty(final Property property) {
			this.property = property;
		}
	}

	static class EntityWithStringProperty {
		private String property;

		public String getProperty() {
			return property;
		}

		public void setProperty(final String property) {
			this.property = property;
		}
	}

	static class EntityWithIntProperty {
		private int property;

		public int getProperty() {
			return property;
		}

		public void setProperty(final int property) {
			this.property = property;
		}
	}

	static class EntityWithCharProperty {
		private char property;

		public char getProperty() {
			return property;
		}

		public void setProperty(final char property) {
			this.property = property;
		}
	}

	static class EntityWithBoolProperty {
		private boolean property;

		public boolean isProperty() {
			return property;
		}

		public void setProperty(final boolean property) {
			this.property = property;
		}
	}

	public void standardMapperShouldAllowImplicitMappingObjectsToStrings() {
		standardModelMapper.createTypeMap(EntityWithObjectProperty.class, EntityWithStringProperty.class);
		standardModelMapper.validate();
	}

	public void standardMapperShouldAllowImplicitMappingObjectsToIntegers() {
		standardModelMapper.createTypeMap(EntityWithObjectProperty.class, EntityWithIntProperty.class);
		standardModelMapper.validate();
	}

	public void standardMapperShouldAllowImplicitMappingObjectsToCharacters() {
		standardModelMapper.createTypeMap(EntityWithObjectProperty.class, EntityWithCharProperty.class);
		standardModelMapper.validate();
	}

	public void standardMapperShouldAllowImplicitMappingObjectsToBooleans() {
		standardModelMapper.createTypeMap(EntityWithObjectProperty.class, EntityWithBoolProperty.class);
		standardModelMapper.validate();
	}

	public void strictMapperShouldNotAllowImplicitMappingObjectsToStrings() {
		strictModelMapper.createTypeMap(EntityWithObjectProperty.class, EntityWithStringProperty.class);
		try {
			strictModelMapper.validate();
		} catch (ValidationException e) {
			return;
		}
		fail();
	}

	public void strictMapperShouldNotAllowImplicitMappingObjectsToIntegers() {
		strictModelMapper.createTypeMap(EntityWithObjectProperty.class, EntityWithIntProperty.class);
		try {
			strictModelMapper.validate();
		} catch (ValidationException e) {
			return;
		}
		fail();
	}

	public void strictMapperShouldNotAllowImplicitMappingObjectsToCharacters() {
		strictModelMapper.createTypeMap(EntityWithObjectProperty.class, EntityWithCharProperty.class);
		try {
			strictModelMapper.validate();
		} catch (ValidationException e) {
			return;
		}
		fail();
	}

	public void strictMapperShouldNotAllowImplicitMappingObjectsToBooleans() {
		strictModelMapper.createTypeMap(EntityWithObjectProperty.class, EntityWithBoolProperty.class);
		try {
			strictModelMapper.validate();
		} catch (ValidationException e) {
			return;
		}
		fail();
	}

}
