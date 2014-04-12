package edu.nyu.vmproj.assemble;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class MyVM {
  // map: instr addr -> instr
  Program prog;
  // map: reg name -> value
  RegisterMap regMap;
  // map: label name -> instr addr
  LabelMap labelMap;
  // mem addr -> value
  Memory memory;
  
  OperandFactory oprFact;
  
  //TODO: for test
  PrintWriter out;
  
  private void setEIP(Integer i) {
    regMap.put("eip", new Integer(i));
  }
  
  private void incEIP() {
    regMap.put("eip", new Integer(regMap.get("eip") + 4));
  }
  
  private void setFLAGS(int v) {
    regMap.put("flags", new Integer(v));
  }
  
  private int getFLAGS() {
    return regMap.get("flags");
  }
  
  private void setREMAINDER(int v) {
    regMap.put("remainder", new Integer(v));
  }
  
  private int getREMAINDER() {
    return regMap.get("remainder");
  }
  
  private void execute(Instruction inst) {
    String op = inst.getOp();
    // I. Memory
    if (op.equals("mov")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop2.read());
      incEIP();
      return;
    }
    
    // II. Stack
    if (op.equals("push")) {
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      memory.push(rop1.read());
      incEIP();
      return;
    }
    
    if (op.equals("pop")) {
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(memory.pop());
      incEIP();
      return;
    }
    
    if (op.equals("pushf")) {
      memory.push(regMap.get("flags"));
      return;
    }
    
    if (op.equals("popf")) {
      setFLAGS(memory.pop());
      return;
    }
    
    // III. Calling Conventions
    if (op.equals("call")) {      
      memory.push(regMap.get("eip"));
      setEIP(labelMap.get(inst.getArg1()));
      return;
    }
    
    if (op.equals("ret")) {
      setEIP(memory.pop());
      incEIP();
      return;
    }
    
    // IV. Arithmetic Operations
    if (op.equals("inc")) {
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() + 1);
      incEIP();
      return;
    }
    
    if (op.equals("dec")) {
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() - 1);
      incEIP();
      return;
    }
    
    if (op.equals("add")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() + rop2.read());
      incEIP();
      return;
    }
    
    if (op.equals("sub")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() - rop2.read());
      incEIP();
      return;
    }
    
    if (op.equals("mul")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() * rop2.read());
      incEIP();
      return;
    }
    
    if (op.equals("div")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() / rop2.read());
      incEIP();
      return;
    }
    
    if (op.equals("mod")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      setREMAINDER(rop1.read() % rop2.read());
      incEIP();
      return;
    }
    
    if (op.equals("rem")) {
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(getREMAINDER());
      incEIP();
      return;
    }
    
    // V. Binary Operators
    if (op.equals("not")) {
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      wop1.assign(~rop1.read());
      incEIP();
      return;
    }
    
    if (op.equals("xor")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() ^ rop2.read());
      incEIP();
      return;
    }
    
    if (op.equals("or")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() | rop2.read());
      incEIP();
      return;
    }
    
    if (op.equals("and")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() & rop2.read());
      incEIP();
      return;
    }
    
    if (op.equals("shl")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() << rop2.read());
      incEIP();
      return;
    }
    
    if (op.equals("shr")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      Assignable wop1 = oprFact.buildLValue(inst.getArg1());
      wop1.assign(rop1.read() >> rop2.read());
      incEIP();
      return;
    }
    
    // VI. Comparison
    if (op.equals("cmp")) {
      Readable rop2 = oprFact.buildRValue(inst.getArg2());
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      setFLAGS(rop1.read() - rop2.read());
      incEIP();
      return;
    }
    
    // VII. Control Flow Manipulation
    if (op.equals("jmp")) {
      String label = inst.getArg1();      
      setEIP(labelMap.get(label));
      return;
    }
    
    if (op.equals("je")) {
      String label = inst.getArg1();
      if (getFLAGS() == 0) {
        setEIP(labelMap.get(label));
      } else incEIP();
      return;
    }
    
    if (op.equals("jne")) {
      String label = inst.getArg1();
      if (getFLAGS() != 0) {
        setEIP(labelMap.get(label));
      } else incEIP();
      return;
    }
    
    if (op.equals("jg")) {
      String label = inst.getArg1();
      if (getFLAGS() > 0) {
        setEIP(labelMap.get(label));
      } else incEIP();
      return;
    }
    
    if (op.equals("jge")) {
      String label = inst.getArg1();
      if (getFLAGS() >= 0) {
        setEIP(labelMap.get(label));
      } else incEIP();
      return;
    }
    
    if (op.equals("jl")) {
      String label = inst.getArg1();
      if (getFLAGS() < 0) {
        setEIP(labelMap.get(label));
      } else incEIP();
      return;
    }
      
    if (op.equals("jle")) {
      String label = inst.getArg1();
      if (getFLAGS() <= 0) {
        setEIP(labelMap.get(label));
      } else incEIP();
      return;
    }
    
    // VIII. Input/Output
    if (op.equals("prn")) {
      Readable rop1 = oprFact.buildRValue(inst.getArg1());
      // TODO: for test
      System.out.println(rop1.read());
      //out.println(rop1.read());
      incEIP();
      return;
    }
  }
  
  public void run(String fileName) {
    Program.initialize(fileName);
    regMap = RegisterMap.getInstance();
    prog = Program.getInstance();
    labelMap = LabelMap.getInstance();
    if (labelMap.contains("start")) {
      regMap.put("eip", labelMap.get("start"));         
    }else {
      regMap.put("eip", new Integer(0));      
    }
    regMap.put("esp", new Integer(10000000));
    regMap.put("ebp", new Integer(10000000));
    memory = Memory.getInstance();
    oprFact = OperandFactory.getInstance();
    Instruction inst = prog.getInst(regMap.get("eip"));
    while (inst != null) {
      execute(inst);
      inst = prog.getInst(regMap.get("eip"));
    }
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.out.println("Please input the filename: MyVM <filename>");
      System.exit(0);
    } else {
      String fileName = args[0];
      //System.out.println("Running "+fileName+".");
      MyVM vm = new MyVM();
      //TODO: for test
      //vm.out = new PrintWriter(new BufferedWriter(new FileWriter(fileName + ".out")));
      vm.run(fileName);
      //vm.out.close();
    }
  }
}
