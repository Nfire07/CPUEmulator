package src.CPU;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.CharBuffer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;


public class EditorASM extends Thread {
    public static Color editorBackgroundColor = Color.decode("#0C0C0C");
    public static Color commentColor = Color.decode("#6A9955");
    public static Color standardColor = Color.decode("#D4D4D4");
    public static Color instructionColor = Color.decode("#569CD6");
    public static Color lineCounterColor = Color.decode("#2a2b2b");
    public static Color registerColor = Color.decode("#6940b8"); 
    public static Color numberColor = Color.decode("#4ea364"); 
    
    public JFrame window;
    public int zoommer = 0;
    private boolean isProcessing = false; 
    public static String currentlyWorkingOn = "template.asm";
    public EditorASM() {}
    
    final static FileSelector[] fs  = new FileSelector[1];
    final static RequireFilename[] filenameRequirer = new RequireFilename[1];

    public void lineCounterUpdater(JTextPane editor, JTextPane lineCounter) {
        String editorText = editor.getText();
        if (!editorText.endsWith("\n")) {
            editorText += "\n";
        }

        String[] lines = editorText.split("\n");
        String lineNumbers = new String();

        for (int i = 1; i <= lines.length; i++) {
            lineNumbers += i + "\n";
        }

        lineCounter.setText(lineNumbers.toString());
    }
    
    public void highlightComments(String[] lines,StyledDocument editorStyle) {
    	int characters = -1;
    	
        for (int i = 0; i < lines.length; i++) {
            for(int j = 0; j < lines[i].length() ; j++) {
            	characters++;
            	
            	if(lines[i].trim().startsWith(";")) {
        			SimpleAttributeSet commentAttribute = new SimpleAttributeSet();
                    StyleConstants.setForeground(commentAttribute, commentColor);
                    editorStyle.setCharacterAttributes(characters,1, commentAttribute, true);
            	}else {
            		SimpleAttributeSet nonCommentAttribute = new SimpleAttributeSet();
                    StyleConstants.setForeground(nonCommentAttribute, standardColor);
                    editorStyle.setCharacterAttributes(characters,1, nonCommentAttribute, true);
            	}
            }
            characters++;
        }   
    }
    public int getTabCount(String line) {
    	int tabCount=0;
		for(int j=0;j<line.length();j++) {
			if(line.charAt(j)=='\t') tabCount++;
		}
		return tabCount;
    }
    
    
    public void highlightKeyword(String[] lines,StyledDocument editorStyle){
    	int characters = 0;
    	
    	for(int i=0;i<lines.length;i++) {
    		if(!lines[i].trim().startsWith(";")) {
    			if(lines[i].isBlank()) {
	    			characters+=lines[i].length()+1;
	    		}
    			else {
	    			String[] wordsInLine = lines[i].trim().split(" ");
	    			if(lines[i].startsWith("\t")) {
	    				characters+=getTabCount(lines[i]);
	    			}
		    		for(int j=0;j<wordsInLine.length;j++) {
		    			if(CPU.reverseSet.get(wordsInLine[j].toUpperCase())!=null && CPU.reverseRegisters.get(wordsInLine[j].toUpperCase())==null) {
		    				SimpleAttributeSet keywordAttribute = new SimpleAttributeSet();
		                    StyleConstants.setForeground(keywordAttribute, instructionColor);
		                    editorStyle.setCharacterAttributes(characters,wordsInLine[j].length(), keywordAttribute, true);
		    			}
	    				characters += wordsInLine[j].length()+1;
		    		}
		    		if(lines[i].endsWith("\t")) {
	    				characters+=getTabCount(lines[i]);
	    			}
		    		
    			}
    			
    		}
    		else {
    			characters+=lines[i].length()+1; 
    		}
    	}
    }
    
    public void highlightRegisters(String[] lines,StyledDocument editorStyle){
    	int characters = 0;
    	
    	for(int i=0;i<lines.length;i++) {
    		if(!lines[i].trim().startsWith(";")) {
    			if(lines[i].isBlank()) {
	    			characters+=lines[i].length()+1;
	    		}
    			else {
	    			String[] wordsInLine = lines[i].trim().split(" ");
	    			if(lines[i].startsWith("\t")) {
	    				characters+=getTabCount(lines[i]);
	    			}
		    		for(int j=0;j<wordsInLine.length;j++) {
		    			if(CPU.reverseRegisters.get(wordsInLine[j].toUpperCase())!=null) {
		    				SimpleAttributeSet keywordAttribute = new SimpleAttributeSet();
							StyleConstants.setForeground(keywordAttribute, registerColor);
		                    editorStyle.setCharacterAttributes(characters,wordsInLine[j].length(), keywordAttribute, true);
		    			}
		    				characters += wordsInLine[j].length()+1; 
		    		}
		    		if(lines[i].endsWith("\t")) {
	    				characters+=getTabCount(lines[i]);
	    			}
		    		
    			}
    		}
    		else {
    			characters+=lines[i].length()+1; 
    		}
    	}
    }
    
