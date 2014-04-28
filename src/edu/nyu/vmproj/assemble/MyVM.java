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
        
      } else if (regMap.get("$2") == 3) {
        // print double
        
      } else if (regMap.get("$2") == 4) {
        // print string
        System.out.print(memory.getData(regMap.get("$4")));
        
      } else if (regMap.get("$2") == 5) {
        // read int
        Scanner in = new Scanner(System.in);
        regMap.put("$2", in.nextInt());
        
      } else if (regMap.get("$2") == 6) {
        // read float
      
      } else if (regMap.get("$2") == 7) {
        // read double
        
      } else if (regMap.get("$2") == 8) {
        // read string
        
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

    // Comparison
    if (op.equals("slti")) {
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
    
    // TODO Inptu test file HERE
    
//    vm.run("files/factorial.asm");
    vm.run("files/fact-2.asm");
//    vm.run("files/hello.asm");
//    vm.run("files/simple-prog.asm");
//    vm.run("files/multiples.asm");
  }
}
