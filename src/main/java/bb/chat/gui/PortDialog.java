package bb.chat.gui;

import bb.util.file.log.BBLogHandler;
import bb.util.file.log.Constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class PortDialog extends JDialog implements ActionListener {

	public int     port         = 256;
	public boolean input_gotten = false;

	private final JTextField InText = new JTextField("256");

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger log;

	static {
		log = Logger.getLogger(PortDialog.class.getName());
		//noinspection DuplicateStringLiteralInspection
		log.addHandler(new BBLogHandler(Constants.getLogFile("ChatServer")));
	}

	public PortDialog() {

		log.entering(this.getClass().getName(),"Constructor");

		Box box = new Box(BoxLayout.Y_AXIS);
		JLabel lab = new JLabel("Enter a port "+ System.lineSeparator()+" or hit OK for the standard port 256!");
		JButton OK = new JButton("OK");
		box.add(lab);
		box.add(InText);
		box.add(OK);
		OK.addActionListener(this);
		add(box);
		pack();

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		log.fine("Got an ActionEvent");

		try {
			port = Integer.valueOf(InText.getText());
			input_gotten = true;
		} catch(IllegalArgumentException e) {
			log.fine("IllegalArgumentException");
			input_gotten = false;
		}

		log.finer("Port set to "+port+"!");
		setVisible(false);
	}
}
