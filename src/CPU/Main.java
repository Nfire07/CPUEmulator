package src.CPU;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.State;

public class Main {
    public static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    public static JLabel[][] registers = new JLabel[12][2];
    public static JLabel errorLabel = new JLabel("");
    public static JLabel[][] flagRegistersLable = new JLabel[2][4];
    
    public static JLabel currentInstructionHex = new JLabel();
    public static JLabel currentInstructionLitteral = new JLabel();
    public static JLabel registerUsed1 = new JLabel();
    public static JLabel registerUsed2 = new JLabel();
    
    public static Color windowBackgroundColor = Color.decode("#0f3834");
    public static Color labelBackgroundColor = Color.decode("#0e2121");
    public static Color borderColor = Color.decode("#121c1b");
    public static Color labelForegroundColor = Color.WHITE;
    

    public static void clearScreen() {
        for (int i = 0; i < 18; i++) {
            System.out.println();
        }
    }
    
    public static Color BrighterColor(Color color) {
    	int red = (int) (color.getRed() + (255 - color.getRed()) / 80.0);
        int green = (int) (color.getGreen() + (255 - color.getGreen()) / 80.0);
        int blue = (int) (color.getBlue() + (255 - color.getBlue()) / 80.0);

        red = Math.min(255, red);
        green = Math.min(255, green);
        blue = Math.min(255, blue);

        return new Color(red, green, blue);
    }
    
    public static Color DarkerColor(Color color) {
        int red = color.getRed() - color.getRed() / 10;
        int green = color.getGreen() - color.getGreen() / 10;
        int blue = color.getBlue() - color.getBlue() / 10;

        red = Math.max(0, red);
        green = Math.max(0, green);
        blue = Math.max(0, blue);

        return new Color(red, green, blue);
    }

    public static Image imageLoader(String filePath) {
		Image i = null;
		try {
			i=ImageIO.read(new File(filePath));
		}catch(IOException e){
			System.out.println("File not found	" + filePath);
		}
		return i;
	}
    
