package src.CPU;

import java.awt.Font;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class RequireFilename extends Thread {
    public JFrame requirerWindow = new JFrame("Editor");
    boolean isFilenameEntered = false;
    String enteredFilename = null;
    String currentFile;
    private FileNameListener fileNameListener;

    public interface FileNameListener {
        void onFileNameEntered(String filename);
    }

    public RequireFilename(FileNameListener listener,String currentFile) {
        this.fileNameListener = listener;
        this.currentFile=currentFile;
    }

    @Override
    public void run() {
        requirerWindow.setBounds(Main.screen.width / 2 - 150, Main.screen.height / 2 - 50, 300, 130);
        requirerWindow.setLayout(null);
        requirerWindow.setResizable(false);
        requirerWindow.getContentPane().setBackground(EditorASM.editorBackgroundColor);
        requirerWindow.setAlwaysOnTop(false);
        requirerWindow.setIconImage(Main.imageLoader("./assets/CPU_ICON.png"));

        JLabel insertFilenameLabel = new JLabel("Standard Filename template.asm");
        insertFilenameLabel.setBounds(10, 0, 300, 30);
        insertFilenameLabel.setBackground(EditorASM.editorBackgroundColor);
        insertFilenameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        insertFilenameLabel.setVerticalAlignment(SwingConstants.CENTER);
        insertFilenameLabel.setForeground(EditorASM.instructionColor);
        insertFilenameLabel.setOpaque(true);
        requirerWindow.add(insertFilenameLabel);

        JTextPane filenameRequirer = new JTextPane();
        filenameRequirer.setBackground(EditorASM.editorBackgroundColor);
        filenameRequirer.setForeground(EditorASM.instructionColor);
        filenameRequirer.setFont(new Font("Monospaced", Font.PLAIN, 17));
        filenameRequirer.setCaretColor(EditorASM.instructionColor);
        filenameRequirer.setText(currentFile);
        filenameRequirer.setCaretPosition(currentFile.length());
        filenameRequirer.setBounds(10, 40, 260, 30);
        filenameRequirer.setBorder(BorderFactory.createLineBorder(EditorASM.standardColor));

        filenameRequirer.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {}

            @Override
            public void insertUpdate(DocumentEvent e) {
            	if (filenameRequirer.getText().endsWith("\n")) {
                    enteredFilename = filenameRequirer.getText().trim();
                    if (!enteredFilename.endsWith(".asm")) {
                        enteredFilename += ".asm";
                    }
                    isFilenameEntered = true;
                    requirerWindow.dispose();

                    if (fileNameListener != null) {
                        SwingUtilities.invokeLater(() -> fileNameListener.onFileNameEntered(enteredFilename));
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        requirerWindow.add(filenameRequirer);
        requirerWindow.setVisible(true);

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
