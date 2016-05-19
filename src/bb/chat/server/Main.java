package bb.chat.server;

import bb.chat.gui.ChatServerGUI;
import bb.chat.gui.PortDialog;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author BB20101997
 */
@SuppressWarnings("ClassNamingConvention")
class Main {

	/**
	 * @param tArgs just the usual tArgs to startup a java Program
	 */
	public static void main(String[] tArgs) {

		Logger.getLogger("").setLevel(Level.ALL);

		boolean gui = true;
		int port = 256;

		//loop thought the Arguments
		for(String s:tArgs){
			if(s.equals("nogui")){
				gui = false;
			}
			if(s.startsWith("port=")){
				port = Integer.valueOf(s.replace("port=",""));
			}
		}

		if(port > 65535) {
			throw new IllegalArgumentException("The port has to be smaller than 65535 it was "+port+".");
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
		new ChatServerGUI(port);
	}
}