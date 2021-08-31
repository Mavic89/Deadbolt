import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.Random;

public class PasswordGenerator {
	public static void PasswordGenerator() {
		JFrame	passGenFrame = new JFrame("Password Generator");
		passGenFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		passGenFrame.setSize(400,250);
		passGenFrame.setResizable(false);
		
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
		topPanel.setBackground(Color.white);
		
		JLabel passGenHeader = new JLabel("Password Generator");
		passGenHeader.setForeground(new Color(161, 16, 16));
		passGenHeader.setFont(new Font("Verdana", Font.PLAIN, 25));
		topPanel.add(passGenHeader);
		
		JLabel chars = new JLabel("Characters:");
		chars.setForeground(new Color(161, 16, 16));
		topPanel.add(chars);
		String[] digits = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
		JComboBox digitList = new JComboBox(digits);
		digitList.setEditable(true);
		topPanel.add(digitList);

		JLabel specialChars = new JLabel("Special Characters?");
		specialChars.setForeground(new Color(161, 16, 16));
		topPanel.add(specialChars);
		String[] specChars = {"Y","N"};
		JComboBox specCharsComboBox = new JComboBox(specChars);
		specCharsComboBox.setEditable(true);
		topPanel.add(specCharsComboBox);
		
		JPanel midPanel = new JPanel();
		midPanel.setBackground(Color.white);
		JButton generateButton = new JButton("Generate");
		JLabel generatedPass = new JLabel("");
		
		generateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String digitCount = digitList.getSelectedItem().toString();
				String specCharsBoolean = specCharsComboBox.getSelectedItem().toString();
				if(specCharsBoolean.equals("Y")) {
					StringBuilder generatedString = new StringBuilder();
					Random random = new Random();
					for(int i = 1; i <= Integer.parseInt(digitCount);++i) {
						int randomInt = random.nextInt(126 - 33) + 33;
						generatedString.append((char)randomInt);
					}
					generatedPass.setText(generatedString.toString());
				}
				else if(specCharsBoolean.equals("N")) {
					StringBuilder generatedString = new StringBuilder();
					Random random = new Random();
					for(int i = 1; i <= Integer.parseInt(digitCount);++i) {
						int randomInt = random.nextInt(122 - 65) + 65;
						if(!(randomInt >= 91 && randomInt <= 96)) {
							generatedString.append((char)randomInt);
						}
						else {
							do {
								randomInt = random.nextInt(122 - 65) + 65;
							}while(randomInt >= 91 && randomInt <= 96);
							generatedString.append((char)randomInt);
						}
					}
					generatedPass.setText(generatedString.toString());
				}
				
			}
			
		});
		midPanel.add(generateButton);
		
		
		
		
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.black);
		

		generatedPass.setForeground(Color.white);
		generatedPass.setFont(new Font("Verdana", Font.PLAIN, 20));
		generatedPass.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton copyButton = new JButton("Copy");
		copyButton.setOpaque(false);
		copyButton.setBorderPainted(false);
		copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					String passText = generatedPass.getText();
				    StringSelection stringSelection = new StringSelection(passText);
				    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				    clpbrd.setContents(stringSelection, null);
			}
		});
		
		bottomPanel.add(generatedPass);
		bottomPanel.add(copyButton);
		
		passGenFrame.getContentPane().add(BorderLayout.NORTH,topPanel);
		passGenFrame.getContentPane().add(BorderLayout.CENTER,midPanel);
		passGenFrame.getContentPane().add(BorderLayout.SOUTH,bottomPanel);
		passGenFrame.setVisible(true);
	}
}
