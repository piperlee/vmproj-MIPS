package edu.nyu.vmproj.assemble;

public class MemCell implements Assignable, Readable {
	private Integer address;
	private Memory memory;
	
	public MemCell(Integer addr) {
		address = addr;
		memory = Memory.getInstance();
	}

	@Override
	public void assign(Integer v) {
		memory.put(address, v);
	}

	@Override
	public Integer read() {
		return memory.get(address);
	}

}
