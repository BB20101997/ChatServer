package bb.chat.server;

import bb.chat.enums.Bundles;
import bb.util.file.log.BBLogHandler;

import java.awt.*;
import java.text.MessageFormat;
import java.util.logging.Logger;

import static bb.util.file.log.Constants.getLogFile;

/**
 * Created by BB20101997 on 12. Aug. 2016.
 */
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class ServerConstants {

	public static final String   LOG_NAME           = "ChatServer";

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger LOGGER = getLogger(ServerConstants.class);

	public static final Dimension MIN_SIZE = new Dimension(500,250);

	public static Logger getLogger(Class clazz) {
		Logger log = Logger.getLogger(clazz.getName());
		log.addHandler(new BBLogHandler(getLogFile(LOG_NAME)));
		if(LOGGER != null) {
			LOGGER.fine(MessageFormat.format(Bundles.LOG_TEXT.getString("log.logger.new"), LOG_NAME));
		}
		return log;
	}

}
