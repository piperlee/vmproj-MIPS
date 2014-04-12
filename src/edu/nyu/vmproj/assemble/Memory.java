package edu.nyu.vmproj.assemble;

import java.util.HashMap;

public class Memory {
	private static Memory instance;
	// addr -> value
	HashMap<Integer, Integer> memMap;
	Stack vmStack; 
	
	private Memory() {
		memMap = new HashMap<Integer, Integer>();
		vmStack = Stack.getInstance();
	}
	
	public static Memory getInstance() {
		if (instance == null) instance = new Memory();
		return instance;
	}
	
	// modify existed on-stack element
	public void put(Integer k, Integer v) {
		memMap.put(k, v);
	}
	
	// push new to stack
	public void push(Integer i){
	  vmStack.push(i);
	  RegisterMap regMap = RegisterMap.getInstance();
	  regMap.put("esp", regMap.get("esp") - 4);
	  put(regMap.get("esp") , i);
	}
	
	// pop from stack
	public Integer pop(){
	  RegisterMap regMap = RegisterMap.getInstance();
	  memMap.remove(regMap.get("esp"));
	  regMap.put("esp", regMap.get("esp") + 4);
	  return vmStack.pop();  
	}
	
	// get element from stack
	public Integer get(Integer k) {
		if (!memMap.containsKey(k)) {
			System.err.println("Memory "+k+" is not found!");
			return null;
		} else {
			return memMap.get(k);
		}
	}
}
