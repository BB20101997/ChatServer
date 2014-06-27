package bb.chat.server;

import bb.chat.gui.ChatServerGUI;
import bb.chat.network.ConnectionListener;

/**
 * @author BB20101997
 */
public class Main
{

	/**
	 * @param tArgs
	 *            just the usual tArgs to startup a java Programm
	 */
	public static void main(String[] tArgs)
	{

		int port = 256;
		if(tArgs.length >= 1)
		{
			port = Integer.valueOf(tArgs[0]);
		}

		ConnectionListener netwListener = new ConnectionListener(port);
		netwListener.start();
		if(!((tArgs.length > 1) && tArgs[1].equals("nogui")))
		{
			netwListener.registerGUI(new ChatServerGUI(netwListener));
		}
	}
}