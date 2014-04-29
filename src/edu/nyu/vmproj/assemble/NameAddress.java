package edu.nyu.vmproj.assemble;

public class NameAddress implements Readable<Object>,Assignable<Object>{
  private String name;
  private Program prog;
  
  public NameAddress(String name) {
    this.name = name;
    prog = Program.getInstance();
  }
  
  @Override
  public void assign(Object v) {
    prog.symbolTable.put(name, (Integer)v);
  }

  @Override
  public Integer read() {
    return prog.symbolTable.get(name);
  }
}