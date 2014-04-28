package edu.nyu.vmproj.assemble;

public class Instruction {
	private String op;
	private String arg1;
	private String arg2;
	private String arg3;
	
	public Instruction(String op, String arg1, String arg2, String arg3) {
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
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
	
	public String getArg3() {
	  return arg3;
	}
	
	public int getArgCount(){
	  if (this.arg1 == null) return 0;
	  if (this.arg2 == null) return 1;
	  if (this.arg3 == null) return 2;
	  return 3;
	}
	
	public void print() {
	  System.out.print(this.op);
	  if (this.arg1 != null) System.out.print(" "+this.arg1);
	  if (this.arg2 != null) System.out.print(" "+this.arg2);
	  if (this.arg3 != null) System.out.print(" "+this.arg3);
	  System.out.println();
	}
}
