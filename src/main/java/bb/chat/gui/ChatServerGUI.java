package bb.chat.gui;

import bb.chat.chat.BasicChat;
import bb.chat.command.BasicCommandRegistry;
import bb.chat.enums.Bundles;
import bb.chat.security.BasicPermissionRegistrie;
import bb.chat.security.BasicUserDatabase;
import bb.chat.server.ServerChat;
import bb.chat.server.ServerConstants;
import bb.net.handler.BasicConnectionManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/**
 * @author BB20101997
 */
public class ChatServerGUI extends JFrame {

	private final BasicChat bc;

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger logger = ServerConstants.getLogger(ChatServerGUI.class);

	/**
	 * Handling the shutdown on closing the window
	 * */
	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			bc.shutdown();
			logger.fine(Bundles.LOG_TEXT.getString("log.window.dispose"));
			dispose();
			System.exit(0);
		}
	}

	/**
	 * @param port the port the Server shall listen on
	 * */
	public ChatServerGUI(int port) {

		super(Bundles.BUTTON_LABEL.getString("title.server"));
		bc = new ServerChat(new BasicConnectionManager(port), new BasicPermissionRegistrie(), new BasicUserDatabase(), new BasicCommandRegistry());

		final BasicChatPanel bcp = new BasicChatPanel(bc);
		bc.setBasicChatPanel(bcp);

		addWindowListener(new WindowListen());
		add(bcp);

		setMinimumSize(ServerConstants.MIN_SIZE);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pack();
		setVisible(true);
	}
}
