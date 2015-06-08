package bb.chat.server;

import bb.chat.gui.ChatServerGUI;
import bb.chat.gui.PortDialog;
import bb.chat.network.ServerConnectionHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author BB20101997
 */
class Main {

	/**
	 * @param tArgs just the usual tArgs to startup a java Program
	 */
	public static void main(String[] tArgs) {

		String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		//noinspection StringConcatenationMissingWhitespace
		File file = new File("Log" + File.pathSeparator + "Server" + File.pathSeparator + "log-" + date + ".fw").getAbsoluteFile();

		boolean gui = !(((tArgs.length > 0) && tArgs[0].equals("nogui")) || ((tArgs.length > 1) && tArgs[1].equals("nogui")));

		int port = 256;
		if(tArgs.length >= 1) {
			try {
				port = Integer.valueOf(tArgs[0]);
				if(port > 65535) {
					throw new NumberFormatException();
				}
			} catch(NumberFormatException e) {
				if(!gui) {
				}
			}
		}

		if(gui) {
			PortDialog p = new PortDialog();
			p.setVisible(true);
			while(p.isVisible()) {
				try {
					Thread.sleep(5);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(p.input_gotten) {
				port = p.port;
			}
			else{
				System.exit(0);
			}
		} else {
			if(System.console() != null) {
				port = Integer.valueOf(System.console().readLine());
			}
		}

		port = port > 65565 ? 65565 : port;
		new ChatServerGUI(new ServerConnectionHandler(port,true));
	}
}