package edu.nyu.vmproj.assemble;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Program {
	private static Program instance;
	// addr: 0x00400000 to 0x0fffffff
	private HashMap<Integer, Instruction> instMap;
	// name -> addr in dataMap
	HashMap <String, Integer> symbolTable;
	private LabelMap labelMap;
	private Memory memory;
	private RegisterMap regMap;
	private int textAddr;
	
	private Program(BufferedReader br) throws IOException {
		instMap = new HashMap<Integer, Instruction>();
		symbolTable = new HashMap<String, Integer>();
		labelMap = LabelMap.getInstance();
		memory = Memory.getInstance();		
		regMap = RegisterMap.getInstance();
		textAddr = 0x00400000;
		String line = null;
		while ((line = br.readLine()) != null) {
		  //parse Comment
		  int i = 0;
      if ((i = line.indexOf('#')) != -1){
        line = line.substring(0,i);
      }		  
		  line = line.trim();
		  // empty line
			if (line.length() == 0) continue;						
			if (line.toLowerCase().equals(".data")) {
			  parseData(br);
			} else {
        parseText(br,line);
			}
			
		}
	}
	
	private void parseData(BufferedReader br) throws IOException {
	  String line;
    while ((line = br.readLine()) != null) {
      //parse Comment
      int i = 0;
      if ((i = line.indexOf('#')) != -1){
        line = line.substring(0,i);
      }     
      line = line.trim();
      // empty line
      if (line.length() == 0) continue; 
      if (line.toLowerCase().equals(".text")) {
        parseText(br,line);
        return;
      } else {
        i = 0;
        if ((i = line.indexOf(':'))!=-1) {
          String symbolName = line.substring(0, i);
          line = line.substring(i+1);
          line = line.trim();
          if (line.length() == 0 || (i = line.indexOf('.'))!=0 ) {
            System.err.println("Program parser error!");
            System.exit(-1);
          }
          int j = line.indexOf(' ');
          String type = line.substring(i,j);
          line = line.substring(j+1);
          line = line.trim();
          if (line.length() == 0) {
            System.err.println("Program parser error!");
            System.exit(-1);
          }
          if (type.toLowerCase().equals(".asciiz") || type.toLowerCase().equals(".ascii")) {
            if ((i = line.indexOf('"')) == -1 || (j = line.lastIndexOf('"')) == -1) {
              System.err.println("Program parser error!");
              System.exit(-1);
            }
            line = line.substring(i+1, j);
            line = line.replaceAll("\\\\n", "\n");
            line = line.replaceAll("\\\\t", "\t");
            symbolTable.put(symbolName, regMap.get("$data"));
            memory.putData(new String(line));
            int size = line.getBytes().length;
            regMap.put("$data", regMap.get("$data") + size);
            //System.out.println(line);
          }
          // TODO add more directives          
        }
      }
    }
	}
	
	 private void parseText(BufferedReader br, String line) throws IOException {
	   if (line.toLowerCase().trim().equals(".text")) {
	     line = br.readLine();
	   }
	   String start = null;
	   do {
	      //parse Comment
	      int i = 0;
	      if ((i = line.indexOf('#')) != -1){
	        line = line.substring(0,i);
	      }     
	      line = line.trim();
	      // empty line
	      if (line.length() == 0) continue; 
	      // starting point
	      if (line.contains(".globl")) {
	        line = line.substring(line.indexOf('.') + 6).trim();
	        if (line.length() == 0) {
	          System.err.println("Program parser error!");
	          System.exit(-1);
	        }
	        start = line;
	      }
	      if (line.toLowerCase().equals(".data")) {
	        parseData(br);
	        return;
	      } else {
	        i = 0;
	        if ((i = line.indexOf(':'))!=-1) {
	          labelMap.put(line.substring(0, i), new Integer(textAddr));
	          if (line.substring(0, i).equals(start)) {
	            regMap.put("$pc", textAddr);
	          }
	          line = line.substring(i+1);
	          line = line.trim();
	        }
	        if (line.length() != 0) {
	          String[] ops = line.split("[, \t]+");
	          if (ops.length == 0) continue;
	          else if (ops.length == 1) {
	            instMap.put(new Integer(textAddr), 
	                new Instruction(ops[0].trim().toLowerCase(), null, null, null));
	            textAddr+=4;
	          } else if (ops.length == 2) {
	            instMap.put(new Integer(textAddr), 
	                new Instruction(ops[0].trim().toLowerCase(), ops[1].trim(), null, null));
	            textAddr+=4;
	          } else if (ops.length == 3) {
	            instMap.put(new Integer(textAddr), 
	                new Instruction(ops[0].trim().toLowerCase(), ops[1].trim(), ops[2].trim(), null));
	            textAddr+=4;
	          } else if (ops.length == 4) {
	            instMap.put(new Integer(textAddr), 
	                new Instruction(ops[0].trim().toLowerCase(), ops[1].trim(), ops[2].trim(), ops[3].trim()));
	            textAddr+=4;
	          } else {
	            System.err.println("Program parser error!");
	            System.exit(-1);
	          }
	        }
	      }
	    } while ((line = br.readLine()) != null);
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
