package org.modelmapper.internal;

import static org.modelmapper.internal.MatchStrength.EXACT;
import static org.modelmapper.internal.MatchStrength.IGNORING_CASE;
import static org.modelmapper.internal.MatchStrength.IGNORING_CASE_AND_PROPERTY_POSITION;
import static org.modelmapper.internal.MatchStrength.IGNORING_PROPERTY_POSITION;
import static org.modelmapper.internal.MatchStrength.NONE;
import static org.modelmapper.internal.MatchStrength.PARTIAL;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class MatchingSummary {

	private static final String PROPERTY_POSITION_FIELD = "propertyPosition";
	private static final String TOKENS_FIELD = "tokens";

	private final List<String> tokens;
	private final int propertyPosition;

	public MatchingSummary(final List<String> tokens, final int propertyPosition) {
		this.tokens = tokens;
		this.propertyPosition = propertyPosition;
	}

	public MatchStrength matchAgainst(final MatchingSummary destSummary) {
		if (this.equals(destSummary))
		{
			return EXACT;
		}
		else if (this.equalsIgnoreCase(destSummary))
		{
			return IGNORING_CASE;
		}
		else if (this.equalsIgnorePropertyPos(destSummary))
		{
			return IGNORING_PROPERTY_POSITION;
		}
		else if (this.equalsIgnoreCaseAndPropertyPos(destSummary))
		{
			return IGNORING_CASE_AND_PROPERTY_POSITION;
		}else
		{
			List<Boolean> tokenMatchesIgnoringCase = tokenMatchesIgnoringCase(this.tokens, destSummary.tokens);
			if(tokenMatchesIgnoringCase.contains(true)){
				return PARTIAL;
			}
		}
		return NONE;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public int getPropertyPosition() {
		return propertyPosition;
	}

	@Override
	public boolean equals(final Object arg0) {
		return EqualsBuilder.reflectionEquals(this, arg0);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	private boolean equalsIgnorePropertyPos(final MatchingSummary another) {
		return EqualsBuilder.reflectionEquals(this, another, new String[] { PROPERTY_POSITION_FIELD });
	}

	private boolean equalsIgnoreCaseAndPropertyPos(final MatchingSummary another) {
		return equalsIgnoreCase(tokens, another.tokens)
				&& EqualsBuilder.reflectionEquals(this, another, new String[] { TOKENS_FIELD, PROPERTY_POSITION_FIELD });
	}

	private boolean equalsIgnoreCase(final MatchingSummary another) {
		return equalsIgnoreCase(tokens, another.tokens)
				&& EqualsBuilder.reflectionEquals(this, another, new String[] { TOKENS_FIELD });
	}

	private boolean equalsIgnoreCase(final List<String> tokens1, final List<String> tokens2) {
		List<Boolean> matches = tokenMatchesIgnoringCase(tokens1, tokens2);
		List<Boolean> reverseMatches = tokenMatchesIgnoringCase(tokens2, tokens1);
		return !matches.contains(false) && !reverseMatches.contains(false);
	}

	private List<Boolean> tokenMatchesIgnoringCase(final List<String> tokens1, final List<String> tokens2) {
		List<Boolean> result = new ArrayList<Boolean>();
		if (tokens1 == null && tokens2 == null)
		{
			result.add(true);
		}
		else if (tokens1 == null || tokens2 == null)
		{
			result.add(false);
		}
		else if (tokens1.equals(tokens2))
		{
			result.add(true);
		}
		else
		{
			for (String token : tokens1)
			{
				boolean matched = false;
				for(String destToken: tokens2){
					if(destToken.equalsIgnoreCase(token)){
						matched = true;
						break;
					}
				}
				result.add(matched);
			}
		}
		return result;
	}

}
