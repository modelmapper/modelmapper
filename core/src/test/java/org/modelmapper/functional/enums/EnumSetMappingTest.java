package org.modelmapper.functional.enums;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


@Test
public class EnumSetMappingTest {
	private enum E {
    E1, E2, E3, E4
	}

	static class A {
		private EnumSet<E> enumsA;

		public EnumSet<E> getEnumsA() {
			return enumsA;
		}
		public void setEnumsA(EnumSet<E> enumsA) {
			this.enumsA = enumsA;
		}
	}


	static class B {
		private EnumSet<E> enumsB;

		public EnumSet<E> getEnumsB() {
			return enumsB;
		}
		public void setEnumsB(EnumSet<E> enumsB) {
			this.enumsB = enumsB;
		}
	}

	private static Converter<EnumSet<?>, EnumSet<?>> enumConverter = context -> {
		Object source = context.getSource();
		if (source == null)
			return null;

		return EnumSet.copyOf((EnumSet<?>) source);
	};

	private ModelMapper initMapperWithPropertyMap() {
		ModelMapper mapper = new ModelMapper();
		mapper.addMappings(new PropertyMap<A, B>() {
			@Override
			protected void configure() {
				using(enumConverter).map(source.getEnumsA()).setEnumsB(null);
			}
		});
		return mapper;
	}

	private ModelMapper initMapperWithAddMapping() {
		ModelMapper mapper = new ModelMapper();
		mapper.typeMap(A.class, B.class)
				.addMappings(mapping -> mapping.using(enumConverter).map(A::getEnumsA, B::setEnumsB));
		return mapper;
	}

	private List<ModelMapper> mappers() {
		return Arrays.asList(
				initMapperWithPropertyMap(), initMapperWithAddMapping());
	}

	public void testEnumSetWithValues() {
		for (ModelMapper mapper : mappers()) {
			A a = new A();
			a.enumsA = EnumSet.of(E.E1, E.E2);
			B b = mapper.map(a, B.class);
			mapper.validate();

			assertTrue(b.getEnumsB().contains(E.E1));
			assertTrue(b.getEnumsB().contains(E.E2));
		}
	}


	public void testEnumSetAsEmpty() {
		for (ModelMapper mapper : mappers()) {
			A a = new A();
			B b = mapper.map(a, B.class);
			mapper.validate();

			assertNull(b.getEnumsB());
		}
	}
}