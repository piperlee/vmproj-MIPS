package edu.nyu.vmproj.assemble;

public class Util {
  public static Long integerToLong(Integer i) {
    return Long.valueOf(String.valueOf(i));
  }
  
  public static boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
    } catch (NumberFormatException e){
      return false;
    }
    return true;
  }
  
  public static boolean isFloat(String str) {
    try {
      Float.parseFloat(str);
    } catch (NumberFormatException e){
      return false;
    }
    return true;
  }
  
  public static boolean isDouble(String str) {
    try {
      Double.parseDouble(str);
    } catch (NumberFormatException e){
      return false;
    }
    return true;
  }
  
  public static String getPairedRegName(String regName) throws Exception {
    OperandFactory opFac = OperandFactory.getInstance(); 
    String name = regName.toLowerCase();
    if (!opFac.regNameMap.containsKey(name) || regName.charAt(1) != 'f') {
      throw new Exception ("float reg name error");
    }
    Integer idx = Integer.valueOf(name.substring(2));
    if ((idx%2) != 0) {
      throw new Exception ("float reg name error");
    }
    return "$f"+String.valueOf(idx+1);
  }
  
  public static byte[] toByta(int data) {
    return new byte[] {
        (byte)((data >> 24) & 0xff),
        (byte)((data >> 16) & 0xff),
        (byte)((data >> 8) & 0xff),
        (byte)((data >> 0) & 0xff),
    };
  }

  public static byte[] toByta(long data) {
      return new byte[] {
          (byte)((data >> 56) & 0xff),
          (byte)((data >> 48) & 0xff),
          (byte)((data >> 40) & 0xff),
          (byte)((data >> 32) & 0xff),
          (byte)((data >> 24) & 0xff),
          (byte)((data >> 16) & 0xff),
          (byte)((data >> 8) & 0xff),
          (byte)((data >> 0) & 0xff),
      };
  }
  
  public static byte[] toByta(float data) {
      return toByta(Float.floatToRawIntBits(data));
  }
  
  public static byte[] toByta(double data) {
      return toByta(Double.doubleToRawLongBits(data));
  }

  public static byte[] toByta(String data) {
    return (data == null) ? null : data.getBytes();
  }
  
  public static String toString(byte[] data) {
    return (data == null) ? null : new String(data);
  }
  

  public static int toInt(byte[] data) {
      if (data == null || data.length != 4) return 0x0;
      // ----------
      return (int)( // NOTE: type cast not necessary for int
              (0xff & data[0]) << 24  |
              (0xff & data[1]) << 16  |
              (0xff & data[2]) << 8   |
              (0xff & data[3]) << 0
              );
  }
  
  public static long toLong(byte[] data) {
      if (data == null || data.length != 8) return 0x0;
      // ----------
      return (long)(
              // (Below) convert to longs before shift because digits
              //         are lost with ints beyond the 32-bit limit
              (long)(0xff & data[0]) << 56  |
              (long)(0xff & data[1]) << 48  |
              (long)(0xff & data[2]) << 40  |
              (long)(0xff & data[3]) << 32  |
              (long)(0xff & data[4]) << 24  |
              (long)(0xff & data[5]) << 16  |
              (long)(0xff & data[6]) << 8   |
              (long)(0xff & data[7]) << 0
              );
  }
  
  public static float toFloat(byte[] data) {
      if (data == null || data.length != 4) return 0x0;
      // ---------- simple:
      return Float.intBitsToFloat(toInt(data));
  }
  
  public static double toDouble(byte[] data) {
      if (data == null || data.length != 8) return 0x0;
      // ---------- simple:
      return Double.longBitsToDouble(toLong(data));
  }
}
