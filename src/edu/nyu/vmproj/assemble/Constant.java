package edu.nyu.vmproj.assemble;

public class Constant implements Readable {

	private Integer constant;
	
	public Constant(Integer v) {
		constant = v;
	}
	
	public Integer read() {
		return constant;
	}
}
