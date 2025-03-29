package src.CPU;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CPU extends Thread{
	
	public static boolean halted=false;
	public static final double REAL_CPU_FREQUENCY = 35000; // 3.5 Ghz written as 35000 Mhz
	public static double emulatedFrequency = 10; // 3.5 Ghz written as 35000 Mhz
	
	String currentInstructionHex="XXXX";
	String instruction="XXXX";
	int reg1;
	int reg2;
	int immediate;
	boolean possibleOverflow = false;
	//registers
	int IP=0;
	int SP=0;

	int[] r = new int[9];
	int P1=0;
	int P2=0;


	boolean[] flagRegister = new boolean[4];

	public static HashMap<String,String> instructionSet = new HashMap<>();
	public static HashMap<String,String> reverseSet = new HashMap<>();
	public static HashMap<String,String> registers = new HashMap<>(); 
	public static HashMap<String,String> reverseRegisters = new HashMap<>();
	
	
	public static void setEmulatedFrequency(double frequency) {
		emulatedFrequency=frequency;
	}
	
	public void setRegistersMap() {
		// registers
		registers.put("0", "NULL");
		registers.put("1", "R1");
		registers.put("2", "R2");
		registers.put("3", "R3");
		registers.put("4", "R4");
		registers.put("5", "R5");
		registers.put("6", "R6");
		registers.put("7", "R7");
		registers.put("8", "R8");
		registers.put("9", "IP");
		registers.put("A", "SP");
		registers.put("B", "P1");
		registers.put("C", "P2");
		
		
	    for (Map.Entry<String, String> entry : registers.entrySet()) {
           reverseRegisters.put(entry.getValue(), entry.getKey());
	    }
		
		
	}
	
	public void setInstructionSetMap() {
		// instructions
		instructionSet.put("00", "NOP");
		instructionSet.put("01", "LD");
		instructionSet.put("02", "LDFM");
		instructionSet.put("03", "LDI");
		instructionSet.put("04", "ADD");
		instructionSet.put("05", "INC");
		instructionSet.put("06", "SUB");
		instructionSet.put("07", "DEC");
		instructionSet.put("08", "MUL");
		instructionSet.put("09", "DIV");
		instructionSet.put("0A", "ADDI");
		instructionSet.put("0B", "SUBI");
		instructionSet.put("0C", "MULI");
		instructionSet.put("0D", "DIVI");
		instructionSet.put("0E", "LDIM");
		instructionSet.put("0F", "LDCI");
		instructionSet.put("10", "LDNZI");
		instructionSet.put("11", "LDZI");
		instructionSet.put("12", "LDNSI");
		instructionSet.put("13", "LDSI");
		instructionSet.put("FF", "HALT");
		
		
		for (Map.Entry<String, String> entry : instructionSet.entrySet()) {
			reverseSet.put(entry.getValue(), entry.getKey());
		}
		
		reverseSet.putAll(reverseRegisters);
	
	}
	
	public CPU() {
		CPU.halted=true;
		updateGraphicallyRegisters();
		updateGraphicallyCurrentInstruction();

		Arrays.fill(flagRegister,false);

		setRegistersMap();
		setInstructionSetMap();
		
		// graphically setted the registers
		for(int i=0;i<Main.registers.length;i++) {
			Main.registers[i][0].setText(registers.get(Integer.toHexString(i + 1).toUpperCase()));
		}

		Main.flagRegistersLable[0][0].setText("FZ"); // flag zero
		Main.flagRegistersLable[0][1].setText("FO"); // flag overflow
		Main.flagRegistersLable[0][2].setText("FS"); // flag sign
		Main.flagRegistersLable[0][3].setText("FU"); // flag unused


		Arrays.fill(r,0);

	}
	
	/*
	 * simulate the frequency of instruction executions
	 */
	public void clockDelay() {
		try{
			Thread.sleep(Math.round(REAL_CPU_FREQUENCY/emulatedFrequency));
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
		}
	}
	
	/*
	 * get next instruction from memory
	 */
	public void fetch() {
		System.out.println("----CPU FETCHING----");
		
		if(IP==RAM.RAMSize)
			IP=0;
		
		currentInstructionHex = RAM.LNI(IP++);
		System.out.println("IP="+(IP-1)+"	Instruction="+currentInstructionHex);
		updateGraphicallyCurrentInstruction();
		clockDelay();
	}
	
	/*
	 * decode the instruction to prepare the execution
	 */
	public void decode(){
		System.out.println("----CPU DECODING----");
		String hexInstruction = currentInstructionHex.substring(0,2);
		String hexImmediate = currentInstructionHex.substring(4,8);
		try {
			reg1 = Integer.parseInt(""+currentInstructionHex.charAt(2),16);
			reg2 = Integer.parseInt(""+currentInstructionHex.charAt(3),16);
			instruction = instructionSet.get(hexInstruction);
			immediate =  Integer.parseInt(hexImmediate,16);
		}catch(Exception e) {
			currentInstructionHex = "0000";
			reg1 = Integer.parseInt(currentInstructionHex.substring(2,3),16);
			reg2 = Integer.parseInt(currentInstructionHex.substring(3,4),16);
			instruction = instructionSet.get(hexInstruction);
			System.out.println("ERROR:	operation not valid result in a NOP");
		}
		
		System.out.println("operation="+instruction+"	reg1="+registers.get(Integer.toHexString(reg1).toUpperCase())+"	reg2="+registers.get(Integer.toHexString(reg1).toUpperCase()));
		updateGraphicallyCurrentInstruction();
		clockDelay();
	}

	public void setFlagRegisters(){
		if(reg1>=9)
			return;
		flagRegister[0]=(r[reg1]==0); // sets zero register
		flagRegister[1]=(r[reg1]==Integer.MAX_VALUE || r[reg1]==Integer.MIN_VALUE);
		flagRegister[2]=(r[reg1]<0); // sets sign register
	}

	public boolean verifyEveryFlagRegister(){
		for(boolean fr : flagRegister){
			if(fr)
				return true;
		}
		return false;
	}

	public boolean FZ(){
		return flagRegister[0];
	}

	public boolean FS(){
		return flagRegister[2];
	}

	public void ALU() {
		switch (instruction) {
			// add two registers and put the result into the first register
			case "ADD": {
				r[reg1]+=r[reg2];

				break;
			}
			// sub two registers and put the result into the first register
			case "SUB":{
				r[reg1]-=r[reg2];

				break;
			}
			// multiply two registers and put the result into the first register
			case "MUL":{
				r[reg1]*=r[reg2];

				break;
			}
			// divide two registers and put the result into the first register
			case "DIV":{
				try {
					r[reg1]/=r[reg2];
				}catch(ArithmeticException e) {
					r[reg1] = 0;
					r[reg2] = 0;
				}

				break;
			}
			// add a register with an immidiate and put the result into the first register
			case "ADDI": {
				r[reg1]+=immediate;

				break;
			}
			// sub a register with an immidiate and put the result into the first register
			case "SUBI":{
				r[reg1]-=immediate;

				break;
			}
			// mul a register with an immidiate and put the result into the first register
			case "MULI":{
				r[reg1]*=immediate;

				break;
			}
			// div a register with an immidiate and put the result into the first register
			case "DIVI":{
				r[reg1]/=immediate;

				break;
			}
			// inc a register by 1 and put the result into the first register
			case "INC":{
				if(currentInstructionHex.charAt(2) == 'A')
					SP++;
				else
					r[reg1]++;
				break;
			}
			// dec a register by 1 and put the result into the first register
			case "DEC":{
				if(currentInstructionHex.charAt(2) == 'A')
					SP++;
				else
					r[reg1]--;
				break;
			}
		}
		setFlagRegisters();
	}

	public void LDI(){
		if(currentInstructionHex.charAt(2) == '9') {
			IP = immediate;
		}
		else if(currentInstructionHex.charAt(2) == 'A'){
			SP = immediate;
		}
		else if(currentInstructionHex.charAt(2) == 'B'){
			P1 = immediate;
		}
		else if(currentInstructionHex.charAt(2) == 'C'){
			P2 = immediate;
		}
		else {
			r[reg1]=immediate;
		}
	}
	
	public void LOADER() {
		switch (instruction) {
			// load content from reg2 to reg1
			// assignment
			case "LD": {
				if(currentInstructionHex.charAt(2) == 'A'){
					SP = r[reg2];
				}
				if(currentInstructionHex.charAt(2) == 'B'){
					P1 = r[reg2];
				}
				else if(currentInstructionHex.charAt(2) == 'C'){
					P2 = r[reg2];
				}
				else{
					r[reg1] = r[reg2];
				}
				break;
			}
			
			// load an immidiate
			case "LDI":{
				LDI();
				break;
			}
			
			// load data from memory into reg1 using pointer from reg2
			case "LDFM":{
				if(currentInstructionHex.charAt(3) == 'A'){
					r[reg1] = RAM.LFM(SP);
					System.out.println("r[reg1]="+r[reg1]);
				}
				else if(currentInstructionHex.charAt(2) == 'B'){
					P1 = RAM.LFM(r[reg2]);
				}
				else if(currentInstructionHex.charAt(2) == 'C'){
					P2 = RAM.LFM(r[reg2]);
				}
				else {
					r[reg1] = RAM.LFM(r[reg2]);
				}
				break;
			}
			// load data into memory using reg1 and pointer reg2
			case "LDIM":{
				if(currentInstructionHex.charAt(2) == 'A'){
					StringBuilder hexData = new StringBuilder(Integer.toHexString(r[reg2]).toUpperCase());
					while(hexData.length()<4)
						hexData.insert(0, "0");
					RAM.write(SP, "0000"+ hexData);
					break;
				}
				else {
					StringBuilder hexData = new StringBuilder(Integer.toHexString(r[reg2]).toUpperCase());
					while (hexData.length() < 4)
						hexData.insert(0, "0");
					RAM.write(r[reg1], "0000" + hexData);
				}
				break;
			}
			// loads data into a register if any of the flag registers is up
			// LOAD CONDITIONED IMMEDIATE
			case "LDCI":{
				if(verifyEveryFlagRegister()){
					LDI();
				}
			}
			case "LDNZI":{
				if(!FZ()){
					LDI();
				}
			}
			case "LDZI":{
				if(FZ()){
					LDI();
				}
			}
			case "LDNSI":{
				if(!FS()){
					LDI();
				}
			}
			case "LDSI":{
				if(FS()){
					LDI();
				}
			}
		}
	}
	
	/*
	 * execute the actual instruction and it's the most demanding clock cycle part
	 */
	public void execute() {
		System.out.println("----CPU EXECUTING----");
		clockDelay();
		if(!instruction.equals("NOP")) {
			// load into registers or memory data
			LOADER();
			// execute and arithmetical logical instruction and updates flags
			ALU();
			
			if(instruction.equals("HALT"))
				CPU.halted=true;
			Main.clearScreen();
		}
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(int i=1;i<r.length;i++) {
			s.append("r").append(i).append("-->").append(r[i]).append("\n");
		}
		s.append("IP->").append(IP).append("\n");
		s.append("SP->").append(SP).append("\n");
		return s.toString();
	}
	
	public void reset() {
		RAM.memory=Arrays.copyOf(RAM.memoryCopy,RAM.memoryCopy.length);
		P1=0;
		P2=0;
		SP=0;
		IP=0;
        Arrays.fill(r, 0);
		Arrays.fill(flagRegister,false);
		CPU.halted=true;
		currentInstructionHex="XXXX";
		instruction="XXXX";
		registers.clear();
		instructionSet.clear();
		updateGraphicallyRegisters();
		updateGraphicallyCurrentInstruction();
		
		setRegistersMap();
		setInstructionSetMap();
	}
	
	
	public void updateGraphicallyRegisters() {
		for(int i=0;i<Main.registers.length-4;i++)
			Main.registers[i][1].setText(""+r[i+1]);
		Main.registers[8][1].setText(""+IP);
		Main.registers[9][1].setText(""+SP);
		Main.registers[10][1].setText(""+P1);
		Main.registers[11][1].setText(""+P2);
		for(int i=0;i<Main.flagRegistersLable[0].length;i++){
			Main.flagRegistersLable[1][i].setText((flagRegister[i] ? "1" : "0"));
		}
	}



	public void updateGraphicallyCurrentInstruction() {
		Main.currentInstructionHex.setText("0x"+currentInstructionHex);
		Main.currentInstructionLitteral.setText(instruction);
		Main.registerUsed1.setText(registers.get(Integer.toHexString(reg1).toUpperCase()));
		Main.registerUsed2.setText(registers.get(Integer.toHexString(reg2).toUpperCase()));
	}
	
	public void run() {
		updateGraphicallyRegisters();
		updateGraphicallyCurrentInstruction();
		while(!CPU.halted) {
			fetch();
			decode();
			execute();
			updateGraphicallyRegisters();
		}
		Main.errorLabel.setText("CPU HALTED");
		System.out.println("CPU HALTED");
	}
}
