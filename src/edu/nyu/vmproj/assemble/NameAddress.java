package edu.nyu.vmproj.assemble;

public class NameAddress implements Readable<Integer>,Assignable<Integer>{
  private String name;
  private Program prog;
  
  public NameAddress(String name) {
    this.name = name;
    prog = Program.getInstance();
  }
  
  @Override
  public void assign(Integer v) {
    prog.symbolTable.put(name, v);
  }

  @Override
  public Integer read() {
    return prog.symbolTable.get(name);
  }
}