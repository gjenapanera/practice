/**
 * 
 */
/**
 * 
 */
package com.mgm.dmp.web.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.web.constant.DmpWebConstant;

/**
 * @author spal8
 * 
 */
@Controller
@RequestMapping(value = DmpWebConstant.CALENDAR_URI, method = RequestMethod.GET,
	consumes = { "*/*" }, 
	produces = { "*/*" })
public class CalendarController {
	
	private static final Logger LOG = LoggerFactory.getLogger(CalendarController.class);
	
	/**
	 * This controller method returns the ics file
	 * @param request
	 * @param result
	 * @param locale
	 * @param offerCounts
	 * @return
	 */
	@RequestMapping(value = "/event")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void getCalendarEvent(@RequestParam Map<String, Object> params, HttpServletResponse response) {
		String calFile = "mycalendar.ics";
		ServletOutputStream fout = null;
		try {
			Calendar calendar = new Calendar();
			calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
			calendar.getProperties().add(Version.VERSION_2_0);
			calendar.getProperties().add(CalScale.GREGORIAN);
			String propertyId = (String)params.get("propertyId");
			
			java.util.Calendar startCal = DateUtil.getCurrentCalendar(propertyId);
			startCal.setTimeInMillis(Long.valueOf((String)params.get("start")));
			
			java.util.Calendar endCal = DateUtil.getCurrentCalendar(propertyId);
			endCal.setTimeInMillis(Long.valueOf((String)params.get("end")));
			
			VEvent calendarEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(startCal.getTime()),new net.fortuna.ical4j.model.DateTime(endCal.getTime()),(String)params.get("title"));
			calendarEvent.getProperties().add(new Location((String)params.get("location")));
			calendarEvent.getProperties().add(new Description(((String)params.get("description")).replace("@@", "\n").replace("__", ":").replace("@_", "(").replace("_@", ")")));
			calendar.getComponents().add(calendarEvent);

			response.setHeader("Content-Disposition", "inline;filename=\""+ calFile +"\"");
			response.setContentType(DmpWebConstant.MEDIA_TYPE_CALENDAR);
			fout = response.getOutputStream();
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.setValidating(false);
			outputter.output(calendar, fout);
			
		} catch (IOException e) {
			LOG.error("IOException occurred", e);
		} catch (ValidationException ve) {
			LOG.error("ValidationException occurred", ve);
		} finally {
			try {
				if(fout!=null){
					fout.close();
				}
			} catch (IOException e) {
				LOG.error("IOException occurred", e);
			}
		}
	}
}
