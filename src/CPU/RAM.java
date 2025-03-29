package src.CPU;


public class RAM{
	
	public static String[] memory;
	public static String[] memoryCopy;
	public static final int MAX_RAM_SIZE=65536;
	public static int RAMSize;
	
	public static void setRAMSize(int size) {
		RAMSize = size; 
	}
	
	public static int getRAMSize() {
		return MAX_RAM_SIZE<=RAMSize ? MAX_RAM_SIZE : RAMSize;
	}
	
	public RAM() {
		memory=new String[getRAMSize()];
		memoryCopy=new String[getRAMSize()];
		for(int i=0;i<getRAMSize();i++) {
			memory[i]="00000000";
			memoryCopy[i]=memory[i];
		}	
	}
	
	public static void resetRAM() {
		for(int i=0;i<getRAMSize();i++) {
			memory[i]="00000000";
			memoryCopy[i]=memory[i];
		}	
	}
	
	/*
	 * time required to access RAM = 0
	 * this function require an instruction pointer
	 * and return the instruction as a String
	 */
	// LNI = load next instruction
	public static String LNI(int IP) {
		return memory[IP];
	}
	
	// LFM = load from memory
	public static int LFM(int reg) {
		return Integer.parseInt(memory[reg].substring(3), 16) ;
	}
	
	public static void write(int reg,String hexData) {
		memory[reg]=hexData;
	}
	
}
