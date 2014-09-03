package bb.chat.server;

import bb.chat.gui.PortDialog;
import bb.chat.network.MessageHandlerServer;

/**
 * @author BB20101997
 */
class Main
{

	/**
	 * @param tArgs
	 *            just the usual tArgs to startup a java Program
	 */
	public static void main(String[] tArgs)
	{

		boolean gui = !(((tArgs.length > 0) && tArgs[0].equals("nogui")) || ((tArgs.length > 1) && tArgs[1].equals("nogui")));

		int port = 256;
		if(tArgs.length >= 1)
		{
			try
			{
				port = Integer.valueOf(tArgs[0]);
                if(port > 65535){
                    throw new NumberFormatException();
                }
			}
			catch(NumberFormatException e)
			{
               if(!gui) {
                   System.err.println("Program Argument 1 was not a valid Port Number!");
                   System.err.println("Using default Port 256");
               }
			}
		}

		if(gui)
		{
			PortDialog p = new PortDialog();
			p.setVisible(true);
			while(!p.input_gotten)
			{
				// System.out.println("Loop");
				try
				{
					Thread.sleep(5);
				}
				catch(InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			port = p.port;
		}
		else
		{
			if(System.console() != null)
			{
				port = Integer.valueOf(System.console().readLine());
			}
		}

		port = port > 65565 ? 65565 : port;

		new MessageHandlerServer(port, gui);

	}
}