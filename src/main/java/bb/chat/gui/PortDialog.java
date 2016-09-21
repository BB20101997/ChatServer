package bb.chat.gui;

import bb.chat.server.ServerConstants;

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
	private static final Logger log = ServerConstants.getLogger(JDialog.class);

	public PortDialog() {

		log.entering(this.getClass().getName(),"Constructor");

		Box box = new Box(BoxLayout.Y_AXIS);
		JLabel lab = new JLabel("Enter a port "+ System.lineSeparator()+" or hit OK for the standard port 256!");
		JButton ok = new JButton("OK");
		box.add(lab);
		box.add(InText);
		box.add(ok);
		ok.addActionListener(this);
		add(box);
		pack();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.fine("Got an ActionEvent");

		try {
			port = Integer.valueOf(InText.getText());
			if(port>65535){
				return;
			}
			input_gotten = true;
			setVisible(false);
			log.finer("Port set to " + port + "!");
		} catch(IllegalArgumentException ex) {
			log.fine("IllegalArgumentException");
			input_gotten = false;
		}



	}
}
