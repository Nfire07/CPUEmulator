package src.CPU;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ASMCompiler {
	
	public ASMCompiler() {

	}
	
	public String encoder(String line) {
	    if(line == null || line.isEmpty())
	        return "00000000";
	    if(line.trim().equals("HALT"))
	    	return "FF000000";
	    line = line.toUpperCase();
	    String instruction = "";
	    String concat = "0000";
        if(!line.trim().split("\\s+")[0].endsWith("I") && CPU.reverseSet.get(line.trim().split("\\s+")[0])!=null) {
            String[] keywords = line.split("\\s+");
            for(String keyword : keywords) {
                instruction += CPU.reverseSet.get(keyword.toUpperCase());
            }
        }
        else {
            String[] keywords = line.split("\\s+");
            for(String keyword : keywords) {
                if(CPU.reverseSet.get(keyword) != null)
                    instruction += CPU.reverseSet.get(keyword.toUpperCase());
                else
                    concat = keyword;
            }
        }
    
	    return instruction + concat;
	}

	
	public boolean isValidCode(String hexInstruction) {
		if(hexInstruction.length()==8) {
			try {
				Long.parseLong(hexInstruction,16);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	public void instructionLoader(int indexInMemory,String instruction) {
		RAM.write(indexInMemory, instruction);
	}
	
	public void Compile() {
		boolean compilationFailed = false;
		try (BufferedReader r = new BufferedReader(new FileReader(new File(EditorASM.currentlyWorkingOn)))) {
			String line;
			int lineCounter=0;
			
			while((line=r.readLine())!=null) {
				if(!line.startsWith(";")) {
					String instructionCode = encoder(line.trim());
					if(isValidCode(instructionCode)) {
						instructionLoader(lineCounter++, instructionCode);
					}
					else {
						System.out.println("line error => "+line);
						System.out.println("error encountered in line " + lineCounter + " with code " + instructionCode);
						Main.errorLabel.setText("ERROR");
						compilationFailed=true;
					}
				}
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		if(compilationFailed==true) {
			System.out.println("compilation failed");
		}
		else {
			Main.errorLabel.setText("COMPILED");
		}
	}
}