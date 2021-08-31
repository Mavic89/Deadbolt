import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.spec.KeySpec;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.simple.parser.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class View {

	static File boltFile;
	static File saveDir;
	public static void main(String args[]) {
		JFrame mainFrame = new JFrame("Deadbolt");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(300,320);
		mainFrame.setResizable(false);
		
		
		JMenuBar viewMenuBar = new JMenuBar();
		JMenu viewMenu = new JMenu("Options");
		
		
		JMenuItem createNew = new JMenuItem("Create new Bolt");
		createNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CreateBolt.CreateBolt();;
				
			}
			
		});
		
		JMenuItem passGen = new JMenuItem("Password Generator");
		passGen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PasswordGenerator.PasswordGenerator();
				
			}
			
		});
		JMenuItem apiCheck = new JMenuItem("Compromised Credentials Check");
		
		viewMenu.add(createNew);
		viewMenu.add(passGen);
		viewMenu.add(apiCheck);
		viewMenuBar.add(viewMenu);
		mainFrame.setJMenuBar(viewMenuBar);
		
		
		JLabel imgLabel = new JLabel(new ImageIcon("C:\\Users\\Owner\\Pictures\\deadbolt.png"));

		imgLabel.setForeground(Color.white);
		imgLabel.setFont(new Font("Verdana", Font.PLAIN, 13));
	    imgLabel.setPreferredSize(new Dimension(100, 100));
	    imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
	    
	    
	    JButton chooseFile = new JButton("Choose File");
	    chooseFile.setPreferredSize(new Dimension(10, 20));
	    chooseFile.setAlignmentX(Component.CENTER_ALIGNMENT);
    
	    
		JLabel selectedFile = new JLabel("Selected File:");
		selectedFile.setAlignmentX(Component.CENTER_ALIGNMENT);
		selectedFile.setForeground(Color.white);
		selectedFile.setFont(new Font("Verdana", Font.PLAIN, 13));
		selectedFile.setPreferredSize(new Dimension(100, 100));

		
	    chooseFile.addActionListener(new ActionListener() {
	          @Override
	          public void actionPerformed(ActionEvent e) {
	             JFileChooser fileChooser = new JFileChooser();
	             fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	             int option = fileChooser.showOpenDialog(mainFrame);
	             if(option == JFileChooser.APPROVE_OPTION){
	                boltFile = fileChooser.getSelectedFile();
	                saveDir = fileChooser.getCurrentDirectory();
	                selectedFile.setText("File Selected: " + boltFile.getName());
	             }
	          }
	       });
	  
	    
	    
	    JButton openFile = new JButton("Open");
	    openFile.setPreferredSize(new Dimension(10, 20));
	    openFile.setAlignmentX(Component.CENTER_ALIGNMENT);
	  
	    
	    openFile.addActionListener(new ActionListener() {
	          @Override
	          public void actionPerformed(ActionEvent e) {
	        	  	String keyInput = JOptionPane.showInputDialog("Encryption Key:");
	        	  
	        	  
		        	 char[] key = new char[keyInput.length()];
		        	 
		        	 for(int i = 0; i < keyInput.length();i++) {
		        		 key[i] = keyInput.charAt(i);
		        	 }
		        	 
	        	 	ByteArrayOutputStream outSalt = new ByteArrayOutputStream();
	        	 	char firstSaltChar = key[key.length-1];
	        	 	char secSaltChar = key[0];
	        	
	        	    while(outSalt.size() < 16) {
	        	    	outSalt.write((byte)secSaltChar);
	        	    	outSalt.write((byte)firstSaltChar);
	        	    }
	        	    
	        	    byte[] salt = outSalt.toByteArray();
	        	 	
	        	 	ByteArrayOutputStream out = new ByteArrayOutputStream();
	        	 	char firstIVChar = key[key.length-1];
	        	 	char secIVChar = key[0];
	        	
	        	    while(out.size() < 16) {
	        	    	out.write((byte)firstIVChar);
	        	    	out.write((byte)secIVChar);
	        	    }
	        	    
	        	    byte[] iv = out.toByteArray();
	        	    
	        	    
	        	    try {
	        	    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        	    	KeySpec spec = new PBEKeySpec(key, salt, 65536, 256);
	        	    	SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	        	 	
	        	    	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        	    	cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
	        	    	FileInputStream inputStream = new FileInputStream(boltFile);
	        	    
	        	    
	        	    	FileOutputStream outputStream = new FileOutputStream(saveDir+"Unencrypted"+".json");
	        	    	byte[] buffer = new byte[64];
	        	    	int bytesRead;
	        	    	while ((bytesRead = inputStream.read(buffer)) != -1) {
	        	    		byte[] output = cipher.update(buffer, 0, bytesRead);
	        	    		if (output != null) {
	        	    			outputStream.write(output);
	        	    		}
	        	    	}
	        	    	byte[] outputBytes = cipher.doFinal();
	        	    	if (outputBytes != null) {
	        	    		outputStream.write(outputBytes);
	        	    	}
	        	    	inputStream.close();
	        	    	outputStream.close();
	        	    
	        	    	}
	        	    	catch(Exception e2){
	        	    		e2.printStackTrace();
	        	    	}	        	    
	        	    	
	        	    
	        	    	JSONParser parser = new JSONParser();
	        	    	Object dataFromFileObject;
	        	    	JSONObject fileData;
	        	    	
	        	    	try {
							dataFromFileObject =  parser.parse(new FileReader(saveDir+"Unencrypted"+".json"));
							fileData = (JSONObject)dataFromFileObject;
							
							JFrame openFrame = new JFrame("Create Bolt");
							openFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							openFrame.setSize(400,400);
							openFrame.setResizable(false);
							openFrame.getContentPane().setBackground(new Color(161, 16, 16));
							
							DefaultTableModel tableModel = new DefaultTableModel();
							JTable table = new JTable(tableModel);
							tableModel.addColumn("Service");
							tableModel.addColumn("Username");
							tableModel.addColumn("Password");
							
							for(int i =0; i < fileData.size();i++) {

								String values=fileData.get("Entry"+String.valueOf(i)).toString().replace("[", "").replace("]", "");
								String[] fillRow = values.split(",");
								tableModel.insertRow(0, new Object[] { fillRow[0],fillRow[1],fillRow[2]});		
							}

							
							JScrollPane scrollPane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED	);
							scrollPane.setMaximumSize(new Dimension(400, 200));
							
							openFrame.add(scrollPane);
							
							
							
							openFrame.setVisible(true);
							
							
						} catch (Exception e1) {
							e1.printStackTrace();
						} 
	        	    	
	        	    	
	        	    	File unencryptedTempFile = new File(saveDir+"Unencrypted"+".json");
	        	    
	        	    	unencryptedTempFile.delete();
	          }
	       });
	    
	   
	    
	    
	    mainFrame.getContentPane().setBackground(new Color(161, 16, 16));
	    mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));
	    mainFrame.add(imgLabel);
	    mainFrame.add(Box.createVerticalStrut(5));
	    mainFrame.add(chooseFile);
	    mainFrame.add(Box.createVerticalStrut(5));
	    mainFrame.add(selectedFile);
	    mainFrame.add(Box.createVerticalStrut(5));
	    mainFrame.add(openFile);
	    mainFrame.setVisible(true);
	    

		
	}
}
