package edu.nyu.vmproj.assemble;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


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
  
  private void incPC() {
    regMap.put("$pc", new Integer(regMap.get("$pc") + 4));
  }
  
  private void execute(Instruction inst) {
    String op = inst.getOp();
    // I. Memory
    if (op.equals("li")) {
      Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop2.read());
      incPC();
      return;
    }
    
    if(op.equals("li.s")||op.equals("li.d")){
    	//TODO: 读取label的数据
    	/*if (labelMap.contains(inst.getArg2())) {
    		setPC(labelMap.get(inst.getArg2()));
    		} else if (regMap.contains(inst.getArg1())){
    			setPC(regMap.get(inst.getArg1()));
    			} else {
    				int addr = Integer.valueOf(inst.getArg1());
    				setPC(addr);
    				}
    		incPC();
    	*/
    	//regMap.put("$31", regMap.get("$pc") + 4);
        return;
    }
    
    if (op.equals("la")) {
      Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop2.read());
      incPC();
      return;
    }
    
    if (op.equals("move")) {
      Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop2.read());
      incPC();
      return;
    }
    
    if (op.equals("sw")) {
      Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg2());
      wop1.assign(rop1.read());
      incPC();
      return;
    }
    
    if (op.equals("lw")) {
    	Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read());
        incPC();
        return;
    }
    
    if (op.equals("lui")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign((int) (Math.pow(2,16)*rop1.read()));
        incPC();
        return;
      }
    
    if (op.equals("mflo")) {
      Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(regMap.get("$lo"));
      incPC();
      return;
    }
    
    // System call
    // TODO
    if (op.equals("syscall")) {
      if (regMap.get("$2") == 1) {
        // print int
        System.out.print(regMap.get("$4"));
      } else if (regMap.get("$2") == 2) {
        // print float
    	System.out.print(regMap.get("$f12"));
      } else if (regMap.get("$2") == 3) {
        // print double
    	  System.out.print(regMap.get("$f12"));
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
        regMap.put("$f12", (int) in.nextFloat());  
      } else if (regMap.get("$2") == 7) {
        // read double
    	  Scanner in = new Scanner(System.in);
          regMap.put("$f12", (int) in.nextDouble());
      } else if (regMap.get("$2") == 8) {
        // read string
    	Scanner in = new Scanner(System.in);
    	memory.putData(regMap.get("$4"), in);
      } else if (regMap.get("$2") == 10) {
        // exit
        System.exit(0);
      }      
      incPC();
      return;
    }
    
    // Arithmetic Operations
    // TODO
    if (op.equals("add") || op.equals("addi")) {
      Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg3());
      Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() + rop2.read());
      incPC();
      return;
    }
    
    if (op.equals("sub") || op.equals("subi")) {
      Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() - rop2.read());
      incPC();
      return;
    }
    
    if (op.equals("mult")) {
      Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
      int lo = ((rop1.read() * rop2.read()) << 32) >> 32;
      regMap.put("$lo", lo);
      incPC();
      return;
    }
    
    if (op.equals("mul")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read() * rop2.read());
        incPC();
        return;
    }

    if (op.equals("div")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
        int lo = ((rop1.read() / rop2.read()) << 32) >> 32;
        int hi = ((rop1.read() % rop2.read()) << 32) >> 32;
        regMap.put("$lo", lo);
        regMap.put("$hi", hi);
        incPC();
        return;
    }
    
    if(op.equals("mfc0")){
    	//Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop2.read());
        incPC();
        return;
    }
    
    // Floating Point Arithmetic
    if (op.equals("add.s")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read() + rop2.read());
        incPC();
        return;
    }
    
    if (op.equals("sub.s")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read() - rop2.read());
        incPC();
        return;
    }
    
    if (op.equals("mul.s")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        int lo = ((rop1.read() * rop2.read()) << 32) >> 32;
        regMap.put("$lo", lo);
        incPC();
        return;
    }
    
    if (op.equals("div.s")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        int lo = ((rop1.read() / rop2.read()) << 32) >> 32;
        regMap.put("$lo", lo);
        incPC();
        return;
    }
    
    // The double part!
    if (op.equals("add.d")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read() + rop2.read());
        incPC();
        return;
    }
    
    if (op.equals("sub.d")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read() - rop2.read());
        incPC();
        return;
    }
    
    if (op.equals("mul.d")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
        int lo = ((rop1.read() * rop2.read()) << 32) >> 32;
        regMap.put("$lo", lo);
        incPC();
        return;
    }
    
    if (op.equals("div.d")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        int lo = ((rop1.read() / rop2.read()) << 32) >> 32;
        regMap.put("$lo", lo);
        incPC();
        return;
    }
    // Floating part - Data transfer
    if (op.equals("lwc1")) {
    	Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read());
        incPC();
        return;
    }
    if (op.equals("swc1")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg2());
        wop1.assign(rop1.read());
        incPC();
        return;
    }
    // Floating part - Branch
    if(op.equals("bc1t")){
    	if(regMap.get("$cond")==1){
    		if (labelMap.contains(inst.getArg1())) {
    	          setPC(labelMap.get(inst.getArg1()));
    	        } else if (regMap.contains(inst.getArg1())){
    	          setPC(regMap.get(inst.getArg1()));
    	        } else {
    	          int addr = Integer.valueOf(inst.getArg1());
    	          setPC(addr);
    	        }
    	}else{
    		incPC();
    	}
    	//regMap.put("$31", regMap.get("$pc") + 4);
        return;
    }
    if(op.equals("bc1f")){
    	if(regMap.get("$cond")==0){
    		if (labelMap.contains(inst.getArg1())) {
    	          setPC(labelMap.get(inst.getArg1()));
    	        } else if (regMap.contains(inst.getArg1())){
    	          setPC(regMap.get(inst.getArg1()));
    	        } else {
    	          int addr = Integer.valueOf(inst.getArg1());
    	          setPC(addr);
    	        }
    	}else{
    		incPC();
    	}
    	//regMap.put("$31", regMap.get("$pc") + 4);
        return;
    }
	if(op.equals("c.eq.s")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() == rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.ne.s")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() != rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.lt.s")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() < rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.le.s")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() <= rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.gt.s")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() > rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.ge.s")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() >= rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.eq.d")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() == rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.ne.d")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() != rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.lt.d")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() < rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.le.d")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() <= rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.gt.d")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() > rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
	if(op.equals("c.ge.d")){
		Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
		Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
		if (rop1.read() >= rop2.read()) {
        	regMap.put("$cond", 1);
        } else {
        	regMap.put("$cond", 0);
        }
        incPC();
        return;
	}
    
    
    // Comparison
    if (op.equals("slti")||op.equals("slt")) {
      Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
      Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
      Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
      if (rop1.read() < rop2.read()) {
        wop1.assign(1);
      } else {
        wop1.assign(0);
      }
      incPC();
      return;
    }
    
    if (op.equals("sltiu")||op.equals("sltu")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        if (rop1.read() < rop2.read()) {
          wop1.assign(1);
        } else {
          wop1.assign(0);
        }
        incPC();
        return;
    }
    
    // Branch
    if (op.equals("beq")) {
      Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
      Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
      String label = inst.getArg3();
      if (rop1.read() == rop2.read()) {
        setPC(labelMap.get(label));
      } else {
        incPC();
      }
      return;
    }
  
    if (op.equals("bne")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
        String label = inst.getArg3();
        if (rop1.read() != rop2.read()) {
          setPC(labelMap.get(label));
        } else {
          incPC();
        }
        return;
      }
    
    if (op.equals("blez")) {
        Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg1());
        //Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg2());
        String label = inst.getArg2();
        if (rop1.read() <= 0) {
          setPC(labelMap.get(label));
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
      } else if (regMap.contains(inst.getArg1())){
        setPC(regMap.get(inst.getArg1()));
      } else {
        int addr = Integer.valueOf(inst.getArg1());
        setPC(addr);
      }
      return;
    }
    
    if (op.equals("j")) {
        if (labelMap.contains(inst.getArg1())) {
          setPC(labelMap.get(inst.getArg1()));
        } else if (regMap.contains(inst.getArg1())){
          setPC(regMap.get(inst.getArg1()));
        } else {
          int addr = Integer.valueOf(inst.getArg1());
          setPC(addr);
        }
        return;
    }
    
    if (op.equals("jal")) {
      regMap.put("$31", regMap.get("$pc") + 4);
      if (labelMap.contains(inst.getArg1())) {
        setPC(labelMap.get(inst.getArg1()));
      } else if (regMap.contains(inst.getArg1())){
        setPC(regMap.get(inst.getArg1()));
      } else {
        int addr = Integer.valueOf(inst.getArg1());
        setPC(addr);
      }
      return;
    }
    
    // Logical
    // TODO
    if(op.equals("and")){
    	Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read() & rop2.read());
        incPC();
        return;
    }
    
    if(op.equals("or")){
    	Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read() | rop2.read());
        incPC();
        return;
    }
	
    if(op.equals("sll")){
    	Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read() << rop2.read());
        incPC();
        return;
	}
	
    if(op.equals("srl")){
    	Readable<Integer> rop1 = oprFact.buildRValue(inst.getArg2());
        Readable<Integer> rop2 = oprFact.buildRValue(inst.getArg3());
        Assignable<Integer> wop1 = oprFact.buildLValue(inst.getArg1());
        wop1.assign(rop1.read() >> rop2.read());
        incPC();
        return;
	}    
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
      if (labelMap.contains("main")) {
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
      execute(inst);
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

//   vm.run("files/add2.asm");
//   vm.run("files/hello.asm");
//   vm.run("files/simple-prog.asm");
//   vm.run("files/fact-2.asm");

//   vm.run("files/atoi-1.asm");//NumberFormatException
//   vm.run("files/factorial.asm");//NullPointerException
//   vm.run("files/multiples.asm");//修改了getArg()23，可以运行，结果不对
//   vm.run("files/palindrome.asm");//NumberFormatException
  }
}
