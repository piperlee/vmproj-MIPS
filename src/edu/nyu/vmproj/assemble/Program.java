package edu.nyu.vmproj.assemble;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Program {
	private static Program instance;
	private HashMap<Integer, Instruction> instMap;
	
	private Program(BufferedReader br) throws IOException {
		instMap = new HashMap<Integer, Instruction>();
		LabelMap labelMap = LabelMap.getInstance();
		int address = 0;
		String line = null;
		while ((line = br.readLine()) != null) {
		  //parse Comment
		  int i = 0;
      if ((i = line.indexOf('#')) != -1){
        line = line.substring(0,i);
      }		  
		  line = line.trim();
			if (line.length() == 0) continue;			
			i = 0;
			if ((i = line.indexOf(':'))!=-1) {
				labelMap.put(line.substring(0, i), new Integer(address));
				line = line.substring(i+1);
				line = line.trim();
			}
			if (line.length() != 0) {
				String[] ops = line.split("[, ]+");
				if (ops.length == 0) continue;
				else if (ops.length == 1) {
					instMap.put(new Integer(address), 
					    new Instruction(ops[0].toLowerCase(), null, null));
					address+=4;
				} else if (ops.length == 2) {
					instMap.put(new Integer(address), 
					    new Instruction(ops[0].toLowerCase(), ops[1].toLowerCase(), null));
					address+=4;
				} else if (ops.length == 3) {
					instMap.put(new Integer(address), 
					    new Instruction(ops[0].toLowerCase(), ops[1].toLowerCase(), ops[2].toLowerCase()));
					address+=4;
				} else {
					System.err.println("Program parser error!");
					System.exit(-1);
				}
			}
		}
	}
	
	public static Program getInstance() {
		if (instance == null) {
			System.out.println("program is not initialize");
			System.exit(-1);
		}
		return instance;
	}
	
	public static void initialize(String fileName) {
		BufferedReader br = null;
		try {
		  if (instance == null){
  			br = new BufferedReader(new FileReader(fileName));
  			instance = new Program(br);
		  }
		} catch (FileNotFoundException e) {
			System.err.println("file "+fileName+" is not found.");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Instruction getInst(Integer address) {
		if (!instMap.containsKey(address)) return null;
		else return instMap.get(address);
	}
}
