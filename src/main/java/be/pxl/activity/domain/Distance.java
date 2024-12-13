package be.pxl.activity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Distance {

	@Column(name = "distance_value")
	private double value;

	@Enumerated(EnumType.STRING)
	@Column(name = "unit")
	private Unit unit;

	public Distance() {
	}

	public Distance(double value, Unit unit) {
		this.value = value;
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	// Method to add two Distance objects together, converting to kilometers if necessary
	public Distance add(Distance distance) {
		double thisValueInKm = this.unit == Unit.KM ? this.value : this.value / 1000;
		double otherValueInKm = distance.unit == Unit.KM ? distance.value : distance.value / 1000;
		return new Distance(thisValueInKm + otherValueInKm, Unit.KM);
	}

}
