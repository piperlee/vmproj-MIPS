package edu.nyu.vmproj.assemble;

import java.util.HashMap;

import edu.nyu.vmproj.assemble.SymbolEntry.TYPE;

public class Memory {
	private static Memory instance;
	// addr -> value (Addr: 0x7fffffff down)
	HashMap<Integer, Byte> stackMap;
	// addr -> value (Addr: 0x10000000 up)
	HashMap<Integer, Byte> dataMap;
	Stack vmStack; 
	
	private Memory() {
		stackMap = new HashMap<Integer, Byte>();
		dataMap = new HashMap<Integer, Byte>();
		vmStack = Stack.getInstance();
	}
	
	public static Memory getInstance() {
		if (instance == null) instance = new Memory();
		return instance;
	}
	
	// modify existed on-stack element
	public void putStack(Integer k, Integer v) {
	  byte [] bytes = Util.toByta(v);
	  for ( byte b : bytes) {
      stackMap.put(k, b);
      k++;
    }
	}
	
	// push new to stack
	public void pushStack(Integer i){
	  vmStack.push(i);
	  RegisterMap regMap = RegisterMap.getInstance();
	  byte [] bytes = Util.toByta(i);
    for ( byte b : bytes) {
      stackMap.put(regMap.get("$29"), b);
      regMap.put("$29", regMap.get("$29") - 1);
    }
	}
	
	// pop from stack
	public Integer popStack(){
	  RegisterMap regMap = RegisterMap.getInstance();
	  byte [] bytes = Util.toByta(regMap.get("$29"));
    for ( byte b : bytes) {
      stackMap.remove(regMap.get("$29"));
      regMap.put("$29", regMap.get("$29") + 1);
    }
	  return vmStack.pop();  
	}
	
	// get element from stack
	public Integer getStack(Integer k) {
		if (!stackMap.containsKey(k) || !stackMap.containsKey(k+1)
		    || !stackMap.containsKey(k+2) || !stackMap.containsKey(k+3)) {
			System.err.println("Memory "+k+" is not found!");
			System.exit(-1);;
		}
		byte [] bytes = new byte[]
		      { stackMap.get(k),stackMap.get(k+1),stackMap.get(k+2),stackMap.get(k+3) };
		return Util.toInt(bytes);		  
	}
	
	public void putData(Integer k, Object v) {
	  byte [] bytes = null;
	  if ( v instanceof Integer ) {
	    bytes = Util.toByta((Integer)v);	    
	  } else if ( v instanceof Float ) {
	    bytes = Util.toByta((Float)v);
	  } else if ( v instanceof Double ) {
	    bytes = Util.toByta((Double)v);
    } else if ( v instanceof String ) {
      bytes = Util.toByta((String)v);
    } else {
      System.err.println("Unknown data type");
      System.exit(-1);
    }
	  for ( byte b : bytes) {
	    dataMap.put(k, b);
	    k++;
    }
	}
	
	public void putData(Object v) {
	  RegisterMap regMap = RegisterMap.getInstance();
	  byte [] bytes = null;
	  if (v instanceof Integer) {
	    bytes = Util.toByta((Integer)v); 
	  } else if (v instanceof Float) {
	    bytes = Util.toByta((Float)v);  
    } else if (v instanceof Double) {
      bytes = Util.toByta((Double)v);  
    } else if (v instanceof String) {
      bytes = Util.toByta((String)v);  
    } else {
      System.err.println("Unknown data type");
      System.exit(-1);
    }
	  for ( byte b : bytes) {
	    dataMap.put(regMap.get("$data"), b);
	    regMap.put("$data", regMap.get("$data") + 1);
    }
	}
	
	// byte
	public void newSpace(Integer size) {
	  RegisterMap regMap = RegisterMap.getInstance();
	  byte [] bytes = new byte[size];
	  for ( byte b : bytes) {
      dataMap.put(regMap.get("$data"), b);
      regMap.put("$data", regMap.get("$data") + 1);
    }
	}
	
	public Object getData(Integer k) {
	  Program prog = Program.getInstance();
	  if (!dataMap.containsKey(k)) {
      System.err.println("Memory "+k+" is not found!");
      System.exit(-1) ;
    } 
	  if (prog.symbolAddrMap.containsKey(k)) {
	    int size = prog.symbolAddrMap.get(k).size;
	    byte[] bytes = new byte[size];
	    for (int i = 0; i < size; i ++ ) {
	      bytes[i] = dataMap.get(k+i);
	    }
	    TYPE t = prog.symbolAddrMap.get(k).t;
	    if (t == TYPE.INT) {
	      return Util.toInt(bytes);
	    } else if (t == TYPE.FLOAT) {
	      return Util.toFloat(bytes);
      } else if (t == TYPE.DOUBLE) {
        return Util.toDouble(bytes);  
	    } else if (t == TYPE.STRING) {
	      return Util.toString(bytes);  
	    }
	  }
    return dataMap.get(k);
	}
}
