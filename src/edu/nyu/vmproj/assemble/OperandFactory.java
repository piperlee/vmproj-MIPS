package edu.nyu.vmproj.assemble;

import java.math.BigDecimal;
import java.util.HashMap;

public class OperandFactory {
	private static OperandFactory instance;
	// $zero -> $0
	// unify to $n
	HashMap<String,String> regNameMap;
	private RegisterMap regMap;
	
	private OperandFactory() {
		//MIPS has dual names for the same register
		regNameMap = new HashMap<String,String>();
		regMap = RegisterMap.getInstance();
		regNameMap.put("$zero", "$0");
		regNameMap.put("$at", "$1");
		regNameMap.put("$v0", "$2");
		regNameMap.put("$v1", "$3");
		regNameMap.put("$a0", "$4");
		regNameMap.put("$a1", "$5");
		regNameMap.put("$a2", "$6");
		regNameMap.put("$a3", "$7");
		regNameMap.put("$t0", "$8");
    regNameMap.put("$t1", "$9");
    regNameMap.put("$t2", "$10");
    regNameMap.put("$t3", "$11");
    regNameMap.put("$t4", "$12");
    regNameMap.put("$t5", "$13");
    regNameMap.put("$t6", "$14");
    regNameMap.put("$t7", "$15");
    regNameMap.put("$s0", "$16");
    regNameMap.put("$s1", "$17");
    regNameMap.put("$s2", "$18");
    regNameMap.put("$s3", "$19");
    regNameMap.put("$s4", "$20");
    regNameMap.put("$s5", "$21");
    regNameMap.put("$s6", "$22");
    regNameMap.put("$s7", "$23");
    regNameMap.put("$t8", "$24");
    regNameMap.put("$t9", "$25");
    regNameMap.put("$k0", "$26");
    regNameMap.put("$k1", "$27");
    regNameMap.put("$gp", "$28");
    regNameMap.put("$sp", "$29");
    regNameMap.put("$fp", "$30");
    regNameMap.put("$ra", "$31");
    // floating registers
    regNameMap.put("$f0", "$f0");
    regNameMap.put("$f1", "$f1");
    regNameMap.put("$f2", "$f2");
    regNameMap.put("$f3", "$f3");
    regNameMap.put("$f4", "$f4");
    regNameMap.put("$f5", "$f5");
    regNameMap.put("$f6", "$f6");
    regNameMap.put("$f7", "$f7");
    regNameMap.put("$f8", "$f8");
    regNameMap.put("$f9", "$f9");
    regNameMap.put("$f10", "$f10");
    regNameMap.put("$f11", "$f11");
    regNameMap.put("$f12", "$f12");
    regNameMap.put("$f13", "$f13");
    regNameMap.put("$f14", "$f14");
    regNameMap.put("$f15", "$f15");
    regNameMap.put("$f16", "$f16");
    regNameMap.put("$f17", "$f17");
    regNameMap.put("$f18", "$f18");
    regNameMap.put("$f19", "$f19");
    regNameMap.put("$f20", "$f20");
    regNameMap.put("$f21", "$f21");
    regNameMap.put("$f22", "$f22");
    regNameMap.put("$f23", "$f23");
    regNameMap.put("$f24", "$f24");
    regNameMap.put("$f25", "$f25");
    regNameMap.put("$f26", "$f26");
    regNameMap.put("$f27", "$f27");
    regNameMap.put("$f28", "$f28");
    regNameMap.put("$f29", "$f29");
    regNameMap.put("$f30", "$f30");
    regNameMap.put("$f31", "$f31");
    
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
			  int i1 = isRegOpr(s1)? regMap.get(s1) : parseInt(s1);
			  int i2 = isRegOpr(s2)? regMap.get(s2) : parseInt(s2);
			  return (op == '+') ? i1+i2 : i1-i2;
			} else if (isRegOpr(s)) {
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
	
	// 4($2)
	private boolean isMemOpr(String s) {
		if (s.length() <= 2) return false;
		if (s.indexOf('(') != -1 && s.indexOf(')') != -1 ) return true;
		else return false;
	}
	
	private boolean isRegOpr(String s) {
		String ls = s.toLowerCase();
		return regNameMap.containsKey(ls) || regNameMap.containsValue(ls);
	}
	
	private boolean isNameOpr(String s) {
	  Program prog = Program.getInstance();
	  return prog.symbolTable.containsKey(s);
	}
	
	/**
	 * Given a register name, find the actual name stored in registerMap ($0-$31)
	 * @param s
	 * @return
	 */
	String unifyRegName(String s) {
	  String ls = s.toLowerCase();
	  return regNameMap.containsKey(ls)?regNameMap.get(ls):ls;
	}
	
	// 100($r)
	// ($r)
	private MemCell buildMemCell(String s) {
	  int i = s.indexOf('(');
	  int j = s.indexOf(')');
	  int offset = 0;
	  if (i > 0) {
	    offset = new BigDecimal(s.substring(0, i)).intValue();
	  } else if ( i == 0 ) {
	    offset = 0;
	  }
	  String reg = unifyRegName(s.substring(i+1,j));
	  int address = regMap.get(reg) + offset;
		return new MemCell(address);
	}
	
	private Constant<Integer> buildIntConstant(String s) {
	  int val = new BigDecimal(s).intValue();
		return new Constant<Integer>(val);
	}
	
	private Constant<Float> buildFloatConstant(String s) {
    float val = new BigDecimal(s).floatValue();
    return new Constant<Float>(val);
  }
		
	private Register buildRegister(String s) {
	  s = unifyRegName(s.toLowerCase());
		return new Register(s);
	}
	
	private NameAddress buildNameAddress(String s) {
	  return new NameAddress(s);
	}
	
	public Assignable<Object> buildLValue(String s) {
		if (isRegOpr(s)) return buildRegister(s);
		else if (isMemOpr(s)) return buildMemCell(s);
		else if (isNameOpr(s)) return buildNameAddress(s);
		else return null;
	}
	
	@SuppressWarnings("unchecked")
  public Readable<Object> buildRValue(String s) {
		if (isRegOpr(s)) return buildRegister(s);
		else if (isMemOpr(s)) return buildMemCell(s);
		else if (isNameOpr(s)) return buildNameAddress(s);
		else if (Util.isInteger(s)) return buildIntConstant(s);
		else if (Util.isFloat(s)) return buildFloatConstant(s);
		return null;
	}
}