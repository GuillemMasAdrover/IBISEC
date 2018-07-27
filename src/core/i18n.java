package core;

import java.util.*;

public class i18n {

	public static String label(String id){
		Locale currentLocale;
        ResourceBundle messages;
        currentLocale = new Locale("es", "ES");
		messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
		return messages.getString(id);
	}
}