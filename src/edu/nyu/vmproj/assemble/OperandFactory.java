package edu.nyu.vmproj.assemble;

import java.util.HashSet;

public class OperandFactory {
	private static OperandFactory instance;
	private HashSet<String> regNameSet;
	
	private OperandFactory() {
		//add 17 register names
		regNameSet = new HashSet<String>();
		regNameSet.add(new String("eax"));
		regNameSet.add(new String("ebx"));
		regNameSet.add(new String("ecx"));
		regNameSet.add(new String("edx"));
		regNameSet.add(new String("esi"));
		regNameSet.add(new String("edi"));
		regNameSet.add(new String("esp"));
		regNameSet.add(new String("ebp"));
		regNameSet.add(new String("eip"));
		regNameSet.add(new String("r08"));
		regNameSet.add(new String("r09"));
		regNameSet.add(new String("r10"));
		regNameSet.add(new String("r11"));
		regNameSet.add(new String("r12"));
		regNameSet.add(new String("r13"));
		regNameSet.add(new String("r14"));
		regNameSet.add(new String("r15"));
	}
	
	public static OperandFactory getInstance() {
		if (instance == null) instance = new OperandFactory();
		return instance;
	}
	
	private Integer parseInt(String s) {
		try {
		  s.toLowerCase();
		  s.trim();
		  RegisterMap regMap = RegisterMap.getInstance();
			if(s.indexOf('+') != -1 || s.indexOf('-') != -1) { 			  
			  char op = (s.indexOf('+') != -1)? '+' : '-';
			  String s1 = s.substring(0, s.indexOf('+'));
			  String s2 = s.substring(s.indexOf('+') + 1);
			  s1.trim();
			  s2.trim();
			  int i1 = regNameSet.contains(s1)? regMap.get(s1) : parseInt(s1);
			  int i2 = regNameSet.contains(s2)? regMap.get(s2) : parseInt(s2);
			  return (op == '+') ? i1+i2 : i1-i2;
			} else if(regNameSet.contains(s)) {
			  return regMap.get(s);
			} else if ((s.length()<2) || (s.charAt(s.length()-2) != '|')) {
        return Integer.decode(s);
      } else {
				int base = 0;
				if (s.charAt(s.length()-1) == 'b') base = 2;
				else if (s.charAt(s.length()-1) == 'h') base = 16;
				else return null;
				return Integer.parseInt(s.substring(0, s.length() - 2), base);
			}
		} catch (NumberFormatException e) {
			System.err.println("Integer parse error");
			return null;
		}
	}
	
	private boolean isMemOpr(String s) {
		if (s.length() <= 2) return false;
		if ((s.charAt(0) == '[') && (s.charAt(s.length() - 1) == ']')) return true;
		else return false;
	}
	
	private boolean isRegOpr(String s) {
		String ls = s.toLowerCase();
		return regNameSet.contains(ls);
	}
	
	private MemCell buildMemCell(String s) {
		Integer address = parseInt(s.substring(1, s.length()-1));
		return new MemCell(address);
	}
	
	private Constant buildConstant(String s) {
		return new Constant(parseInt(s));
	}
	
	private Register buildRegister(String s) {
		return new Register(s);
	}
	
	public Assignable buildLValue(String s) {
		if (isRegOpr(s)) return buildRegister(s);
		else if (isMemOpr(s)) return buildMemCell(s);
		else return null;
	}
	
	public Readable buildRValue(String s) {
		if (isRegOpr(s)) return buildRegister(s);
		else if (isMemOpr(s)) return buildMemCell(s);
		else return buildConstant(s);
	}
}