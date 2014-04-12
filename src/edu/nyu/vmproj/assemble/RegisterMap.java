package edu.nyu.vmproj.assemble;

import java.util.HashMap;

public class RegisterMap {
	private static RegisterMap instance;
	HashMap<String, Integer> regMap;
	
	private RegisterMap() {
		regMap = new HashMap<String, Integer>();
	}
	
	public static RegisterMap getInstance() {
		if (instance == null) instance = new RegisterMap();
		return instance;
	}
	
	public Integer get(String regName) {
		if (!regMap.containsKey(regName)) {
			regMap.put(regName, new Integer(0));
		}
		return regMap.get(regName);
	}
	
	public void put(String regName, Integer value) {
		regMap.put(regName, value);
	}
}
