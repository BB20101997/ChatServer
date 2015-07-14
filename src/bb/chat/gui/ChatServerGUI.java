package bb.chat.gui;

import bb.chat.chat.BasicChat;
import bb.chat.command.BasicCommandRegistry;
import bb.chat.security.BasicPermissionRegistrie;
import bb.chat.security.BasicUserDatabase;
import bb.chat.server.ServerChat;
import bb.net.enums.Side;
import bb.net.handler.BasicConnectionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author BB20101997
 */
public class ChatServerGUI extends JFrame {

	BasicChat bc;

	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			bc.shutdown();
			System.out.println("Disposing Window");
			dispose();
			System.exit(0);
		}
	}

	public ChatServerGUI(int port) {

		super("Server GUI");
		BasicConnectionManager sch;
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
