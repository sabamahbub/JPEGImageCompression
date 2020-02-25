import java.util.Scanner;

public class CS4551_Mahbub{
  public static void main(String[] args) throws Exception {
    Scanner input = new Scanner(System.in);
    System.out.println("--Welcome to Multimedia Software System--");
    if(args[0] == null) System.exit(0);
    System.out.println("Enter 7 if you want all quantization 0 <= n <= 5");
    System.out.println("Else enter n such that 0 <= n <= 5");
    System.out.print("Enter n: ");
    int n = input.nextInt();

    if(n == 7){
      for(int i = 0; i < 6; i++){
        System.out.println("n = " + i);
        DCTCompression compression = new DCTCompression(args[0], i);
        compression.driver();
      }
    }
    else if(n > 0 && n <6){
          DCTCompression compression = new DCTCompression(args[0], n);
    compression.driver();
    }
    else{
      System.exit(0);
    }




    System.out.println("--Good Bye--");
    System.exit(0);
  }
  public static void usage(){
    System.out.println("\nUsage: java CS4551_Main\n");
  }    
}
