package bb.chat.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import bb.chat.network.ConnectionListener;

/**
 * @author BB20101997
 */
public class ChatServerGUI extends JFrame
{

	private class WindowLisen extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{

			super.windowClosing(e);
			conLis.end();
			System.out.println("Disposing Window");
			dispose();
			System.exit(DISPOSE_ON_CLOSE);
		}
	}

	private static final long	serialVersionUID	= 1L;

	ConnectionListener			conLis;

	private BasicChatPanel		BCP					= new BasicChatPanel();

	/**
	 * @param netwListener
	 *            the ConnectionListener to be linked to
	 */
	public ChatServerGUI(ConnectionListener netwListener)
	{

		super("Server GUI");
		conLis = netwListener;
		addWindowListener(new WindowLisen());
		BCP.addMessageHandler(netwListener.MH);
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
