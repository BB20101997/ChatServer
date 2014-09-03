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
public class ChatServerGUI extends JFrame
{

	private final IMessageHandler	IMHandler;

	private class WindowListen extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{

			super.windowClosing(e);
			((MessageHandlerServer) IMHandler).getConLis().end();
			System.out.println("Disposing Window");
			dispose();
			System.exit(DISPOSE_ON_CLOSE);
		}
	}

	private final BasicChatPanel		BCP					= new BasicChatPanel();

	public ChatServerGUI(IMessageHandler imh)
	{

		super("Server GUI");
		IMHandler = imh;
		imh.addBasicChatPanel(BCP);
		addWindowListener(new WindowListen());
		BCP.addMessageHandler(imh);
		add(BCP);

		setMinimumSize(new Dimension(500, 250));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pack();
	}

	/**
	 * @param s
	 *            passed down to the print method of the BasicChatPanel
	 */
	public void print(String s)
	{

		BCP.print(s);
	}

	/**
	 * @param s
	 *            passed down to the println method of the BasicChatPanel
	 */
	public void println(String s)
	{

		BCP.println(s);
	}
}
