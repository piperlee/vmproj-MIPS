package edu.nyu.vmproj.assemble;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import edu.nyu.vmproj.assemble.SymbolEntry.TYPE;

public class Program {
	private static Program instance;
	// addr: 0x00400000 to 0x0fffffff
	private HashMap<Integer, Instruction> instMap;
	// name -> addr in dataMap
	HashMap <String, Integer> symbolTable;
	// starting addr of symbol -> symbol entry
	HashMap <Integer, SymbolEntry> symbolAddrMap;
	private LabelMap labelMap;
	private Memory memory;
	private RegisterMap regMap;
	private int textAddr;
	
	private Program(BufferedReader br) throws IOException {
		instMap = new HashMap<Integer, Instruction>();
		symbolTable = new HashMap<String, Integer>();
		symbolAddrMap = new HashMap <Integer, SymbolEntry>();
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
			// screening directives
			if ((line.charAt(0) == '.')
			    && !line.toLowerCase().equals(".data")
			    && !line.toLowerCase().equals(".rdata")
			    && !line.toLowerCase().equals(".text")
			    && !line.toLowerCase().contains(".globl") ) {
			  continue;
			}
			if (line.toLowerCase().equals(".data") 
			    || line.toLowerCase().equals(".rdata")) {
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
          // String 
          if (type.toLowerCase().equals(".asciiz") || type.toLowerCase().equals(".ascii")) {
            if ((i = line.indexOf('"')) == -1 || (j = line.lastIndexOf('"')) == -1) {
              System.err.println("Program parser error!");
              System.exit(-1);
            }
            line = line.substring(i+1, j);
            line = line.replaceAll("\\\\n", "\n");
            line = line.replaceAll("\\\\t", "\t");
            symbolTable.put(symbolName, regMap.get("$data"));
            updateSymbolAddrMap(symbolName, regMap.get("$data"), line.length(), TYPE.STRING);
            memory.putData(line);            
            //System.out.println(line);
          }
          // float
          if (type.toLowerCase().equals(".float")) {
            String[] floats = line.split("[, \t]+");
            int startingAddr = regMap.get("$data");
            symbolTable.put(symbolName, startingAddr);
            for (String f : floats) {
              float fn = Float.parseFloat(f);
              updateSymbolAddrMap(symbolName, startingAddr, 4, TYPE.FLOAT);
              memory.putData(fn);
              startingAddr += 4;
            }
          }
          
          // double 
          if (type.toLowerCase().equals(".double")) {
            String[] doubles = line.split("[, \t]+");
            int startingAddr = regMap.get("$data");
            symbolTable.put(symbolName, startingAddr);
            for (String d : doubles) {
              Double dn = Double.parseDouble(d);
              updateSymbolAddrMap(symbolName, startingAddr, 8, TYPE.DOUBLE);
              memory.putData(dn);
              startingAddr += 8;             
            }
          }
          
          // space
          if (type.toLowerCase().equals(".space")) {
            int size = Integer.parseInt(line);
            int startingAddr = regMap.get("$data");
            symbolTable.put(symbolName, startingAddr);
            newSpaceInSymbolAddrMap(symbolName, startingAddr, size);
            memory.newSpace(size);
          }
          
          // word
          if (type.toLowerCase().equals(".word")) {
            String[] words = line.split("[, \t]+");
            int startingAddr = regMap.get("$data");
            symbolTable.put(symbolName, startingAddr);
            for (String w : words) {
              int in = Integer.parseInt(w);
              updateSymbolAddrMap(symbolName, startingAddr, 4, TYPE.INT);
              memory.putData(in);
              startingAddr += 4;
            }
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
	      if (line.toLowerCase().equals(".data") || line.toLowerCase().equals(".rdata")) {
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
	 
	void updateSymbolAddrMap(String name, Integer startingAddr, int size, TYPE t) {
    SymbolEntry e = new SymbolEntry(name,size,startingAddr,t);
    symbolAddrMap.put(startingAddr, e);
	}
	
	private void newSpaceInSymbolAddrMap(String name, Integer startingAddr, Integer size) {
	  SymbolEntry e = new SymbolEntry(name,size,startingAddr);
    symbolAddrMap.put(startingAddr, e);
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
