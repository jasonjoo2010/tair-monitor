package test;

import java.net.MalformedURLException;
import java.net.URL;


import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import junit.framework.TestCase;

public class testMail extends TestCase {
	public void testMailSend() throws EmailException, MalformedURLException {
		HtmlEmail email = new HtmlEmail();
		
		email.setHostName("smtp.126.com");
		email.setSmtpPort(25);
		email.setAuthenticator(new DefaultAuthenticator("brianzf", "*****"));
		email.setTLS(true);
		email.setFrom("brianzf@126.com");
		email.setSubject("TestMail");
		email.setMsg("This is a mail from Tair Monitor");


		// embed the image and get the content id

		// set the html message
		email.setHtmlMsg("<html>The apache logo - </html>");

		// set the alternative message
		email.setTextMsg("Your email client does not support HTML messages");

		// send the email
		email.send();
	}
}