    public static void main(String[] args) {
        CPU.setEmulatedFrequency(10);
        RAM.setRAMSize(65536);
        
       
        
        JFrame window = new JFrame("CPU Emulator");
        window.setBounds(screen.width / 2 - 400, screen.height / 2 - 400, 800, 800);
        window.setLayout(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(windowBackgroundColor);
        window.setAlwaysOnTop(false);
        window.setIconImage(imageLoader("./assets/CPU_ICON.png"));
        
        JLabel title = new JLabel("CPU Emulator");
        title.setBounds(355,50,300,30);
        title.setBackground(windowBackgroundColor);
        title.setFont(new Font("Arial", Font.PLAIN, 40));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setForeground(labelForegroundColor);
        title.setOpaque(true);
        window.add(title);
        
        JLabel registersLabel = new JLabel("Registers");
        registersLabel.setBounds(100,20,100,30);
        registersLabel.setBackground(windowBackgroundColor);
        registersLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        registersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registersLabel.setVerticalAlignment(SwingConstants.CENTER);
        registersLabel.setForeground(labelForegroundColor);
        registersLabel.setOpaque(true);
        window.add(registersLabel);
        
        
        for (int i = 0; i < registers.length; i++) {
            for (int j = 0; j < registers[i].length; j++) {
                registers[i][j] = new JLabel();
                registers[i][j].setBounds(50 + (j * 100), 50 + (i * 50), 100, 50);
                registers[i][j].setBorder(BorderFactory.createLineBorder(borderColor, 1));
                registers[i][j].setBackground(labelBackgroundColor);
                if(j==0)
                	registers[i][j].setFont(new Font("Arial", Font.PLAIN, 25));
                if(j==1)
                	registers[i][j].setFont(new Font("Arial", Font.PLAIN, 20));
                registers[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                registers[i][j].setVerticalAlignment(SwingConstants.CENTER);
                registers[i][j].setForeground(labelForegroundColor);
                registers[i][j].setOpaque(true);
                window.add(registers[i][j]);
            }
            labelBackgroundColor = BrighterColor(labelBackgroundColor);
        }

        for (int i = 0; i < flagRegistersLable.length; i++) {
            for (int j = 0; j < flagRegistersLable[i].length; j++) {
                flagRegistersLable[i][j] = new JLabel();
                flagRegistersLable[i][j].setBounds(375 + (j * 70), 550 + (i * 50), 50, 50);
                flagRegistersLable[i][j].setBorder(BorderFactory.createLineBorder(borderColor, 1));
                flagRegistersLable[i][j].setBackground(labelBackgroundColor);
                flagRegistersLable[i][j].setFont(new Font("Arial", Font.PLAIN, 20));
                flagRegistersLable[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                flagRegistersLable[i][j].setVerticalAlignment(SwingConstants.CENTER);
                flagRegistersLable[i][j].setForeground(labelForegroundColor);
                flagRegistersLable[i][j].setOpaque(true);
                window.add(flagRegistersLable[i][j]);
            }
            labelBackgroundColor = BrighterColor(labelBackgroundColor);
        }
        
        
        JLabel sliderLabel = new JLabel("10KHz");
        sliderLabel.setBounds(600, 150, 80, 50);
        sliderLabel.setBackground(windowBackgroundColor);
        sliderLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        sliderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sliderLabel.setVerticalAlignment(SwingConstants.CENTER);
        sliderLabel.setForeground(labelForegroundColor);
        sliderLabel.setOpaque(true);
        window.add(sliderLabel);
        

        JLabel frequencyLabel = new JLabel("Frequency");
        frequencyLabel.setBounds(450,125,100,30);
        frequencyLabel.setBackground(windowBackgroundColor);
        frequencyLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        frequencyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frequencyLabel.setVerticalAlignment(SwingConstants.CENTER);
        frequencyLabel.setForeground(labelForegroundColor);
        frequencyLabel.setOpaque(true);
        window.add(frequencyLabel);
        
        JSlider slider = new JSlider(1, (int)CPU.REAL_CPU_FREQUENCY, 1);
        slider.setValue(10);
        slider.setBounds(400,150,200,50);
        slider.setMajorTickSpacing(10); 
        slider.setMinorTickSpacing(1);
        slider.setBackground(windowBackgroundColor);
        slider.setFont(new Font("Arial", Font.PLAIN, 12));
        slider.addChangeListener(e ->{
        	sliderLabel.setText(""+slider.getValue()+"KHz");
        	CPU.setEmulatedFrequency(slider.getValue());
        });
        window.add(slider);
        
        currentInstructionHex.setBounds(400,200,100,50);
        currentInstructionHex.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        currentInstructionHex.setBackground(labelBackgroundColor);
        currentInstructionHex.setFont(new Font("Arial", Font.PLAIN, 17));
        currentInstructionHex.setHorizontalAlignment(SwingConstants.CENTER);
        currentInstructionHex.setVerticalAlignment(SwingConstants.CENTER);
        currentInstructionHex.setForeground(labelForegroundColor);
        currentInstructionHex.setOpaque(true);
        window.add(currentInstructionHex);
        labelBackgroundColor = BrighterColor(labelBackgroundColor);
        
        currentInstructionLitteral.setBounds(500,200,100,50);
        currentInstructionLitteral.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        currentInstructionLitteral.setBackground(labelBackgroundColor);
        currentInstructionLitteral.setFont(new Font("Arial", Font.PLAIN, 17));
        currentInstructionLitteral.setHorizontalAlignment(SwingConstants.CENTER);
        currentInstructionLitteral.setVerticalAlignment(SwingConstants.CENTER);
        currentInstructionLitteral.setForeground(labelForegroundColor);
        currentInstructionLitteral.setOpaque(true);
        window.add(currentInstructionLitteral);
        labelBackgroundColor = BrighterColor(labelBackgroundColor);
        
        registerUsed1.setBounds(400,250,100,50);
        registerUsed1.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        registerUsed1.setBackground(labelBackgroundColor);
        registerUsed1.setFont(new Font("Arial", Font.PLAIN, 17));
        registerUsed1.setHorizontalAlignment(SwingConstants.CENTER);
        registerUsed1.setVerticalAlignment(SwingConstants.CENTER);
        registerUsed1.setForeground(labelForegroundColor);
        registerUsed1.setOpaque(true);
        window.add(registerUsed1);
        labelBackgroundColor = BrighterColor(labelBackgroundColor);
        
        registerUsed2.setBounds(500,250,100,50);
        registerUsed2.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        registerUsed2.setBackground(labelBackgroundColor);
        registerUsed2.setFont(new Font("Arial", Font.PLAIN, 17));
        registerUsed2.setHorizontalAlignment(SwingConstants.CENTER);
        registerUsed2.setVerticalAlignment(SwingConstants.CENTER);
        registerUsed2.setForeground(labelForegroundColor);
        registerUsed2.setOpaque(true);
        window.add(registerUsed2);
        labelBackgroundColor = BrighterColor(labelBackgroundColor);
        
        final RAMViewer[] viewer = new RAMViewer[1];
  
        JButton viewMemoryButton = new JButton("VIEW RAM");
        viewMemoryButton.setBounds(400,300,100,50);
        viewMemoryButton.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        viewMemoryButton.setRolloverEnabled(true);
        viewMemoryButton.setFocusPainted(false);
        viewMemoryButton.setBackground(labelBackgroundColor);
        viewMemoryButton.setFont(new Font("Arial", Font.PLAIN, 17));
        viewMemoryButton.setHorizontalAlignment(SwingConstants.CENTER);
        viewMemoryButton.setVerticalAlignment(SwingConstants.CENTER);
        viewMemoryButton.setForeground(labelForegroundColor);
        viewMemoryButton.setOpaque(true);
        viewMemoryButton.addActionListener(e -> {
        	if(viewer[0]==null || viewer[0].window.isVisible()==false) {
        		viewer[0] = new RAMViewer();
        		viewer[0].start();
        	}
        });
        window.add(viewMemoryButton);
        
        final CPU[] Cpu = new CPU[1];
        Cpu[0] = new CPU();
        new RAM();
        
        
		errorLabel.setBounds(600,300,200,30);
		errorLabel.setBackground(windowBackgroundColor);
		errorLabel.setFont(new Font("Arial", Font.BOLD, 17));
		errorLabel.setVerticalAlignment(SwingConstants.CENTER);
		errorLabel.setForeground(Color.RED);
		errorLabel.setOpaque(true);
        window.add(errorLabel);
        
        JButton startCPUButton = new JButton("START CPU");
        startCPUButton.setBounds(500,300,100,50);
        startCPUButton.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        startCPUButton.setRolloverEnabled(true);
        startCPUButton.setFocusPainted(false);
        startCPUButton.setBackground(labelBackgroundColor);
        startCPUButton.setFont(new Font("Arial", Font.PLAIN, 17));
        startCPUButton.setHorizontalAlignment(SwingConstants.CENTER);
        startCPUButton.setVerticalAlignment(SwingConstants.CENTER);
        startCPUButton.setForeground(labelForegroundColor);
        startCPUButton.setOpaque(true);
        startCPUButton.addActionListener(e -> {
        	if(CPU.halted==true && !Cpu[0].isAlive() && Cpu[0].getState()!=State.TERMINATED) {
        		Cpu[0].start();
        		CPU.halted=false;
        		errorLabel.setText("");
        	}
        	else {
        		errorLabel.setText("RESET CPU FIRST");
        	}
        });
        window.add(startCPUButton);
        labelBackgroundColor = BrighterColor(labelBackgroundColor);
        
        JButton resetCPUButton = new JButton("RESET CPU");
        resetCPUButton.setBounds(400,350,100,50);
        resetCPUButton.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        resetCPUButton.setRolloverEnabled(true);
        resetCPUButton.setFocusPainted(false);
        resetCPUButton.setBackground(labelBackgroundColor);
        resetCPUButton.setFont(new Font("Arial", Font.PLAIN, 17));
        resetCPUButton.setHorizontalAlignment(SwingConstants.CENTER);
        resetCPUButton.setVerticalAlignment(SwingConstants.CENTER);
        resetCPUButton.setForeground(labelForegroundColor);
        resetCPUButton.setOpaque(true);
        resetCPUButton.addActionListener(e -> {
        	if(CPU.halted==true) {
        		Cpu[0]=new CPU();
        		Cpu[0].reset();
        		errorLabel.setText("");
        	}else {
        		errorLabel.setText("WAIT UNTIL CPU HALT");
        	}
        });
        window.add(resetCPUButton);
        labelBackgroundColor = BrighterColor(labelBackgroundColor);
        
        
        final EditorASM editor[] = new EditorASM[1];
        JButton editorASMButton = new JButton("EDIT ASM");
        editorASMButton.setBounds(500,350,100,50);
        editorASMButton.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        editorASMButton.setRolloverEnabled(true);
        editorASMButton.setFocusPainted(false);
        editorASMButton.setBackground(labelBackgroundColor);
        editorASMButton.setFont(new Font("Arial", Font.PLAIN, 17));
        editorASMButton.setHorizontalAlignment(SwingConstants.CENTER);
        editorASMButton.setVerticalAlignment(SwingConstants.CENTER);
        editorASMButton.setForeground(labelForegroundColor);
        editorASMButton.setOpaque(true);
        editorASMButton.addActionListener(e -> {
        	if (editor[0] == null || editor[0].window.isVisible()==false) {
                editor[0] = new EditorASM();
                editor[0].start();
            }else {
            	errorLabel.setText("ALREADY EDITING");
            }
        });
        window.add(editorASMButton);
        labelBackgroundColor = BrighterColor(labelBackgroundColor);
        
        JButton compilerButton = new JButton("COMPILE");
        compilerButton.setBounds(400,400,100,50);
        compilerButton.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        compilerButton.setRolloverEnabled(true);
        compilerButton.setFocusPainted(false);
        compilerButton.setBackground(labelBackgroundColor);
        compilerButton.setFont(new Font("Arial", Font.PLAIN, 17));
        compilerButton.setHorizontalAlignment(SwingConstants.CENTER);
        compilerButton.setVerticalAlignment(SwingConstants.CENTER);
        compilerButton.setForeground(labelForegroundColor);
        compilerButton.setOpaque(true);
        compilerButton.addActionListener(e -> {
        	new ASMCompiler().Compile();
        });
        window.add(compilerButton);
        labelBackgroundColor = BrighterColor(labelBackgroundColor);
        
        JButton stopButton = new JButton("STOP CPU");
        stopButton.setBounds(500,400,100,50);
        stopButton.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        stopButton.setRolloverEnabled(true);
        stopButton.setFocusPainted(false);
        stopButton.setBackground(labelBackgroundColor);
        stopButton.setFont(new Font("Arial", Font.PLAIN, 17));
        stopButton.setHorizontalAlignment(SwingConstants.CENTER);
        stopButton.setVerticalAlignment(SwingConstants.CENTER);
        stopButton.setForeground(labelForegroundColor);
        stopButton.setOpaque(true);
        stopButton.addActionListener(e -> {
        	if(!CPU.halted) {
	        	CPU.halted=true;
	        	errorLabel.setText("CPU HALTING");
        	}
        });
        window.add(stopButton);
        labelBackgroundColor = BrighterColor(labelBackgroundColor);
        
        
        JLabel currentFileLabel = new JLabel("Currently working on " + EditorASM.currentlyWorkingOn);
        currentFileLabel.setBounds(325,450,350,20);
        currentFileLabel.setBackground(windowBackgroundColor);
        currentFileLabel.setFont(new Font("Arial", Font.BOLD, 17));
        currentFileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentFileLabel.setVerticalAlignment(SwingConstants.CENTER);
        currentFileLabel.setForeground(EditorASM.registerColor);
        currentFileLabel.setOpaque(true);
        window.add(currentFileLabel);
        
        JLabel ramSizeLabel = new JLabel("RAM size is " + (double)(RAM.getRAMSize()*4)/(double)1000+"KB");
        ramSizeLabel.setBounds(325,470,350,20);
        ramSizeLabel.setBackground(windowBackgroundColor);
        ramSizeLabel.setFont(new Font("Arial", Font.BOLD, 17));
        ramSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ramSizeLabel.setVerticalAlignment(SwingConstants.CENTER);
        ramSizeLabel.setForeground(EditorASM.numberColor);
        ramSizeLabel.setOpaque(true);
        window.add(ramSizeLabel);






        window.setVisible(true);
        
        
        while(true) {
        	window.repaint();
        	currentFileLabel.setText("Currently working on " + EditorASM.currentlyWorkingOn);
        	try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
        }

    }
}