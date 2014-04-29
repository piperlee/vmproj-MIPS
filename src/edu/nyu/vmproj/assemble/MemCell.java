package edu.nyu.vmproj.assemble;

public class MemCell implements Assignable<Object>, Readable<Object> {
	private Integer address;
	private Memory memory;
	
	public MemCell(Integer addr) {
		address = addr;
		memory = Memory.getInstance();
	}

	@Override
	public void assign(Object v) {		
		RegisterMap regMap = RegisterMap.getInstance();
		if (address >= regMap.get("$29")) {
		  memory.putStack(address, (Integer)v);
		} else {
		  memory.putData(address, v);
		}
	}

	@Override
	public Object read() {
	  RegisterMap regMap = RegisterMap.getInstance();
	  if (address >= regMap.get("$29")) {
      return memory.getStack(address);
    } else {
      return memory.getData(address);
    }
	}

}
