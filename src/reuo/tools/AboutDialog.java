package reuo.tools;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class AboutDialog extends JDialog {
	private JPanel buttonPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AboutDialog dialog = new AboutDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public AboutDialog() {
		this(null);
	}

	/**
	 * Create the dialog.
	 */
	public AboutDialog(JFrame owner) {
		super(owner);
		setTitle("About vUO");
		setBounds(100, 100, 450, 300);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		{
			buttonPane = new JPanel();
			springLayout.putConstraint(SpringLayout.NORTH, buttonPane, 229, SpringLayout.NORTH, getContentPane());
			springLayout.putConstraint(SpringLayout.WEST, buttonPane, 0, SpringLayout.WEST, getContentPane());
			springLayout.putConstraint(SpringLayout.EAST, buttonPane, 434, SpringLayout.WEST, getContentPane());
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		JLabel lblNewLabel = new JLabel("vUO");
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel, 43, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblNewLabel, 192, SpringLayout.WEST, getContentPane());
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("reuo.github.com");
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_1, 7, SpringLayout.SOUTH, lblNewLabel);
		lblNewLabel_1.setForeground(Color.BLUE);
		getContentPane().add(lblNewLabel_1);
		
		JLabel lblWebsite = new JLabel("Website:");
		springLayout.putConstraint(SpringLayout.EAST, lblWebsite, -381, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_1, 6, SpringLayout.EAST, lblWebsite);
		springLayout.putConstraint(SpringLayout.NORTH, lblWebsite, 7, SpringLayout.SOUTH, lblNewLabel);
		lblWebsite.setLabelFor(lblNewLabel_1);
		getContentPane().add(lblWebsite);
		
		JLabel lblVersion = new JLabel("Version:");
		springLayout.putConstraint(SpringLayout.NORTH, lblVersion, 6, SpringLayout.SOUTH, lblWebsite);
		springLayout.putConstraint(SpringLayout.EAST, lblVersion, 0, SpringLayout.EAST, lblWebsite);
		getContentPane().add(lblVersion);
		
		JLabel label = new JLabel("11.11");
		springLayout.putConstraint(SpringLayout.NORTH, label, 6, SpringLayout.SOUTH, lblNewLabel_1);
		springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, lblNewLabel_1);
		getContentPane().add(label);
	}
}
