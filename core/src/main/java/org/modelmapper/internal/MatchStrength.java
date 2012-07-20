package org.modelmapper.internal;

import java.util.Comparator;

public enum MatchStrength{
	EXACT(0),
	IGNORING_CASE(1),
	IGNORING_TOKEN_POSITION(2),
	IGNORING_CASE_AND_TOKEN_POSITION(3),
	IGNORING_PROPERTY_POSITION(4),
	IGNORING_CASE_AND_PROPERTY_POSITION(5),
	IGNORING_PROPERTY_AND_TOKEN_POSITION(6),
	IGNORING_CASE_AND_PROPERTY_AND_TOKEN_POSITION(7),
	PARTIAL(8), 
	NONE(100);
	
	private int priority;
	
	private MatchStrength(final int priority){
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}

	public static class MatchStrengthPriorityComparator implements Comparator<MatchStrength>{

		@Override
		public int compare(final MatchStrength arg0, final MatchStrength arg1) {
			return arg0.priority - arg1.priority;
		}
		
	}
	
}