    public void highlightNumbers(String[] lines,StyledDocument editorStyle){
    	int characters = 0;
    	
    	for(int i=0;i<lines.length;i++) {
    		if(!lines[i].trim().startsWith(";")) {
    			if(lines[i].isBlank()) {
	    			characters+=lines[i].length()+1;
	    		}
    			else {
	    			String[] wordsInLine = lines[i].trim().split(" ");
	    			if(lines[i].startsWith("\t")) {
	    				characters+=getTabCount(lines[i]);
	    			}
		    		for(int j=0;j<wordsInLine.length;j++) {
		    			if(isHexadecimal(wordsInLine[j])) {
		    				SimpleAttributeSet keywordAttribute = new SimpleAttributeSet();
							StyleConstants.setForeground(keywordAttribute, numberColor);
		                    editorStyle.setCharacterAttributes(characters,wordsInLine[j].length(), keywordAttribute, true);
		    			}
		    				characters += wordsInLine[j].length()+1; 
		    		}
		    		if(lines[i].endsWith("\t")) {
	    				characters+=getTabCount(lines[i]);
	    			}
		    		
    			}
    		}
    		else {
    			characters+=lines[i].length()+1; 
    		}
    	}
    }
    
    public void highlightSyntax(JTextPane editor) {
        if (isProcessing) return;
        
        isProcessing = true;
        
        SwingUtilities.invokeLater(() -> {
        	applyHighlighting(editor);
            isProcessing = false;
        });
    }
    
    public void applyHighlighting(JTextPane editor) {
            StyledDocument editorStyle = editor.getStyledDocument();
            String[] lines = editor.getText().split("\n");
            highlightComments(lines,editorStyle);
            highlightKeyword(lines,editorStyle);
            highlightRegisters(lines,editorStyle);
            highlightNumbers(lines,editorStyle);
    }
    
