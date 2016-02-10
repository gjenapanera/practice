/**
 * 
 */
package com.mgm.dmp.dao.impl.mock;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.fasterxml.jackson.core.io.NumberInput;

/**
 * @author ssahu6
 *
 */
public final class MultiDateFormat extends DateFormat implements Cloneable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3533253872398209547L;
	private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("GMT");
	private static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	private Set<String> patternList = new HashSet<String>();
	private TimeZone timeZone = DEFAULT_TIMEZONE;
	
	public MultiDateFormat() {
		this(DATE_FORMAT_STR_ISO8601);
	}

	public MultiDateFormat(String pattern) {
		this(pattern, DEFAULT_TIMEZONE);
	}
	
	public MultiDateFormat(String pattern, TimeZone tz) {
		patternList.addAll(Arrays.asList(StringUtils.split(StringUtils.trimToEmpty(pattern), "|")));
		patternList.add(DATE_FORMAT_STR_ISO8601);
		setTimeZone(tz == null ? DEFAULT_TIMEZONE : tz);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.DateFormat#format(java.util.Date, java.lang.StringBuffer,
	 * java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo,
			FieldPosition fieldPosition) {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601);
		df.setTimeZone(DEFAULT_TIMEZONE);
		return df.format(date, toAppendTo, fieldPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.DateFormat#parse(java.lang.String,
	 * java.text.ParsePosition)
	 */
	@Override
	public Date parse(String source, ParsePosition pos) {
		Date target = null;
		if(NumberUtils.isDigits(source) && NumberInput.inLongRange(source, false)) {
			target = new Date(Long.parseLong(source));
		} else {
			for(String pattern : patternList) {
				DateFormat df = new SimpleDateFormat(pattern);
				df.setTimeZone(getTimeZone());
				target = df.parse(source, pos);
				if(target != null) {
					break;
				}
			}
		}
		return target;
	}

	/* (non-Javadoc)
	 * @see java.text.DateFormat#parse(java.lang.String)
	 */
	@Override
	public Date parse(String source) throws ParseException {
		ParsePosition localParsePosition = new ParsePosition(0);
		Date target = parse(source, localParsePosition);
		if(target == null) {
		    throw new ParseException(String.format("Can not parse date \"%s\": not compatible with any of standard forms (%s)", new Object[] { source, StringUtils.join(patternList, "|") }), localParsePosition.getErrorIndex());
		} else {
			return target;
		}
	}

	/* (non-Javadoc)
	 * @see java.text.DateFormat#clone()
	 */
	@Override
	public Object clone() {
		return new MultiDateFormat(StringUtils.join(patternList, "|"), getTimeZone());
	}

	/* (non-Javadoc)
	 * @see java.text.DateFormat#setTimeZone(java.util.TimeZone)
	 */
	@Override
	public void setTimeZone(TimeZone zone) {
		this.timeZone = zone;
	}

	/* (non-Javadoc)
	 * @see java.text.DateFormat#getTimeZone()
	 */
	@Override
	public TimeZone getTimeZone() {
		return this.timeZone;
	}

}
