package edu.nyu.vmproj.assemble;

public class MemCell implements Assignable<Integer>, Readable<Integer> {
	private Integer address;
	private Memory memory;
	
	public MemCell(Integer addr) {
		address = addr;
		memory = Memory.getInstance();
	}

	@Override
	public void assign(Integer v) {		
		RegisterMap regMap = RegisterMap.getInstance();
		if (address > regMap.get("$29")) {
		  memory.putStack(address, v);
		} else {
		  memory.putData(address, v);
		}
	}

	@Override
	public Integer read() {
	  RegisterMap regMap = RegisterMap.getInstance();
	  if (address > regMap.get("$29")) {
      return memory.getStack(address);
    } else {
      return (Integer) memory.getData(address);
    }
	}

}