    public void openFile(JTextPane editorTextArea,String folder) {
    	editorTextArea.setText("");
    	 
    	FileSelector.FileNameListener fileNameListener = new FileSelector.FileNameListener() {
            @Override
            public void onFileNameEntered(String enteredFileName) {
            	if(enteredFileName.equals(".asm"))
            		enteredFileName = "template.asm";
            	
                File asmFile = new File(enteredFileName);
                currentlyWorkingOn = asmFile.getName();
        		try {
        			BufferedReader r = new BufferedReader(new FileReader(asmFile));
        			CharBuffer buffer = CharBuffer.allocate((int) asmFile.length());
        			r.read(buffer);
        			editorTextArea.setText(buffer.flip().toString());
        			r.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		applyHighlighting(editorTextArea);
            }
        };
        if(fs[0]==null || fs[0].fileSelectorWindow.isVisible()==false) {
        	fs[0] = new FileSelector(fileNameListener,folder);
        	fs[0].start();
        }
	}
    
    public static boolean isHexadecimal(String str) {
        if (str.length() != 4) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c) && !(c >= 'A' && c <= 'F') && !(c >= 'a' && c <= 'f')) {
                return false;
            }
        }

        return true;
    }
    
    //waits until the user saves his file to save it in memory
    
    public static void saveCurrentFile(JTextPane editorTextArea) {
    	
    	
    	RequireFilename.FileNameListener fileNameListener = new RequireFilename.FileNameListener() {
            @Override
            public void onFileNameEntered(String enteredFileName) {
            	if(enteredFileName.equals(".asm"))
            		enteredFileName = "template.asm";
            	
                File asmFile = new File(enteredFileName);
        		try {
        			PrintWriter pw = new PrintWriter(asmFile);
        			pw.write(editorTextArea.getText());
        			pw.close();
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
            }
        };
        
		if(filenameRequirer[0]==null || filenameRequirer[0].requirerWindow.isVisible()==false) {
			filenameRequirer[0] = new RequireFilename(fileNameListener,currentlyWorkingOn);
			filenameRequirer[0].start();
		}
		
    }
    
    public void startWithCurrentFile(JTextPane editorTextArea) {
    	File asmFile = new File(currentlyWorkingOn);
        currentlyWorkingOn = asmFile.getName();
		try {
			BufferedReader r = new BufferedReader(new FileReader(asmFile));
			CharBuffer buffer = CharBuffer.allocate((int) asmFile.length());
			r.read(buffer);
			editorTextArea.setText(buffer.flip().toString());
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		highlightSyntax(editorTextArea);
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            window = new JFrame("ASM Editor");
            window.setBounds(Main.screen.width / 2 - 400, Main.screen.height / 2 - 400, 800, 768);
            window.setLayout(new BorderLayout());
            window.setResizable(false);
            window.getContentPane().setBackground(editorBackgroundColor);
            window.setAlwaysOnTop(false);
            window.setIconImage(Main.imageLoader("./assets/CPU_ICON.png"));
           

            JTextPane editorTextArea = new JTextPane();
            editorTextArea.setBackground(editorBackgroundColor);
            editorTextArea.setForeground(standardColor);
            editorTextArea.setFont(new Font("Monospaced", Font.PLAIN, 17));
            editorTextArea.setCaretColor(standardColor);
            editorTextArea.setText("");
            
            JTextPane lineTextArea = new JTextPane();
            lineTextArea.setBounds(0, 0, 30, window.getHeight());
            lineTextArea.setBackground(editorBackgroundColor);
            lineTextArea.setForeground(lineCounterColor.brighter().brighter());
            lineTextArea.setFont(new Font("Monospaced", Font.ITALIC, 17));
            lineTextArea.setCaretColor(editorBackgroundColor);
            lineTextArea.setEditable(false);
            lineTextArea.setText("");
            
            editorTextArea.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_S){
						saveCurrentFile(editorTextArea);
					}
					if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_O) {
						openFile(editorTextArea,".");
					}
					if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_PLUS) {
						if(zoommer<30) {
							zoommer++;
							editorTextArea.setFont(new Font("Monospaced", Font.PLAIN, 17+zoommer));
							lineTextArea.setFont(new Font("Monospaced", Font.ITALIC, 17+zoommer));
							window.repaint();
						}
					}
					if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_MINUS) {
						if(zoommer>-17) {
							zoommer--;
							editorTextArea.setFont(new Font("Monospaced", Font.PLAIN, 17+zoommer));
							lineTextArea.setFont(new Font("Monospaced", Font.ITALIC, 17+zoommer));
							window.repaint();
						}
					}
				}
			});
            
            

            

            editorTextArea.getStyledDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void removeUpdate(DocumentEvent e) {
                    lineCounterUpdater(editorTextArea, lineTextArea);
                    highlightSyntax(editorTextArea);
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    lineCounterUpdater(editorTextArea, lineTextArea);
                    highlightSyntax(editorTextArea);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    lineCounterUpdater(editorTextArea, lineTextArea);
                    highlightSyntax(editorTextArea);
                }
            });

            JScrollPane editorScrollPane = new JScrollPane(editorTextArea);
            JScrollPane lineCounterScrollPane = new JScrollPane(lineTextArea);

            lineCounterScrollPane.setPreferredSize(new Dimension(40, window.getHeight()));

            editorScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
            lineCounterScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

            editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            lineCounterScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            editorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            lineCounterScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            editorScrollPane.setBackground(editorBackgroundColor);
            lineCounterScrollPane.setBackground(editorBackgroundColor);
            editorScrollPane.setBorder(null);
            lineCounterScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.DARK_GRAY));
            
            editorScrollPane.getVerticalScrollBar().setVisible(false);
            lineCounterScrollPane.getVerticalScrollBar().setVisible(false);
            editorScrollPane.getHorizontalScrollBar().setVisible(false);
            lineCounterScrollPane.getHorizontalScrollBar().setVisible(false);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(lineCounterScrollPane, BorderLayout.WEST);
            panel.add(editorScrollPane, BorderLayout.CENTER);
            window.add(panel);
            
            
            JMenuBar upperMenuBar = new JMenuBar();
            upperMenuBar.setFont(new Font("Monospaced", Font.BOLD, 17));
            upperMenuBar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,commentColor));
            upperMenuBar.setBounds(0,0,window.getWidth(),4);
            upperMenuBar.setBackground(editorBackgroundColor.brighter());
            upperMenuBar.setForeground(standardColor);
            upperMenuBar.setLayout(new FlowLayout(FlowLayout.CENTER,100,0));
           
            JButton saveButton = new JButton("SAVE");
            saveButton.setRolloverEnabled(true);
            saveButton.setFocusPainted(false);
            saveButton.setBorder(null);
            saveButton.setBackground(editorBackgroundColor);
            saveButton.setFont(new Font("Monospace", Font.BOLD, 17));
            saveButton.setHorizontalAlignment(SwingConstants.CENTER);
            saveButton.setVerticalAlignment(SwingConstants.CENTER);
            saveButton.setForeground(standardColor);
            saveButton.setOpaque(true);
            saveButton.addMouseListener(new MouseAdapter() {
           	 @Override
                public void mouseEntered(MouseEvent e) {
           		saveButton.setBackground(editorBackgroundColor);
           		saveButton.setForeground(standardColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                	saveButton.setBackground(editorBackgroundColor);
                	saveButton.setForeground(standardColor);
                }
           	
           	
            });
            saveButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					saveCurrentFile(editorTextArea);
				}
			});         
           
            upperMenuBar.add(saveButton);
            
            JButton openButton = new JButton("OPEN FILE");
            openButton.setRolloverEnabled(true);
            openButton.setFocusPainted(false);
            openButton.setBorder(null);
            openButton.setBackground(editorBackgroundColor);
            openButton.setFont(new Font("Monospace", Font.BOLD, 17));
            openButton.setHorizontalAlignment(SwingConstants.CENTER);
            openButton.setVerticalAlignment(SwingConstants.CENTER);
            openButton.setForeground(standardColor);
            openButton.setOpaque(true);
            openButton.addMouseListener(new MouseAdapter() {
            	 @Override
                 public void mouseEntered(MouseEvent e) {
            		 openButton.setBackground(editorBackgroundColor);
                	 openButton.setForeground(standardColor);
                 }

                 @Override
                 public void mouseExited(MouseEvent e) {
                	 openButton.setBackground(editorBackgroundColor);
                	 openButton.setForeground(standardColor);
                 }
            	
            	
            });
            openButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					openFile(editorTextArea,".");
				}
			});         
            
            upperMenuBar.add(openButton);
            
            
            JButton viewInstructionsButton = new JButton("INSTRUCTION SET");
            viewInstructionsButton.setRolloverEnabled(true);
            viewInstructionsButton.setFocusPainted(false);
            viewInstructionsButton.setBorder(null);
            viewInstructionsButton.setBackground(editorBackgroundColor);
            viewInstructionsButton.setFont(new Font("Monospace", Font.BOLD, 17));
            viewInstructionsButton.setHorizontalAlignment(SwingConstants.CENTER);
            viewInstructionsButton.setVerticalAlignment(SwingConstants.CENTER);
            viewInstructionsButton.setForeground(standardColor);
            viewInstructionsButton.setOpaque(true);
            viewInstructionsButton.addMouseListener(new MouseAdapter() {
            	 @Override
                 public void mouseEntered(MouseEvent e) {
            		 viewInstructionsButton.setBackground(editorBackgroundColor);
                	 viewInstructionsButton.setForeground(standardColor);
                 }

                 @Override
                 public void mouseExited(MouseEvent e) {
                	 viewInstructionsButton.setBackground(editorBackgroundColor);
                	 viewInstructionsButton.setForeground(standardColor);
                 }
            	
            	
            });
            viewInstructionsButton.addActionListener(new ActionListener() {
            	final InstructionSetViewer setViewer[] = new InstructionSetViewer[1];
				
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(setViewer[0]==null || setViewer[0].window.isVisible()==false) {
						setViewer[0] = new InstructionSetViewer();
						setViewer[0].start();
					}
				}
			});         
            
            upperMenuBar.add(viewInstructionsButton);
            
            JButton viewRegistersButton = new JButton("REGISTERS");
            viewRegistersButton.setRolloverEnabled(true);
            viewRegistersButton.setFocusPainted(false);
            viewRegistersButton.setBorder(null);
            viewRegistersButton.setBackground(editorBackgroundColor);
            viewRegistersButton.setFont(new Font("Monospace", Font.BOLD, 17));
            viewRegistersButton.setHorizontalAlignment(SwingConstants.CENTER);
            viewRegistersButton.setVerticalAlignment(SwingConstants.CENTER);
            viewRegistersButton.setForeground(standardColor);
            viewRegistersButton.setOpaque(true);
            viewRegistersButton.addMouseListener(new MouseAdapter() {
            	 @Override
                 public void mouseEntered(MouseEvent e) {
            		 viewRegistersButton.setBackground(editorBackgroundColor);
            		 viewRegistersButton.setForeground(standardColor);
                 }

                 @Override
                 public void mouseExited(MouseEvent e) {
                	 viewRegistersButton.setBackground(editorBackgroundColor);
                	 viewRegistersButton.setForeground(standardColor);
                 }
            	
            	
            });
            viewRegistersButton.addActionListener(new ActionListener() {
				final RegistersViewer regViewer[] = new RegistersViewer[1];
				
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(regViewer[0]==null || regViewer[0].window.isVisible()==false) {
						regViewer[0] = new RegistersViewer();
						regViewer[0].start();
					}
				}
			});         
            
            upperMenuBar.add(viewRegistersButton);
            
            
            
            
            window.setJMenuBar(upperMenuBar);
            startWithCurrentFile(editorTextArea);
            window.repaint();
            window.setVisible(true);
            
        });
    }
}