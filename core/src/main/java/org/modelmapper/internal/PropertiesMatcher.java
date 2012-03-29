package org.modelmapper.internal;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.config.Configuration;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.PropertyInfo;

public class PropertiesMatcher {

	private final Configuration configuration;

	PropertiesMatcher(final Configuration configuration) {
		this.configuration = configuration;
	}

	public List<MatchStrength> compareProperties(final List<? extends PropertyInfo> matching,
			final List<? extends PropertyInfo> matchingAgainst) {

		List<String> sourceTokens = getAllTokens(matching, configuration.getSourceNameTokenizer());
		List<String> destinationTokens = getAllTokens(matchingAgainst, configuration.getDestinationNameTokenizer());
		
		TokenMatcher matcher = new TokenMatcher(sourceTokens, destinationTokens);
		return matcher.match();
	}

	private List<String> getAllTokens(final List<? extends PropertyInfo> properties, final NameTokenizer nameTokenizer) {
		List<String> toReturn = new ArrayList<String>();
		for (PropertyInfo property : properties)
		{
			NameableType nameableType = NameableType.forPropertyType(property.getPropertyType());
			List<String> tokens = asList(nameTokenizer.tokenize(property.getName(), nameableType));
			toReturn.addAll(tokens);
		}
		return toReturn;
	}

}
