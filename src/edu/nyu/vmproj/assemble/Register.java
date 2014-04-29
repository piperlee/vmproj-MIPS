package edu.nyu.vmproj.assemble;

public class Register implements Readable<Object>, Assignable<Object> {
	private String regName;
	private RegisterMap regMap;
	
	public Register(String regName) {
		this.regName = regName;
		regMap = RegisterMap.getInstance();
	}
	
	@Override
	public void assign(Object v) {
		regMap.put(regName, (Integer)v);
	}

	@Override
	public Integer read() {
		return regMap.get(regName);
	}

}
