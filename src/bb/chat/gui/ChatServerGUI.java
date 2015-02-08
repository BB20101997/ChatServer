package bb.chat.gui;

import bb.chat.command.BasicCommandRegistry;
import bb.chat.interfaces.IConnectionHandler;
import bb.chat.main.BasicChat;
import bb.chat.network.packet.PacketDistributor;
import bb.chat.network.packet.PacketRegistrie;
import bb.chat.security.BasicPermissionRegistrie;
import bb.chat.security.BasicUserDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 * @author BB20101997
 */
public class ChatServerGUI extends JFrame {

	BasicChat basicChat;

	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			basicChat.shutdown();
			System.out.println("Disposing Window");
			dispose();
			System.exit(0);
		}
	}

	public ChatServerGUI(IConnectionHandler imh) {

		super("Server GUI");
		basicChat = new BasicChat(imh,new PacketRegistrie(),new BasicPermissionRegistrie(),new PacketDistributor(imh),new BasicUserDatabase(),new BasicCommandRegistry());
		BasicChatPanel BCP = new BasicChatPanel(imh);
		basicChat.setBasicChatPanel(BCP);
		addWindowListener(new WindowListen());
		add(BCP);

		setMinimumSize(new Dimension(500, 250));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pack();
		setVisible(true);
	}
}
