package org.modelmapper.internal;

import static org.modelmapper.internal.MatchStrength.EXACT;
import static org.modelmapper.internal.MatchStrength.IGNORING_CASE;
import static org.modelmapper.internal.MatchStrength.IGNORING_CASE_AND_TOKEN_POSITION;
import static org.modelmapper.internal.MatchStrength.IGNORING_TOKEN_POSITION;
import static org.modelmapper.internal.MatchStrength.NONE;

import java.util.ArrayList;
import java.util.List;

public class TokenMatcher {

	private final List<String> tokens;
	private final List<String> tokens2;

	public TokenMatcher(final List<String> tokens, final List<String> tokens2) {
		this.tokens = tokens;
		this.tokens2 = tokens2;
	}

	public List<MatchStrength> match() {
		List<MatchStrength> strengths = new ArrayList<MatchStrength>();
		List<String> unmatchedTokens = new ArrayList<String>(tokens2);
		int sourcePosition = 0;
		for (String token : tokens)
		{
			boolean tokenAvailableInDestination = tokens2.size() > sourcePosition;
			
			if(tokenAvailableInDestination){
				String destToken = tokens2.get(sourcePosition);
				if (destToken.equals(token))
				{
					strengths.add(EXACT);
					unmatchedTokens.remove(token);
				}
				else if (destToken.equalsIgnoreCase(token))
				{
					strengths.add(IGNORING_CASE);
					boolean remove = unmatchedTokens.remove(destToken);
					if(!remove){ 
						/*
						 * Handle the case where we have a token appearing twice with different cases
						 * and the corresponding one has already been matched in unmatched tokens list
						 */
						remove(unmatchedTokens, destToken);
					}
				}else{
					strengths.add(manuallyMatch(unmatchedTokens, token));
				}
			}
			else
			{
				strengths.add(manuallyMatch(unmatchedTokens, token));
			}
			sourcePosition++;
		}
		
		for (int i = 0; i < unmatchedTokens.size(); i++)
		{
			strengths.add(NONE);
		}

		return strengths;
	}

	private MatchStrength manuallyMatch(final List<String> unmatchedTokens, final String token) {
		MatchStrength strength = NONE;
		int matchPosition = -1; 
		for (int i = 0; i < unmatchedTokens.size() && matchPosition < 0; i++)
		{
			String otherToken = unmatchedTokens.get(i);
			if (token.equals(otherToken))
			{
				strength = IGNORING_TOKEN_POSITION;
				matchPosition = i;
			}
			else if (token.equalsIgnoreCase(otherToken))
			{
				strength = IGNORING_CASE_AND_TOKEN_POSITION;
				matchPosition = i;
			}
		}
		if(matchPosition > -1)
		{
			unmatchedTokens.remove(matchPosition);
		}
		return strength;
	}

	private void remove(final List<String> unmatchedTokens, final String destToken) {
		int matchPosition = -1;
		for (int i = 0; i < unmatchedTokens.size(); i++)
		{
			String otherToken = unmatchedTokens.get(i);
			if (destToken.equalsIgnoreCase(otherToken))
			{
				matchPosition = i;
				break;
			}
		}
		unmatchedTokens.remove(matchPosition);
	}

}
