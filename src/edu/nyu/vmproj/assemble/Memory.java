package edu.nyu.vmproj.assemble;

import java.util.HashMap;

public class Memory {
	private static Memory instance;
	// addr -> value (Addr: 0x7fffffff down)
	HashMap<Integer, Integer> stackMap;
	// addr -> value (Addr: 0x10000000 up)
	HashMap<Integer, Object> dataMap;
	Stack vmStack; 
	
	private Memory() {
		stackMap = new HashMap<Integer, Integer>();
		dataMap = new HashMap<Integer, Object>();
		vmStack = Stack.getInstance();
	}
	
	public static Memory getInstance() {
		if (instance == null) instance = new Memory();
		return instance;
	}
	
	// modify existed on-stack element
	public void putStack(Integer k, Integer v) {
		stackMap.put(k, v);
	}
	
	// push new to stack
	public void pushStack(Integer i){
	  vmStack.push(i);
	  RegisterMap regMap = RegisterMap.getInstance();
	  regMap.put("$29", regMap.get("$29") - 4);
	  putStack(regMap.get("$29") , i);
	}
	
	// pop from stack
	public Integer popStack(){
	  RegisterMap regMap = RegisterMap.getInstance();
	  stackMap.remove(regMap.get("esp"));
	  regMap.put("esp", regMap.get("esp") + 4);
	  return vmStack.pop();  
	}
	
	// get element from stack
	public Integer getStack(Integer k) {
		if (!stackMap.containsKey(k)) {
			System.err.println("Memory "+k+" is not found!");
			return null;
		} else {
			return stackMap.get(k);
		}
	}
	
	public void putData(Integer k, Object v) {
	  dataMap.put(k, v);
	}
	
	public void putData(Object v) {
	  RegisterMap regMap = RegisterMap.getInstance();
	  int size = 0;
	  if (v instanceof Integer) {
	    size = 4;
	  } else if (v instanceof Float) {
      size = 4;
    } else if (v instanceof Double) {
      size = 8;
    } else if (v instanceof String) {
      size = String.valueOf(v).length();
    } else {
      System.err.println("Unknown data type");
      System.exit(-1);
    }
	  dataMap.put(regMap.get("$data"), v);
	  regMap.put("$data", regMap.get("$data") + size);
	}
	
	public Object getData(Integer k) {
	  if (!dataMap.containsKey(k)) {
      System.err.println("Memory "+k+" is not found!");
      return null;
    } else {
      return dataMap.get(k);
    }
	}
}
