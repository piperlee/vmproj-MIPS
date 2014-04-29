package edu.nyu.vmproj.assemble;

import java.util.HashMap;

public class RegisterMap {
	private static RegisterMap instance;
	// $0-31 value
	// $pc -> program counter
	// Long because register will store mem addr, java do not support unsigned int
	HashMap<String, Integer> regMap;
	
	private RegisterMap() {
		regMap = new HashMap<String, Integer>();
//		regMap.put("$pc", 0);
		regMap.put("$0", 0x00000000);
		regMap.put("$data", 0x10010000);
	}
	
	public static RegisterMap getInstance() {
		if (instance == null) instance = new RegisterMap();
		return instance;
	}
	
	public Integer get(String regName) {
	  OperandFactory opFac = OperandFactory.getInstance();
    String name = opFac.unifyRegName(regName);
    return regMap.get(name);
	}
	
	public void put(String regName, Integer value) {
	  OperandFactory opFac = OperandFactory.getInstance();
	  String name = opFac.unifyRegName(regName);
		regMap.put(name, value);
	}
	
	public boolean contains(String regName) {
	  OperandFactory opFac = OperandFactory.getInstance();
    String name = opFac.unifyRegName(regName);
    return regMap.containsKey(name);
	}
	
	public boolean validRegister(String regName) {
	  OperandFactory opFac = OperandFactory.getInstance();
    String name = opFac.unifyRegName(regName);
    return opFac.regNameMap.containsValue(name);
	}
}
