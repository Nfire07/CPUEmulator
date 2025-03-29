package src.CPU;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import src.CPU.RequireFilename.FileNameListener;


public class FileSelector extends Thread{
	public JFrame fileSelectorWindow = new JFrame("Editor");
	boolean isFilenameEntered = false;
    String enteredFilename = null;
	
	String folder;
	private FileNameListener fileNameListener;
	
	public interface FileNameListener {
        void onFileNameEntered(String filename);
    }
	
	public FileSelector(FileNameListener listener,String folder) {
        this.fileNameListener = listener;
        this.folder=folder;
    }
	
    @Override
    public void run() { 
    	fileSelectorWindow.setBounds(Main.screen.width / 2 - 150, Main.screen.height / 2 - 100, 300, 200);
        fileSelectorWindow.setLayout(null);
        fileSelectorWindow.setResizable(false);
        fileSelectorWindow.getContentPane().setBackground(EditorASM.editorBackgroundColor);
        fileSelectorWindow.setAlwaysOnTop(false);
        fileSelectorWindow.setIconImage(Main.imageLoader("./assets/CPU_ICON.png"));

        JLabel insertFilenameLabel;
        if(folder.equals("."))
        	insertFilenameLabel = new JLabel("Files in current folder");
        else
        	insertFilenameLabel = new JLabel("Files in folder " + folder);
        
        insertFilenameLabel.setBounds(10, 0, 300, 30);
        insertFilenameLabel.setBackground(EditorASM.editorBackgroundColor);
        insertFilenameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        insertFilenameLabel.setVerticalAlignment(SwingConstants.CENTER);
        insertFilenameLabel.setForeground(EditorASM.instructionColor);
        insertFilenameLabel.setOpaque(true);
        fileSelectorWindow.add(insertFilenameLabel);

        
        
        
        File currentFolderFile = new File(folder);
        File[] files = null;
        if(currentFolderFile.exists() && currentFolderFile.isDirectory())
        	files = currentFolderFile.listFiles();
        
        ArrayList<String> filenames = new ArrayList<>();
        for(int i=0;i<files.length;i++) {
        	if(files[i].getName().endsWith(".asm")) {
        		filenames.add(files[i].getName());
        	}	
        }
        
        JComboBox<String> fileList = new JComboBox<>((String[])filenames.toArray(new String[0]));
        fileList.setBounds(10, 30, 265, 30);
        fileList.setBackground(EditorASM.editorBackgroundColor.brighter());
        fileList.setForeground(EditorASM.instructionColor);
        fileList.setBorder(null);
        fileList.setFocusable(false);
        fileList.setEditable(false);
        
        
        
        fileList.addActionListener(e ->{
        	enteredFilename=(String) fileList.getSelectedItem();
        	fileSelectorWindow.dispose();
        	isFilenameEntered = true;
        	if (fileNameListener != null) {
                SwingUtilities.invokeLater(() -> fileNameListener.onFileNameEntered(enteredFilename));
            }
        });
        
        fileSelectorWindow.add(fileList);
        fileSelectorWindow.setVisible(true);

        while (!isFilenameEntered) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getEnteredFilename() {
        return enteredFilename;
    }
}