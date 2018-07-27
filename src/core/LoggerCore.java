package core;

import java.io.IOException;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class LoggerCore {
	
	public static void addLog(String text, String usuari) {
		BasicConfigurator.configure();
		Logger log = Logger.getLogger("Logger de Ejemplo");
		try {
			//log.addAppender(new FileAppender(new PatternLayout(),"V:/INTERCANVI D'OBRES/MAS, GUILLEM/prova.log", false));	
			// Get the base naming context
		    Context env;
			try {
				env = (Context)new InitialContext().lookup("java:comp/env");
				String ruta =  (String)env.lookup("ruta_base");
				log.addAppender(new FileAppender(new PatternLayout(),ruta + "/logs/intranetIBISEC.log", false));	
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    // Get a single value
			
			log.error(new Date().toString() + " - " + usuari + " - " + text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
