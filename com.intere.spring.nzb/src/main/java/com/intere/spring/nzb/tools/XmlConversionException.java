package com.intere.spring.nzb.tools;

/**
 * This class is just a special wrapper for another Exception type.
 * 
 * @author Eric Internicola (intere@gmail.com)
 */
@SuppressWarnings("serial")
public class XmlConversionException extends Exception {


	/**
	 * Constructor that takes the message and cause.
	 * 
	 * @param message
	 * @param cause
	 */
	public XmlConversionException(String message, Throwable cause) {
		super(message, cause);

	}
}
