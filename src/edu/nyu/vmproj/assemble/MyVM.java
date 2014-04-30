package edu.nyu.vmproj.assemble;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import edu.nyu.vmproj.assemble.SymbolEntry.TYPE;


public class MyVM {
  // map: instr addr -> instr
  Program prog;
  // map: reg name -> value
  RegisterMap regMap;
  // map: label name -> instr addr
  LabelMap labelMap;
  // mem addr -> value
  Memory memory;
  // sys call f
  
  OperandFactory oprFact;
  
  //for test
  PrintWriter out;
  
  private void setPC(Integer i) {
    regMap.put("$pc", new Integer(i));
  }
  
  // offset: word
  private void incPC(Integer offset) {
    regMap.put("$pc", new Integer(regMap.get("$pc") + offset * 4));
  }
  
  private void incPC() {
    regMap.put("$pc", new Integer(regMap.get("$pc") + 4));
  }
  
  private void execute(Instruction inst) {
    String op = inst.getOp();
    
    // Memory
    if (op.equals("li")) {
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop2.read());
      incPC();
      return;
    }
    
    // load address
    if (op.equals("la")) {
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop2.read());      
      incPC();
      return;
    }
    
    if (op.equals("lb")) {
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());            
      if (rop2 instanceof MemCell) {
        int addr = ((MemCell) rop2).getAddr();
        Byte b = memory.getDataByte(addr);
        wop1.assign(b.intValue() & 0xFF);
      } else {
        runningErr(inst);
      }
      
      incPC();
      return;
    }
    
    if (op.equals("move")) {
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop2.read());
      incPC();
      return;
    }
    
    if (op.equals("sw")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg2());
      wop1.assign(rop1.read());
      incPC();
      return;
    }
    
    if (op.equals("lw")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read());
      incPC();
      return;
    }
    
    if (op.equals("lui")) {
        Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
        Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign((Integer)rop1.read() << 16);
        incPC();
        return;
     }
    
    if (op.equals("mflo")) {
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(regMap.get("$lo"));
      incPC();
      return;
    }
    
    if(op.equals("mfc0")){
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop2.read());
      incPC();
      return;
    }
    
    // Floating part - Data transfer
    if (op.equals("lwc1")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      if (rop1 instanceof MemCell) {
        int addr = ((MemCell) rop1).getAddr();        
        wop1.assign(memory.getWord(addr));
      } else {
        runningErr(inst);
      }
      incPC();
      return;
    }
    
    if (op.equals("swc1")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg2());
      if (wop1 instanceof MemCell) {   
        wop1.assign(rop1.read());
      } else {
        runningErr(inst);
      }
      incPC();
      return;
    }
    
    // Pseudo
    //lwc1 $f1,100($2)
    //l.s   fd,val
    if (op.equals("l.s")) {
      if (Util.isFloat(inst.getArg2())) {
        Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
        Float f = Float.parseFloat(inst.getArg2());
        Integer bits = Float.floatToIntBits(f);
        Readable<Object> rop1 = oprFact.buildRValue(String.valueOf(bits));
        wop1.assign(rop1.read());
        
      } else if (prog.symbolTable.containsKey(inst.getArg2())) {
        Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
        Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
        Float f = (Float)memory.getData((Integer)rop1.read());
        wop1.assign(Float.floatToIntBits(f));        
      
      } else if (regMap.validRegister(inst.getArg2())) { 
        Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
        Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
        wop1.assign(rop1.read());  
        
      } else {
        runningErr(inst);
      }
      incPC();
      return;
    }
    
    if (op.equals("l.d")) {
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      Assignable<Object> wop2 = null;
      try {
        wop2 = oprFact.buildLValue(Util.getPairedRegName(inst.getArg1()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      if (Util.isDouble(inst.getArg2())) {
        double d = Double.parseDouble(inst.getArg2());
        long bits = Double.doubleToLongBits(d);
        int low = (int) bits; 
        int high = (int) (bits >> 32);
        wop1.assign(high);
        wop2.assign(low);        
      } else if (prog.symbolTable.containsKey(inst.getArg2())) {
        int addr = prog.symbolTable.get(inst.getArg2());
        double d = (Double)memory.getData(addr);
        long bits = Double.doubleToLongBits(d);
        int low = (int) bits; 
        int high = (int) (bits >> 32);
        wop1.assign(high);
        wop2.assign(low);
      
      } else if (regMap.validRegister(inst.getArg2())) { 
        Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Object> rop2 = null;
        try {
          rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
        } catch (Exception e) {
          System.err.println(e.getMessage());
          System.exit(-1);
        }        
        wop1.assign(rop1.read());
        wop2.assign(rop2.read());
        
      } else {
        runningErr(inst);
      }
      incPC();
      return;
    }
    
    // System call
    if (op.equals("syscall")) {
      if (regMap.get("$2") == 1) {
        // print int
        System.out.print(regMap.get("$4"));
        
      } else if (regMap.get("$2") == 2) {
        // print float
        System.out.print(Float.intBitsToFloat(regMap.get("$f12")));
        
      } else if (regMap.get("$2") == 3) {
        // print double
        long l = (((long)regMap.get("$f12")) << 32) | (((long)regMap.get("$f13") & 0xffffffffL));
    	  System.out.print(Double.longBitsToDouble(l));
    	  
      } else if (regMap.get("$2") == 4) {
        // print string
        System.out.print(memory.getData(regMap.get("$4")));
        
      } else if (regMap.get("$2") == 5) {
        // read int
        Scanner in = new Scanner(System.in);
        regMap.put("$2", in.nextInt());
        
      } else if (regMap.get("$2") == 6) {
        // read float
        Scanner in = new Scanner(System.in);
        regMap.put("$f12", Float.floatToIntBits(in.nextFloat()));  
        
      } else if (regMap.get("$2") == 7) {
        // read double
    	  Scanner in = new Scanner(System.in);
    	  double d = in.nextDouble();
    	  long l = Double.doubleToLongBits(d);
        regMap.put("$f12", (int) (l >> 32));
        regMap.put("$f13", (int) l);
        
      } else if (regMap.get("$2") == 8) {
        // read string
      	Scanner in = new Scanner(System.in);
      	String input = in.nextLine();
      	SymbolEntry e = prog.symbolAddrMap.get(regMap.get("$4"));
      	e.size = input.length();
      	e.t = TYPE.STRING;
      	memory.putData(regMap.get("$4"), input);
      	
      } else if (regMap.get("$2") == 9) {
        // allocate mem
        int size = regMap.get("$4"); 
        int addr = regMap.get("$2");
        SymbolEntry e = new SymbolEntry ("",size, addr);
        prog.symbolAddrMap.put(addr, e);
        memory.newSpace(size);
        
      } else if (regMap.get("$2") == 10) {
        // exit
        System.exit(0);
      }      
      incPC();
      return;
    }
    
    // Arithmetic Operations
    if (op.equals("add") || op.equals("addi") || op.equals("addu")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg3());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign((Integer)rop1.read() + (Integer)rop2.read());
      incPC();
      return;
    }
    
    if (op.equals("sub") || op.equals("subi") || op.equals("subu")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign((Integer)rop1.read() - (Integer)rop2.read());
      incPC();
      return;
    }
    
    if (op.equals("mult")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      int lo = (((Integer)rop1.read() * (Integer)rop2.read()) << 32) >> 32;
      int hi = (((Integer)rop1.read() * (Integer)rop2.read())) >> 32;
      regMap.put("$lo", lo);
      regMap.put("$hi", hi);
      incPC();
      return;
    }
    
    // Pseudo
    if (op.equals("mul")) {
        Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign((Integer)rop1.read() * (Integer)rop2.read());
        incPC();
        return;
    }

    if (op.equals("div")) {
        Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
        Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
        int lo = ((Integer)rop1.read() / (Integer)rop2.read());
        int hi = ((Integer)rop1.read() % (Integer)rop2.read());
        regMap.put("$lo", lo);
        regMap.put("$hi", hi);
        incPC();
        return;
    }
    
    
    // Floating Point Arithmetic
    if (op.equals("add.s")) {
        Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
        float f1 = Float.intBitsToFloat((Integer)rop1.read());
        float f2 = Float.intBitsToFloat((Integer)rop2.read());
        wop1.assign(Float.floatToIntBits(f1+f2));
        incPC();
        return;
    }
    
    if (op.equals("sub.s")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      float f1 = Float.intBitsToFloat((Integer)rop1.read());
      float f2 = Float.intBitsToFloat((Integer)rop2.read());
      wop1.assign(Float.floatToIntBits(f1-f2));
      incPC();
      return;
    }
    
    if (op.equals("mul.s")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      float f1 = Float.intBitsToFloat((Integer)rop1.read());
      float f2 = Float.intBitsToFloat((Integer)rop2.read());
      wop1.assign(Float.floatToIntBits(f1*f2));
      incPC();
      return;
    }
    
    if (op.equals("div.s")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      float f1 = Float.intBitsToFloat((Integer)rop1.read());
      float f2 = Float.intBitsToFloat((Integer)rop2.read());
      wop1.assign(Float.floatToIntBits(f1/f2));
      incPC();
      return;
    }
    
    // The double part!
    if (op.equals("add.d")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      Readable<Object> rop2 = null, rop4 = null;
      Assignable<Object> wop2 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg3()));
        wop2 = oprFact.buildLValue(Util.getPairedRegName(inst.getArg1()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      long l1 = (((long)rop1.read()) << 32) | (((long)rop2.read() & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)rop3.read()) << 32) | (((long)rop4.read() & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      
      long l3 = Double.doubleToLongBits(d1 + d2);
      
      int low = (int) l3; 
      int high = (int) (l3 >> 32);
      wop1.assign(high);
      wop2.assign(low);
      incPC();
      return;
    }
    
    if (op.equals("sub.d")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      Readable<Object> rop2 = null, rop4 = null;
      Assignable<Object> wop2 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg3()));
        wop2 = oprFact.buildLValue(Util.getPairedRegName(inst.getArg1()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      long l1 = (((long)rop1.read()) << 32) | (((long)rop2.read() & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)rop3.read()) << 32) | (((long)rop4.read() & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      
      long l3 = Double.doubleToLongBits(d1 - d2);
      
      int low = (int) l3; 
      int high = (int) (l3 >> 32);
      wop1.assign(high);
      wop2.assign(low);
      incPC();
      return;
    }
    
    if (op.equals("mul.d")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      Readable<Object> rop2 = null, rop4 = null;
      Assignable<Object> wop2 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg3()));
        wop2 = oprFact.buildLValue(Util.getPairedRegName(inst.getArg1()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      int irop1 = (int)rop1.read();
      int irop2 = (int)rop2.read();
      int irop3 = (int)rop3.read();
      int irop4 = (int)rop4.read();
      long l1 = (((long)irop1)<< 32) | (((long)irop2 & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)irop3) << 32) | (((long)irop4 & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      
      long l3 = Double.doubleToLongBits(d1 * d2);
      
      int low = (int) l3; 
      int high = (int) (l3 >> 32);
      wop1.assign(high);
      wop2.assign(low);
      incPC();
      return;
    }
    
    if (op.equals("div.d")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      Readable<Object> rop2 = null, rop4 = null;
      Assignable<Object> wop2 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg3()));
        wop2 = oprFact.buildLValue(Util.getPairedRegName(inst.getArg1()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      long l1 = (((long)rop1.read()) << 32) | (((long)rop2.read() & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)rop3.read()) << 32) | (((long)rop4.read() & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      
      long l3 = Double.doubleToLongBits(d1 / d2);
      
      int low = (int) l3; 
      int high = (int) (l3 >> 32);
      wop1.assign(high);
      wop2.assign(low);
      incPC();
      return;
    }
    
    // Floating part - Branch
    if(op.equals("bc1t")){
    	if(regMap.get("$cond") == 1){
    		if (labelMap.contains(inst.getArg1())) {
          setPC(labelMap.get(inst.getArg1()));
        } else if (regMap.validRegister(inst.getArg1())){
          int offset = regMap.get(inst.getArg1());
          incPC(offset);
        } else if (Util.isInteger(inst.getArg1())) {
          int offset = Integer.parseInt(inst.getArg1());
          incPC(offset);    
        } else {
          runningErr(inst);
        }
    	}else{
    		incPC();
    	}
      return;
    }
    
    
    if(op.equals("bc1f")){
      if(regMap.get("$cond") == 0){
        if (labelMap.contains(inst.getArg1())) {
          setPC(labelMap.get(inst.getArg1()));
        } else if (regMap.validRegister(inst.getArg1())){
          int offset = regMap.get(inst.getArg1());
          incPC(offset);
        } else if (Util.isInteger(inst.getArg1())) {
          int offset = Integer.parseInt(inst.getArg1());
          incPC(offset);    
        } else {
          runningErr(inst);
        }
      }else{
        incPC();
      }
      return;
    }
    
  	if(op.equals("c.eq.s")){
  		Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
  		Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
  		float f1 = Float.intBitsToFloat((Integer)rop1.read());
  		float f2 = Float.intBitsToFloat((Integer)rop2.read());
  		if ( f1 == f2 ) {
      	regMap.put("$cond", 1);
      } else {
      	regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.ne.s")){
  	  Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      float f1 = Float.intBitsToFloat((Integer)rop1.read());
      float f2 = Float.intBitsToFloat((Integer)rop2.read());
      if ( f1 != f2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.lt.s")){
  	  Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      float f1 = Float.intBitsToFloat((Integer)rop1.read());
      float f2 = Float.intBitsToFloat((Integer)rop2.read());
      if ( f1 < f2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.le.s")){
  	  Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      float f1 = Float.intBitsToFloat((Integer)rop1.read());
      float f2 = Float.intBitsToFloat((Integer)rop2.read());
      if ( f1 <= f2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.gt.s")){
  	  Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      float f1 = Float.intBitsToFloat((Integer)rop1.read());
      float f2 = Float.intBitsToFloat((Integer)rop2.read());
      if ( f1 > f2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.ge.s")){
  	  Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      float f1 = Float.intBitsToFloat((Integer)rop1.read());
      float f2 = Float.intBitsToFloat((Integer)rop2.read());
      if ( f1 >= f2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.eq.d")){
  	  Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = null, rop4 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg1()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      long l1 = (((long)rop1.read()) << 32) | (((long)rop2.read() & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)rop3.read()) << 32) | (((long)rop4.read() & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      if ( d1 == d2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.ne.d")){
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = null, rop4 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg1()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      long l1 = (((long)rop1.read()) << 32) | (((long)rop2.read() & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)rop3.read()) << 32) | (((long)rop4.read() & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      if ( d1 != d2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.lt.d")){
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = null, rop4 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg1()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      long l1 = (((long)rop1.read()) << 32) | (((long)rop2.read() & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)rop3.read()) << 32) | (((long)rop4.read() & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      if ( d1 < d2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.le.d")){
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = null, rop4 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg1()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      long l1 = (((long)rop1.read()) << 32) | (((long)rop2.read() & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)rop3.read()) << 32) | (((long)rop4.read() & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      if ( d1 <= d2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.gt.d")){
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = null, rop4 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg1()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      long l1 = (((long)rop1.read()) << 32) | (((long)rop2.read() & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)rop3.read()) << 32) | (((long)rop4.read() & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      if ( d1 > d2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
  	
  	if(op.equals("c.ge.d")){
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());      
      Readable<Object> rop3 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = null, rop4 = null;
      try {
        rop2 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg1()));
        rop4 = oprFact.buildRValue(Util.getPairedRegName(inst.getArg2()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
      }
      long l1 = (((long)rop1.read()) << 32) | (((long)rop2.read() & 0xffffffffL));
      double d1 = Double.longBitsToDouble(l1);
      long l2 = (((long)rop3.read()) << 32) | (((long)rop4.read() & 0xffffffffL));
      double d2 = Double.longBitsToDouble(l2);
      if ( d1 >= d2 ) {
        regMap.put("$cond", 1);
      } else {
        regMap.put("$cond", 0);
      }
      incPC();
      return;
  	}
    
    
    // Comparison
    if (op.equals("slti") || op.equals("slt")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      if ((Integer)rop1.read() < (Integer)rop2.read()) {
        wop1.assign(1);
      } else {
        wop1.assign(0);
      }
      incPC();
      return;
    }
    
    if (op.equals("sltiu")||op.equals("sltu")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      if ((Integer)rop1.read() < (Integer)rop2.read()) {
        wop1.assign(1);
      } else {
        wop1.assign(0);
      }
      incPC();
      return;
    }
    
    // Branch
    if (op.equals("beq")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      // pseudo 
      if (((Integer)rop1.read()).equals((Integer)rop2.read())) {
        if (labelMap.contains(inst.getArg3())) {
          String label = inst.getArg3();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg3())){
          int offset = regMap.get(inst.getArg3());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg3())) {
          int offset = Integer.parseInt(inst.getArg3());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }     
      } else {
        incPC();
      }
      return;
    }
  
    if (op.equals("bne")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      // pseudo 
      if (!((Integer)rop1.read()).equals((Integer)rop2.read())) {
        if (labelMap.contains(inst.getArg3())) {
          String label = inst.getArg3();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg3())){
          int offset = regMap.get(inst.getArg3());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg3())) {
          int offset = Integer.parseInt(inst.getArg3());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }     
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("blt")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      // pseudo 
      if (((Integer)rop1.read()) < ((Integer)rop2.read())) {
        if (labelMap.contains(inst.getArg3())) {
          String label = inst.getArg3();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg3())){
          int offset = regMap.get(inst.getArg3());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg3())) {
          int offset = Integer.parseInt(inst.getArg3());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }     
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("ble")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      // pseudo 
      if (((Integer)rop1.read()) <= ((Integer)rop2.read())) {
        if (labelMap.contains(inst.getArg3())) {
          String label = inst.getArg3();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg3())){
          int offset = regMap.get(inst.getArg3());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg3())) {
          int offset = Integer.parseInt(inst.getArg3());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }     
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("bgt")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      // pseudo 
      if (((Integer)rop1.read()) > ((Integer)rop2.read())) {
        if (labelMap.contains(inst.getArg3())) {
          String label = inst.getArg3();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg3())){
          int offset = regMap.get(inst.getArg3());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg3())) {
          int offset = Integer.parseInt(inst.getArg3());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }     
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("bge")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg2());
      // pseudo 
      if (((Integer)rop1.read()) >= ((Integer)rop2.read())) {
        if (labelMap.contains(inst.getArg3())) {
          String label = inst.getArg3();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg3())){
          int offset = regMap.get(inst.getArg3());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg3())) {
          int offset = Integer.parseInt(inst.getArg3());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }     
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("beqz")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      if ((Integer)rop1.read() == 0) {
        if (labelMap.contains(inst.getArg2())) {
          String label = inst.getArg2();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg2())){
          int offset = regMap.get(inst.getArg2());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg2())) {
          int offset = Integer.parseInt(inst.getArg2());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }   
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("bnez")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      if ((Integer)rop1.read() != 0) {
        if (labelMap.contains(inst.getArg2())) {
          String label = inst.getArg2();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg2())){
          int offset = regMap.get(inst.getArg2());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg2())) {
          int offset = Integer.parseInt(inst.getArg2());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }   
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("bltz")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      if ((Integer)rop1.read() < 0) {
        if (labelMap.contains(inst.getArg2())) {
          String label = inst.getArg2();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg2())){
          int offset = regMap.get(inst.getArg2());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg2())) {
          int offset = Integer.parseInt(inst.getArg2());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }   
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("blez")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      if ((Integer)rop1.read() <= 0) {
        if (labelMap.contains(inst.getArg2())) {
          String label = inst.getArg2();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg2())){
          int offset = regMap.get(inst.getArg2());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg2())) {
          int offset = Integer.parseInt(inst.getArg2());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }   
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("bgtz")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      if ((Integer)rop1.read() > 0) {
        if (labelMap.contains(inst.getArg2())) {
          String label = inst.getArg2();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg2())){
          int offset = regMap.get(inst.getArg2());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg2())) {
          int offset = Integer.parseInt(inst.getArg2());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }   
      } else {
        incPC();
      }
      return;
    }
    
    if (op.equals("bgez")) {
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg1());
      if ((Integer)rop1.read() >= 0) {
        if (labelMap.contains(inst.getArg2())) {
          String label = inst.getArg2();
          setPC(labelMap.get(label));          
        } else if (regMap.validRegister(inst.getArg2())){
          int offset = regMap.get(inst.getArg2());
          incPC(offset*4);
        } else if (Util.isInteger(inst.getArg2())) {
          int offset = Integer.parseInt(inst.getArg2());
          incPC(offset*4);    
        } else {
          runningErr(inst);
        }   
      } else {
        incPC();
      }
      return;
    }
    
    // Unconditional Jump
    // TODO
    if (op.equals("jr")) {
      if (labelMap.contains(inst.getArg1())) {
        setPC(labelMap.get(inst.getArg1()));
      } else if (regMap.validRegister(inst.getArg1())){
        setPC(regMap.get(inst.getArg1()));
      } else if (Util.isInteger(inst.getArg1())){
        int addr = Integer.valueOf(inst.getArg1());
        setPC(addr);
      } else {
        runningErr(inst);
      } 
      return;
    }
    
    if (op.equals("j") || op.equals("b")) {
      if (labelMap.contains(inst.getArg1())) {
        setPC(labelMap.get(inst.getArg1()));
      } else if (regMap.validRegister(inst.getArg1())){
        setPC(regMap.get(inst.getArg1()));
      } else if (Util.isInteger(inst.getArg1())){
        int addr = Integer.valueOf(inst.getArg1());
        setPC(addr);
      } else {
        runningErr(inst);
      } 
      return;
    }
    
    if (op.equals("jal")) {
      regMap.put("$31", regMap.get("$pc") + 4);
      if (labelMap.contains(inst.getArg1())) {
        setPC(labelMap.get(inst.getArg1()));
      } else if (regMap.validRegister(inst.getArg1())){
        setPC(regMap.get(inst.getArg1()));
      } else if (Util.isInteger(inst.getArg1())){
        int addr = Integer.valueOf(inst.getArg1());
        setPC(addr);
      } else {
        runningErr(inst);
      } 
      return;
    }
    
    // Logical
    if(op.equals("and")){
    	Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign((Integer)rop1.read() & (Integer)rop2.read());
      incPC();
      return;
    }
    
    if(op.equals("or")){
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign((Integer)rop1.read() | (Integer)rop2.read());
      incPC();
      return;
    }

    if(op.equals("sll")){
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign((Integer)rop1.read() << (Integer)rop2.read());
      incPC();
      return;
    }

    if(op.equals("srl")){
      Readable<Object> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Object> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Object> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign((Integer)rop1.read() >> (Integer)rop2.read());
      incPC();
      return;
    }
   
    // unknown instruction
    runningErr(inst);
  }



  public void runningErr (Instruction inst) {
    System.err.print("Err: " + inst.getOp());
    if (inst.getArg1() != null) {
      System.err.print(" " + inst.getArg1());
    }
    if (inst.getArg2() != null) {
      System.err.print(" " + inst.getArg2());
    }
    if (inst.getArg3() != null) {
      System.err.print(" " + inst.getArg3());
    }
    System.err.println();
    System.exit(-1);
  }
  
  public void run(String fileName) {
    Program.initialize(fileName);
    regMap = RegisterMap.getInstance();
    prog = Program.getInstance();
    labelMap = LabelMap.getInstance();
    memory = Memory.getInstance();
    oprFact = OperandFactory.getInstance();
    
    // initialize $pc
    if (!regMap.regMap.containsKey("$pc")) {
      if (labelMap.contains("__start")) {
        regMap.put("$pc", labelMap.get("__start"));
      } else if (labelMap.contains("main")) {
        regMap.put("$pc", labelMap.get("main"));
      } else {
        regMap.put("$pc", new Integer(0x00400000)); 
      }
    }
    // initialize stack pointer
    regMap.put("$29", new Integer(0x7fffffff));
    // initialize frame pointer
    regMap.put("$30", new Integer(0x7fffffff));
        
    Instruction inst = prog.getInst(regMap.get("$pc"));
    while (inst != null) {
//      inst.print();
//      try {
      execute(inst);
//      } catch (Exception e) {
//        runningErr(inst);
//      }
      inst = prog.getInst(regMap.get("$pc"));
    }
  }
  
  public static void main(String[] args) throws IOException {
//    if (args.length < 1) {
//      System.out.println("Please input the filename: MyVM <filename>");
//      System.exit(0);
//    } else {
//      String fileName = args[0];
//      //System.out.println("Running "+fileName+".");
//      MyVM vm = new MyVM();
//      //TODO: for test
//      //vm.out = new PrintWriter(new BufferedWriter(new FileWriter(fileName + ".out")));
//      vm.run(fileName);
//      //vm.out.close();
//    }
    
    MyVM vm = new MyVM();
  
    // TODO Input test file HERE
//   vm.run("files/hello.asm");
//   vm.run("files/simple-prog.asm");
//   vm.run("files/funcall_factorial.asm");
//   vm.run("files/int_arithmatic.asm");
//   vm.run("files/float_arithmatic.asm");
   vm.run("files/double_arithmatic.asm");
//   vm.run("files/io_string.asm");
//   vm.run("files/io_float_double.asm");
//    vm.run("files/fib_array.asm");

//   vm.run("files/printf.asm"); 
//   vm.run("files/palindrome.asm");

  }
}
