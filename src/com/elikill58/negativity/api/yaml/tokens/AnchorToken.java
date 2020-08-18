package com.elikill58.negativity.api.yaml.tokens;

import com.elikill58.negativity.api.yaml.error.Mark;

public final class AnchorToken extends Token {
	private final String value;

	public AnchorToken(final String value, final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	protected String getArguments() {
		return "value=" + this.value;
	}

	@Override
	public ID getTokenId() {
		return ID.Anchor;
	}
}
