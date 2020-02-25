public class JpegCompression{
    
    final public double PI = Math.PI;

    private double[][] A = {
        {139,144,149,153,155,155,155,155},
        {144,151,153,156,159,156,156,156},
        {150,155,160,163,158,156,156,156},
        {159,161,162,160,160,159,159,159},
        {159,160,161,162,162,155,155,155},
        {161,161,161,161,160,157,157,157},
        {162,162,161,163,162,157,157,157},
        {162,162,161,161,163,158,158,158}
        };
    private double[][] B = new double[8][8];
    private double[][] C = new double[8][8];


    public JpegCompression(){
        DCT(this.A, this.B);
        IDCT(this.B, this.C);
    }

    public void printArray(double[][] array){
        for(int i = 0; i < array.length; i++){
            for( int j = 0; j < array[i].length; j++){
                System.out.printf(array[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public void print(){
        System.out.println("--------- Array A -------------");
        printArray(this.A);
        System.out.println("--------- Array B -------------");
        printArray(this.B);
        System.out.println("--------- Array C -------------");
        printArray(this.C);
    }

    public void DCT(double[][] original, double[][] DCTarray){
        double newA, originalFactor, firstFactor, secondFactor;
        for(int u = 0; u < DCTarray.length; u++){
            for(int v = 0; v < DCTarray[u].length; v++){
                originalFactor = (C(u) * C(v))/4;
                double DCTvalue = 0.0;
                for(int i= 0; i < original.length; i++){
                    for(int j = 0; j < original[i].length; j++){
                        newA = original[i][j] - 128;
                        firstFactor = Math.cos( ((2*i+1)*u*PI)/16);
                        secondFactor = Math.cos( ((2*j+1)*v*PI)/16);
                        DCTvalue += firstFactor * secondFactor * newA;
                    }
                }
                DCTvalue *= originalFactor;
                DCTarray[u][v] = Math.round(DCTvalue * 10.0) / 10.0;
            }
        }
    }

    public void IDCT(double[][] DCTarray, double[][] IDCTarray){
        double firstFactor, secondFactor, thirdFactor;
        for(int i = 0; i < DCTarray.length; i++){
            // double IDCTvalue = 0.0;
            for(int j = 0; j < DCTarray[i].length; j++){
                double IDCTvalue = 0.0;
                for(int u = 0; u < IDCTarray.length; u++){
                    for(int v = 0; v < IDCTarray[u].length; v++){
                        firstFactor = (C(u) * C(v))/4;
                        secondFactor = Math.cos( ((2*i+1)*u*PI)/16);
                        thirdFactor = Math.cos( ((2*j+1)*v*PI)/16);
                        IDCTvalue += firstFactor * secondFactor * thirdFactor * (DCTarray[u][v]);
                    }
                }
                IDCTvalue += 128;
                // IDCTarray[i][j] = Math.round(IDCTvalue * 10.0) / 10.0;
                IDCTarray[i][j] = Math.round(IDCTvalue);

            }
        }
    }
    public double C(double x){
        if(x == 0) return Math.sqrt(2) / 2;
        else return 1;
    }
}