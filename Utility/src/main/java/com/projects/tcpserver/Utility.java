package com.projects.tcpserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utility {

	private static final Logger logger = LoggerFactory.getLogger(Utility.class);
	private static final Random random = new Random();
	public static final String MongoDBManagerServletContextAttributeName = "MongoDBManager";
	private static final GregorianCalendar gcalendar = new GregorianCalendar();
	
	public static void shutdownExecutorService(final ExecutorService es, long timeout, TimeUnit timeUnit) throws InterruptedException {
		es.shutdown();
		if (!es.awaitTermination(timeout, timeUnit))
			es.shutdownNow();
		logger.info("Terminated ExecutorService "+es.toString());
	}
	
	public static Properties getApplicationProperties(final String propertiesFileName) throws FileNotFoundException, IOException {
		final Properties p = new Properties();
		try(final InputStream inputStream = ClassLoader.getSystemResourceAsStream(propertiesFileName)) {
			p.load(inputStream);
			return p;
		}
	}
	
	public static int getRandom(final int upperBound) {
		return random.nextInt(upperBound);
	}
	
	public static String getSubString(final String sourceString, final MessageType messageType) {
		return sourceString.substring(sourceString.indexOf(messageType.getMessageDescription()) + messageType.getMessageDescription().length());
	}
	
	public static XMLGregorianCalendar getXMLGregorianCalendar(final Date date) throws DatatypeConfigurationException {
		gcalendar.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcalendar);
	}
}
