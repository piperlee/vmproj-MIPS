package edu.nyu.vmproj.assemble;

public class Instruction {
	private String op;
	private String arg1;
	private String arg2;
	
	public Instruction(String op, String arg1, String arg2) {
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	public String getOp() {
		return op;
	}
	
	public String getArg1() {
		return arg1;
	}
	
	public String getArg2() {
		return arg2;
	}
	
	public int getArgCount(){
	  if (this.arg1 == null) return 0;
	  if (this.arg2 == null) return 1;
	  return 2;
	}
}
