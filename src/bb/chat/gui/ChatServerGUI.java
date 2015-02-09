package bb.chat.gui;

import bb.chat.command.BasicCommandRegistry;
import bb.chat.main.BasicChat;
import bb.chat.network.ServerConnectionHandler;
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
		ServerConnectionHandler sch;
		bc = new BasicChat(sch = new ServerConnectionHandler(port),new PacketRegistrie(),new BasicPermissionRegistrie(),new PacketDistributor(sch),new BasicUserDatabase(),new BasicCommandRegistry());
		BasicChatPanel BCP = new BasicChatPanel(sch);
		bc.setBasicChatPanel(BCP);
		addWindowListener(new WindowListen());
		add(BCP);

		setMinimumSize(new Dimension(500, 250));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pack();
	}
}
