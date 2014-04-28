package edu.nyu.vmproj.assemble;

public class Constant<T> implements Readable {

	private T constant;
	
	public Constant(T v) {
		constant = v;
	}
	
	public T read() {
		return constant;
	}
}
