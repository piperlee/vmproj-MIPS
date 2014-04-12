package edu.nyu.vmproj.assemble;

public class Register implements Readable, Assignable {
	private String regName;
	private RegisterMap regMap;
	
	public Register(String regName) {
		this.regName = regName;
		regMap = RegisterMap.getInstance();
	}
	
	@Override
	public void assign(Integer v) {
		regMap.put(regName, v);
	}

	@Override
	public Integer read() {
		return regMap.get(regName);
	}

}
