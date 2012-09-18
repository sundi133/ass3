import java.io.*;
public class Tree{
  public static void main(String[] args) throws Exception{
  File file = null;
  FileReader freader = null;
  LineNumberReader lnreader = null;
  try{
  file = new File("C:\\Users\\sundi133\\Downloads\\fall2011\\ML\\ass3\\optdigits_tra_trans.dat");
  freader = new FileReader(file);
  lnreader = new LineNumberReader(freader);
  String line = "";
  while ((line = lnreader.readLine()) != null){
  System.out.println("Line:  " + lnreader.
getLineNumber() + ": " + line);
  }
  }
  finally{
  freader.close();
  lnreader.close();
  }
  }
}