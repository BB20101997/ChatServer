package bb.chat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class PortDialog extends JDialog implements ActionListener
{

	public int		port		= 256;
	public boolean	inputgotten	= false;

	JTextField		InText		= new JTextField("256");

	public PortDialog()
	{

		Box b = new Box(BoxLayout.Y_AXIS);
		JLabel lab = new JLabel("Enter a port \n or hit OK for the standart port 256!");
		JButton OK = new JButton("OK");
		b.add(lab);
		b.add(InText);
		b.add(OK);
		OK.addActionListener(this);
		add(b);
		addWindowListener(new WinLis());
		pack();

	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{

		try
		{

			port = Integer.valueOf(InText.getText());

		}
		catch(IllegalArgumentException e)
		{
			System.err.println("Input was not an Integer!\nUsing standart port 256!");
		}

		inputgotten = true;
		setVisible(false);
	}

	private class WinLis extends WindowAdapter
	{
		@Override
		public void windowClosed(WindowEvent arg0)
		{

			super.windowClosed(arg0);

			inputgotten = true;
		}

		@Override
		public void windowClosing(WindowEvent e)
		{

			inputgotten = true;
			super.windowClosing(e);
		}
	}

}
