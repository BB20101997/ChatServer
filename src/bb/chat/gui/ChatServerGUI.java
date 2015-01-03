package bb.chat.gui;

import bb.chat.interfaces.IMessageHandler;
import bb.chat.network.MessageHandlerServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 * @author BB20101997
 */
public class ChatServerGUI extends JFrame {

	private final IMessageHandler IMHandler;

	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			((MessageHandlerServer) IMHandler).getConLis().end();
			System.out.println("Disposing Window");
			dispose();
			System.exit(0);
		}
	}

	public ChatServerGUI(IMessageHandler imh) {

		super("Server GUI");
		IMHandler = imh;
		BasicChatPanel BCP = new BasicChatPanel(IMHandler);
		imh.setBasicChatPanel(BCP);
		addWindowListener(new WindowListen());
		add(BCP);

		setMinimumSize(new Dimension(500, 250));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pack();
	}
}
