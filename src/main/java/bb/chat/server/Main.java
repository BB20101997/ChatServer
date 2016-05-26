package bb.chat.server;

import bb.chat.gui.ChatServerGUI;
import bb.chat.gui.PortDialog;
import bb.chat.interfaces.IBasicChatPanel;
import bb.util.file.log.BBLogHandler;
import bb.util.file.log.Constants;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author BB20101997
 */
@SuppressWarnings("ClassNamingConvention")
class Main {
	static volatile boolean keepGoing = true;

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger log;

	static {
		log = Logger.getLogger(Main.class.getName());
		log.addHandler(new BBLogHandler(Constants.getLogFile("ChatServer")));
	}


	/**
	 * @param tArgs just the usual tArgs to startup a java Program
	 */
	public static void main(String[] tArgs) {

		Logger.getLogger("").setLevel(Level.ALL);
		log.fine("Starting...");
		boolean gui = true;
		int port = 256;
		boolean portProvided = false;

		//loop thought the Arguments
		for(String s : tArgs) {
			if(s.equals("nogui")) {
				gui = false;
			}
			if(s.startsWith("port=")) {
				port = Integer.valueOf(s.replace("port=", ""));
				portProvided = true;
			}
		}

		if(port > 65535) {
			throw new IllegalArgumentException("The port has to be smaller than 65535 it was " + port + ".");
		}

		if(!portProvided) {
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
				} else {
					System.exit(0);
				}
			} else {
				if(System.console() != null) {
					port = Integer.valueOf(System.console().readLine());
				}
			}
		}

		port = port > 65565 ? 65565 : port;
		if(gui) {
			new ChatServerGUI(port);
		} else {

			ServerChat bc = new ServerChat(port);

			//noinspection PublicMethodWithoutLogging
			IBasicChatPanel BCP = new IBasicChatPanel() {
				@SuppressWarnings("UseOfSystemOutOrSystemErr")
				@Override
				public void WipeLog() {
					System.out.flush();
				}

				@SuppressWarnings("UseOfSystemOutOrSystemErr")
				@Override
				public void print(String s) {
					System.out.print(s);
				}

				@Override
				public void stop() {
					keepGoing = false;
				}
			};

			bc.setBasicChatPanel(BCP);
			boolean consolePresent = System.console() != null;
			if(!consolePresent) {
				log.fine("No Console found,using Scanner!");
			}
			while(keepGoing) {
				if(consolePresent) {
					String s = System.console().readLine();
					bc.Message(s);
				} else {
					Scanner in = new Scanner(System.in);
					String s = in.nextLine();
					bc.Message(s);
				}
			}
			System.exit(0);
		}
	}
}