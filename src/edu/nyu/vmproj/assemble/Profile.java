package edu.nyu.vmproj.assemble;

import java.util.HashMap;
import java.util.Map;

public class Profile {
  private static Profile instance;
  private HashMap<DBB, Integer> DBBMap;
  
  private Profile() {
    DBBMap = new HashMap<DBB, Integer>();
  }
  
  public static Profile getInstance() {
    if (instance == null) instance = new Profile();
    return instance;
  }
  
  public void updateDBBMap(DBB k) {
    if (!DBBMap.containsKey(k)) {
      DBBMap.put(k, new Integer(1));
    }else {
      DBBMap.put(k, DBBMap.get(k)+1);
    }
  }
  
  public Integer get(DBB k) {
    if (!DBBMap.containsKey(k)) {
      System.err.println("DBBMap "+k+" is not found!");
      return null;
    } else {
      return DBBMap.get(k);
    }
  }
  
  public boolean contains(DBB k) {
    return DBBMap.containsKey(k);
  }
  
  public void printDBBMap(){
    Program prog = Program.getInstance();
    for (Map.Entry<DBB, Integer> entry : DBBMap.entrySet()) {
      MyVM.out.println(entry.getValue());
      for (int i = entry.getKey().getStart(); i<= entry.getKey().getEnd(); i += 4){
        Instruction instr = prog.getInst(i);
        if (instr.getArgCount() == 0) {
          MyVM.out.println(instr.getOp());
        } else if (instr.getArgCount() == 1) {
          MyVM.out.println(instr.getOp()+" "+instr.getArg1());
        } else {
          MyVM.out.println(instr.getOp()+" "+instr.getArg1()+", "+instr.getArg2());
        }
      }
      MyVM.out.println();
    }    
  }
}
