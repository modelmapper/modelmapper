package org.modelmapper.internal;


public class MatchingSummaryWithStrength {

	private final MatchStrength strength;
	private final MatchingSummary summary;
	
	public MatchingSummaryWithStrength(final MatchingSummary summary, final MatchStrength strength) {
		this.summary = summary;
		this.strength = strength;
	}
	
	public MatchStrength getStrength() {
		return strength;
	}
	
	public MatchingSummary getSummary() {
		return summary;
	}
	
}
