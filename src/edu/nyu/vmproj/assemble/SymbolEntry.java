package edu.nyu.vmproj.assemble;

public class SymbolEntry {
  
  public enum TYPE {
    INT, DOUBLE, FLOAT, STRING, UNKNOWN
  }
  
  String name;
  int size;
  int capacity;
  int startingAddr;
  int endingAddr;
  TYPE t;
  boolean isSpace;
//  Object data;
  
  public SymbolEntry (String name, int size, int startingAddr, TYPE t) {
    this.name = name;
    this.size = size;
    this.capacity = size;
    this.startingAddr = startingAddr;
    this.endingAddr = this.size + this.startingAddr;
    this.t = t;
    this.isSpace = false;
//    this.data = data;    
  }
  
  public SymbolEntry (String name, int capacity, int startingAddr) {
    this.name = name;
    this.capacity = capacity;
    this.size = 0;
    this.startingAddr= startingAddr;
    this.endingAddr = this.size + this.startingAddr;
    this.t = TYPE.UNKNOWN;
    this.isSpace = true;
  }
}
