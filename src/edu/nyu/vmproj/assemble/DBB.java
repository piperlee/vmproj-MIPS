package edu.nyu.vmproj.assemble;

import java.util.Arrays;

public class DBB {
  private Integer start;
  private Integer end;
  
  public DBB(Integer s, Integer e){
    this.start = s;
    this.end = e;
  }
  
  public Integer getStart(){
    return this.start;
  }
  
  public Integer getEnd(){
    return this.end;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof DBB)) return false;
    DBB dbb = (DBB)obj;
    return this.start == dbb.getStart() && this.end == dbb.getEnd();
  }
  
  @Override
  public int hashCode(){
    // TODO
    int [] a = new int[2];
    a[0] = this.start;
    a[1] = this.end;
    return Arrays.hashCode(a);
    //return this.start.hashCode() + this.end.hashCode();
  }
  
}
