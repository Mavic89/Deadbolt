import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CreateBolt {

	private static File createDir;
	private static File tempFile;
	
	
	public static void CreateBolt() {
		
		JFrame createBoltFrame = new JFrame("Create Bolt");
		createBoltFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		createBoltFrame.setSize(400,400);
		createBoltFrame.setResizable(false);
		createBoltFrame.getContentPane().setBackground(new Color(161, 16, 16));
		
		
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
		
		
		DefaultTableModel tableModel = new DefaultTableModel();
		JTable table = new JTable(tableModel);
		tableModel.addColumn("Service");
		tableModel.addColumn("Username");
		tableModel.addColumn("Password");
		tableModel.insertRow(0, new Object[] { "" ,"" ,"" });
		JScrollPane scrollPane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED	);
		scrollPane.setMaximumSize(new Dimension(400, 200));
		
		tablePanel.add(scrollPane);
		tablePanel.setPreferredSize(new Dimension(400,200));
		
		
		
		JPanel middlePanel = new JPanel();
		
		JButton addRow = new JButton("+");
		JButton deleteRow = new JButton("-");
		JLabel fileNameLabel = new JLabel("File Name:");
		fileNameLabel.setForeground(Color.white);
		JTextArea fileName = new JTextArea("BoltFile");
		fileName.setPreferredSize(new Dimension(100,20));
		addRow.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		addRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.insertRow((int)table.getRowCount(), new Object[] { "" ,"" ,"" });
			}
		});
		
		deleteRow.setAlignmentX(Component.CENTER_ALIGNMENT);
		deleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.removeRow((int)table.getRowCount()-1);
			}
		});
		middlePanel.add(addRow);
		middlePanel.add(deleteRow);
		middlePanel.add(fileNameLabel);
		middlePanel.add(fileName);
		

		
		
		JPanel bottomPanel = new JPanel();
		JButton ChooseDir = new JButton("Save Location...");
		
		
	    ChooseDir.addActionListener(new ActionListener() {
	          @Override
	          public void actionPerformed(ActionEvent e) {
	             JFileChooser fileChooser = new JFileChooser();
	             fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	             int option = fileChooser.showOpenDialog(createBoltFrame);
	             if(option == JFileChooser.APPROVE_OPTION){
	                createDir = fileChooser.getSelectedFile();
	                
	             }
	          }
	       });
	    
	    
		JButton Save = new JButton("Create");
	    Save.addActionListener(new ActionListener() {
	          @Override
	          public void actionPerformed(ActionEvent e) {
	        	  String nameOfFile = fileName.getText();
	        	  
	        	  ArrayList<ArrayList<String>> rowData = new ArrayList<ArrayList<String>>();
	        	  
	        	  for (int i = 0; i < table.getRowCount(); i++){
	        		  ArrayList<String> row = new ArrayList<String>();
	        		  row.add(table.getValueAt(i,0).toString());
	        		  row.add(table.getValueAt(i,1).toString());
	        		  row.add(table.getValueAt(i,2).toString());
	        		  rowData.add(row);
	        	  }
	        	 
	        	 JSONObject jsonData = new JSONObject();
	        	 for(int j = 0; j < rowData.size();j++) {
	        		 jsonData.put("Entry"+String.valueOf(j), rowData.get(j));
	        	 }
	        	 
	        	 
	        	 try {
	        		tempFile = new File(createDir.toString()+"\\"+"tempFile"+".json");
					FileWriter writeFile = new FileWriter(tempFile);
					writeFile.write(jsonData.toJSONString());
					writeFile.close();
	        	 	} catch (IOException e1) {
					e1.printStackTrace();
	        	 }

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
	        	    	cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
	        	    	FileInputStream inputStream = new FileInputStream(tempFile);
	        	    	System.out.println(createDir);
	        	    	FileOutputStream outputStream = new FileOutputStream(createDir.toString()+"\\"+nameOfFile+".json");
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
	        	 
	        	 //Run garbage collector to clear key from heap
	        	    	key=null;
	        	    	System.gc();
	        	    	tempFile.delete();
	        	 
	          		}
	       		});
	    
	    
		ChooseDir.setAlignmentX(Component.CENTER_ALIGNMENT);
		Save.setAlignmentX(Component.CENTER_ALIGNMENT);
		bottomPanel.add(ChooseDir);
		bottomPanel.add(Save);
		middlePanel.setBackground(new Color(161, 16, 16));
		
		
		createBoltFrame.getContentPane().add(BorderLayout.NORTH,tablePanel);
		createBoltFrame.getContentPane().add(BorderLayout.CENTER,middlePanel);	
		createBoltFrame.getContentPane().add(BorderLayout.SOUTH,bottomPanel);	
		createBoltFrame.setVisible(true);
	}
}
