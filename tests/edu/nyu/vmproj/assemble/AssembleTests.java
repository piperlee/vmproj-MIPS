package edu.nyu.vmproj.assemble;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class AssembleTests {

  public void assertFileEqual(File a, File b) throws IOException {
    BufferedReader fileA = new BufferedReader(new FileReader(a));
    BufferedReader fileB = new BufferedReader(new FileReader(b));
    while (true) {
      String linea = fileA.readLine();
      String lineb = fileB.readLine();
      if ((linea == null) && (lineb == null)) break; 
      Assert.assertEquals("failure - strings not same", linea, lineb);
    }
    fileA.close();
    fileB.close();
  }
//  
//  @Test
//  public void test_mem() throws IOException {
//    MyVM.main(new String[] {"files/mem.vm"});
//    File outFile = new File("files/mem.vm.out");
//    File stdFile = new File("files/mem.out.std");
//    assertFileEqual(outFile, stdFile);
//  }
//
//  @Test
//  public void test_fact() throws IOException {
//    MyVM.main(new String[] {"files/fact.vm"});
//    File outFile = new File("files/fact.vm.out");
//    File stdFile = new File("files/fact.out.std");
//    assertFileEqual(outFile, stdFile);
//  }
// 
//  @Test
//  public void test_fib() throws IOException {
//    MyVM.main(new String[] {"files/fib.vm"});
//    File outFile = new File("files/fib.vm.out");
//    File stdFile = new File("files/fib.out.std");
//    assertFileEqual(outFile, stdFile);
//  }
//  
//  @Test
//  public void test_jsr() throws IOException {
//    MyVM.main(new String[] {"files/jsr.vm"});
//    File outFile = new File("files/jsr.vm.out");
//    File stdFile = new File("files/jsr.out.std");
//    assertFileEqual(outFile, stdFile);
//  }
//  
//  @Test
//  public void test_loop() throws IOException {
//    MyVM.main(new String[] {"files/loop.vm"});
//    File outFile = new File("files/loop.vm.out");
//    File stdFile = new File("files/loop.out.std");
//    assertFileEqual(outFile, stdFile);
//  }
//  
  @Test
  public void test_comp() throws IOException {
    MyVM.main(new String[] {"files/comp.vm"});
    File outFile = new File("files/comp.vm.out");
    File stdFile = new File("files/comp.out.std");
    assertFileEqual(outFile, stdFile);
  }

}
