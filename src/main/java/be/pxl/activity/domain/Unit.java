package be.pxl.activity.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Unit {
	KM, M;

	@JsonCreator
	public static Unit fromString(String value) {
		if (value != null) {
			return Unit.valueOf(value.toUpperCase());
		}
		throw new IllegalArgumentException("Invalid unit: " + value);
	}

	@JsonValue
	public String toJson() {
		return this.name();
	}
}
