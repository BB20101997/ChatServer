package bb.chat.gui;

import bb.chat.chat.BasicChat;
import bb.chat.command.BasicCommandRegistry;
import bb.chat.security.BasicPermissionRegistrie;
import bb.chat.security.BasicUserDatabase;
import bb.chat.server.ServerChat;
import bb.net.enums.Side;
import bb.net.handler.BasicConnectionManager;
import bb.util.file.log.BBLogHandler;
import bb.util.file.log.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/**
 * @author BB20101997
 */
public class ChatServerGUI extends JFrame {

	final BasicChat bc;

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger log;

	static {
		log = Logger.getLogger(ChatServerGUI.class.getName());
		log.addHandler(new BBLogHandler(Constants.getLogFile("ChatServer")));
	}
	
	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			bc.shutdown();
			log.fine("Disposing Window");
			dispose();
			System.exit(0);
		}
	}

	public ChatServerGUI(int port) {

		super("Server GUI");
		bc = new ServerChat(new BasicConnectionManager(Side.SERVER, port), new BasicPermissionRegistrie(), new BasicUserDatabase(), new BasicCommandRegistry());
		BasicChatPanel BCP = new BasicChatPanel(bc);
		bc.setBasicChatPanel(BCP);
		addWindowListener(new WindowListen());
		add(BCP);

		setMinimumSize(new Dimension(500, 250));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pack();
		setVisible(true);
	}
}
