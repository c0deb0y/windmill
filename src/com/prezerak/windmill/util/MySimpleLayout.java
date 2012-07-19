package com.prezerak.windmill.util;

import java.util.Date;

import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;

public class MySimpleLayout extends SimpleLayout {

	@Override
	public String format(final LoggingEvent arg0) {
		// TODO Auto-generated method stub
		
		StringBuffer sb = new StringBuffer();
		Date d = new Date(arg0.getTimeStamp());
		sb.append(d.toString());
		sb.append(" - ");
		sb.append(arg0.getRenderedMessage());	
		sb.append("-\r\n");
		return sb.toString();
	}

	
}
