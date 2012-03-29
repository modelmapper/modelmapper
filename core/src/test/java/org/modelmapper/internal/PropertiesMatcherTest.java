package org.modelmapper.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.modelmapper.internal.MatchStrength.EXACT;
import static org.modelmapper.internal.MatchStrength.IGNORING_CASE;
import static org.modelmapper.internal.MatchStrength.IGNORING_CASE_AND_TOKEN_POSITION;
import static org.modelmapper.internal.MatchStrength.IGNORING_TOKEN_POSITION;
import static org.modelmapper.internal.MatchStrength.NONE;

import java.util.Arrays;
import java.util.List;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class PropertiesMatcherTest {
	
	private PropertiesMatcher underTest;
	@Mock	
	private Configuration configuration;
	
	@BeforeMethod
	public void setUp(){
		initMocks(this);
		underTest = new PropertiesMatcher(configuration);
		stubTokenizers();
	}
	
	public void exactPropertiesShouldReturnExactMatches(){
		PropertyInfo prop1 = stubPropertyInfo("defaultValue");
		PropertyInfo prop2 = stubPropertyInfo("value");
		List<PropertyInfo> props = Arrays.asList(prop1, prop2);
		List<MatchStrength> returned = underTest.compareProperties(props, props);
		assertMatchStrengths(returned, EXACT, EXACT, EXACT);
	}
	
	public void exactIgnoringCaseShouldReturnExactMatchesForTokensWhichAreExactAndExactIgnoringCaseForOthers(){
		PropertyInfo prop1 = stubPropertyInfo("defaultValue");
		PropertyInfo prop2 = stubPropertyInfo("value");
		PropertyInfo prop1WithCaseChange = stubPropertyInfo("DefaultValue");
		
		List<PropertyInfo> props = Arrays.asList(prop1, prop2);
		List<PropertyInfo> dest = Arrays.asList(prop1WithCaseChange, prop2);
		List<MatchStrength> returned = underTest.compareProperties(props, dest);
		assertMatchStrengths(returned, IGNORING_CASE, EXACT, EXACT);
	}
	
	public void noMatchShouldBeReturnedForAdditionalPropertiesWhenDestinationHasAlreadyBeenUsed(){
		PropertyInfo prop1 = stubPropertyInfo("defaultValue");
		PropertyInfo prop2 = stubPropertyInfo("valueValue");
		PropertyInfo prop3 = stubPropertyInfo("value");
		
		List<PropertyInfo> props = Arrays.asList(prop1, prop2, prop3);
		List<PropertyInfo> dest = Arrays.asList(prop1, prop2);
		List<MatchStrength> returned = underTest.compareProperties(props, dest);
		assertMatchStrengths(returned, EXACT, EXACT, EXACT, EXACT, NONE);
	}
	
	public void tokenShiftWithAdditionalTokenInDestination(){
		PropertyInfo prop1 = stubPropertyInfo("defaultValue");
		PropertyInfo prop2 = stubPropertyInfo("value");
		PropertyInfo prop1WithPrefix = stubPropertyInfo("someDefaultValue");
		
		List<PropertyInfo> props = Arrays.asList(prop1, prop2);
		List<PropertyInfo> dest = Arrays.asList(prop1WithPrefix, prop2);
		List<MatchStrength> returned = underTest.compareProperties(props, dest);
		assertMatchStrengths(returned, IGNORING_CASE_AND_TOKEN_POSITION, IGNORING_TOKEN_POSITION, IGNORING_CASE, NONE);
	}
	
	public void tokenShiftWithAdditionalTokenInSource(){
		PropertyInfo prop1WithPrefix = stubPropertyInfo("someDefaultValue");
		PropertyInfo prop1 = stubPropertyInfo("defaultValue");
		PropertyInfo prop2 = stubPropertyInfo("value");
		
		List<PropertyInfo> props = Arrays.asList(prop1WithPrefix, prop2);
		List<PropertyInfo> dest = Arrays.asList(prop1, prop2);
		List<MatchStrength> returned = underTest.compareProperties(props, dest);
		assertMatchStrengths(returned, NONE, IGNORING_CASE_AND_TOKEN_POSITION, IGNORING_CASE, IGNORING_CASE_AND_TOKEN_POSITION);
	}
	
	public void propertyShiftWithAdditionalPropertyInDestination(){
		PropertyInfo prop1 = stubPropertyInfo("defaultValue");
		PropertyInfo prop2 = stubPropertyInfo("value");
		PropertyInfo additionalDestPrefix = stubPropertyInfo("foo");
		
		List<PropertyInfo> props = Arrays.asList(prop1, prop2);
		List<PropertyInfo> dest = Arrays.asList(additionalDestPrefix, prop1, prop2);
		List<MatchStrength> returned = underTest.compareProperties(props, dest);
		assertMatchStrengths(returned, IGNORING_TOKEN_POSITION, IGNORING_TOKEN_POSITION, IGNORING_CASE, NONE);
	}
	
	public void propertyShiftWithAdditionalPropertyInSource(){
		PropertyInfo additionalSourcePrefix = stubPropertyInfo("foo");
		PropertyInfo prop1 = stubPropertyInfo("defaultValue");
		PropertyInfo prop2 = stubPropertyInfo("value");
		
		List<PropertyInfo> props = Arrays.asList(additionalSourcePrefix, prop1, prop2);
		List<PropertyInfo> dest = Arrays.asList(prop1, prop2);
		List<MatchStrength> returned = underTest.compareProperties(props, dest);
		assertMatchStrengths(returned, NONE, IGNORING_TOKEN_POSITION, IGNORING_CASE, IGNORING_CASE_AND_TOKEN_POSITION);
	}
	
	private void assertMatchStrengths(final List<MatchStrength> actual, final MatchStrength... expected) {
		List<MatchStrength> expectedList = Arrays.asList(expected);
		assertEquals(expectedList, actual);
	}

	private PropertyInfo stubPropertyInfo(final String propertyName){
		PropertyInfo prop = Mockito.mock(PropertyInfo.class);
		when(prop.getPropertyType()).thenReturn(PropertyType.FIELD);
		when(prop.getName()).thenReturn(propertyName);
		return prop;
	}
	
	private void stubTokenizers() {
		when(configuration.getSourceNameTokenizer()).thenReturn(NameTokenizers.CAMEL_CASE);
		when(configuration.getDestinationNameTokenizer()).thenReturn(NameTokenizers.CAMEL_CASE);
	}

}
