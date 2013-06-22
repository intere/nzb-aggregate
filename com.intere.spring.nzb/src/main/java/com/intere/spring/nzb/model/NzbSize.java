package com.intere.spring.nzb.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@SuppressWarnings("serial")
@XmlType(name="NzbSize")
@XmlAccessorType(XmlAccessType.FIELD)
public class NzbSize implements Serializable, Comparable<NzbSize> {

	/** The Unit Enumeration. */
	@XmlType
	@XmlEnum(String.class)
	public enum Unit {
		/** Bytes.  */
		B("Bytes", 1L), 
		/** Kilobytes.  */
		KB("Kilobytes", 1024L), 
		/** Megabytes. */
		MB("Megabytes", 1024L * 1024L), 
		/** Gigabytes.  */
		GB("Gigabytes", 1024L * 1024L * 1024L);

		private long multiplier;
		
		@XmlValue
		private String value;

		private Unit(String value, long multiplier) {
			this.value = value;
			this.multiplier = multiplier;
		}

		public String getValue() {
			return value;
		}

		public long getMultiplier() {
			return multiplier;
		}
	}

	@XmlAttribute(name="unit")
	private Unit unit;
	
	@XmlAttribute(name="size")
	private Double size;
	
	@XmlTransient
	private Long sizeInBytes;

	/**
	 * Default Constructor - don't use.
	 */
	public NzbSize() {
	}

	/**
	 * String based constructor - sets the units and size.
	 * 
	 * @param unit
	 * @param size
	 */
	public NzbSize(String unit, Double size) {
		this(Unit.valueOf(unit), size);
	}

	/**
	 * Unit based constructor - sets the units and size.
	 * 
	 * @param unit
	 * @param size
	 */
	public NzbSize(Unit unit, Double size) {
		this.unit = unit;
		this.size = size;
		calculateSizeInBytes();
	}

	/**
	 * This method calculates the size in bytes.
	 */
	private void calculateSizeInBytes() {
		this.sizeInBytes = (long) ((double) unit.getMultiplier() * size);
	}

	@Override
	public int compareTo(NzbSize o) {
		
		if (this == o)
			return 0;
		if (o == null)
			return size.intValue();

		if (getSizeInBytes() == null) {
			if (o.sizeInBytes != null)
				return -o.getSize().intValue();
		} else {
			
			long diff = getSizeInBytes() - o.getSizeInBytes();
			
			if(diff==0) {
				return 0;
			} else if (diff <0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			}
		}
		
		return getSize().intValue();
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Long getSizeInBytes() {
		if(sizeInBytes==null)
		{
			calculateSizeInBytes();
		}
		return sizeInBytes;
	}

	@Override
	public String toString() {
		return String.valueOf(size) + " " + unit.name();
	}	
	
}
