import com.sun.org.apache.xerces.internal.impl.dv.xs.YearMonthDV;

public class DCTCompression{
    String fileName;
    int n;
    double PI = Math.PI;
    Image2 originalImage;
    Image2 image;
    Image2 returnImage;

    Image2 yImage;
    Image2 cbImage;
    Image2 crImage;

    Image2 yInverseImage;
    Image2 cbInverseImage;
    Image2 crInverseImage;

    double[] Y = {0.2990, 0.5870, 0.1140};
    double[] Cb = {-0.1687, -0.3313, 0.5000};
    double[] Cr = {0.5000, -0.4187, -0.0813};

    double[] R= {1.0000, 0, 1.4020};
    double[] G = {1.0000, -0.3441, -0.7141};
    double[] B = {1.0000, 1.7720, 0};

    double[][] YQ = {
        {4, 4, 4, 8, 8, 16, 16, 32},
        {4, 4, 4, 8, 8, 16, 16, 32},
        {4, 4, 8, 8, 16, 16, 32, 32},
        {8, 8, 8, 16, 16, 32, 32, 32},
        {8, 8, 16, 16, 32, 32, 32, 32},
        {16, 16, 16, 32, 32, 32, 32, 32},
        {16, 16, 32, 32, 32, 32, 32, 32},
        {32, 32, 32, 32, 32, 32, 32, 32}
    };

    double[][] CQ = {
        {8, 8, 8, 16, 32, 32, 32, 32},
        {8, 8, 8, 16, 32, 32, 32, 32},
        {8, 8, 16, 32, 32, 32, 32, 32},
        {16, 16, 32, 32, 32, 32, 32, 32},
        {32, 32, 32, 32, 32, 32, 32, 32},
        {32, 32, 32, 32, 32, 32, 32, 32},
        {32, 32, 32, 32, 32, 32, 32, 32},
        {32, 32, 32, 32, 32, 32, 32, 32}
    };
    
    double[][] yArray;
    double[][] cbArray;
    double[][] crArray;

    double[][] cbSubArray;
    double[][] crSubArray;

    double[][] DCTY;
    double[][] DCTCb;
    double[][] DCTCr;

    double[][] QuanY;
    double[][] QuanCb;
    double[][] QuanCr;

    double[][] DeQuanY;
    double[][] DeQuanCb;
    double[][] DeQuanCr;

    double[][] IDCTY;
    double[][] IDCTCb;
    double[][] IDCTCr;

    double[][] cbSupArray;
    double[][] crSupArray;



    public DCTCompression(String fileName, int n){
        this.fileName = fileName;
        this.n = n;
        resize(fileName);
        yArray = new double[image.getW()][image.getH()];
        cbArray = new double[image.getW()][image.getH()];
        crArray = new double[image.getW()][image.getH()];
        originalImage.write2PPM("Original" + originalImage.getW() + "x" + originalImage.getH()+".ppm");
    }
    
    public void resize(String fileName){
        originalImage = new Image2(fileName);
        int originalWidth = originalImage.getW();
        int originalHeight = originalImage.getH();
        // int extraW = (originalWidth <= 8)? 8 - originalWidth : 8-originalWidth%8;
        // int extraH = (originalHeight <= 8) ? 8 - originalHeight : 8-originalHeight%8;
        int extraW = addExtra(originalWidth);
        int extraH = addExtra(originalHeight);

        System.out.println("Padded X's: "+extraW);
        System.out.println("Padded Y's: "+extraH);

        int[] rgb = new int[3];

        image = new Image2(originalWidth + extraW, originalHeight + extraH);
        for(int x = 0; x < originalWidth; x++){
            for(int y = 0; y < originalHeight; y++){
                originalImage.getPixel(x, y, rgb);
                image.setPixel(x, y, rgb);
            }
        }

        image.write2PPM("Intermediate"+ image.getW() + "x" + image.getH()+".ppm");
    }

    public void colorSpace(){
        int[] rgb = new int[3];
        for(int y = 0; y < image.getH(); y++){
            for(int x = 0; x < image.getW(); x++){
                image.getPixel(x, y, rgb);
                
                //Matrix Multiplication
                yArray[x][y] = ( (Y[0]*rgb[0]) + (Y[1]*rgb[1]) + (Y[2]*rgb[2]) ); 
                cbArray[x][y] = ( (Cb[0]*rgb[0]) + (Cb[1]*rgb[1]) + (Cb[2]*rgb[2]) ); 
                crArray[x][y] = ( (Cr[0]*rgb[0]) + (Cr[1]*rgb[1]) + (Cr[2]*rgb[2]) ); 
                
                //Truncate if necessary
                if(yArray[x][y] < 0) yArray[x][y] = 0;
                if(yArray[x][y] > 255) yArray[x][y] = 255;
                if(cbArray[x][y] < -128) cbArray[x][y] = -127.5;
                if(cbArray[x][y] > 127) cbArray[x][y] = 127.5;
                if(crArray[x][y] < -128) crArray[x][y] = -127.5;
                if(crArray[x][y] > 127) crArray[x][y] = 127.5;

                //Subtract Necessary Values
                yArray[x][y] = Math.round(yArray[x][y] -128);
                cbArray[x][y] = Math.round(cbArray[x][y] -0.5);
                crArray[x][y] = Math.round(crArray[x][y] -0.5);
                // yArray[x][y] = yArray[x][y] -128;
                // cbArray[x][y] = cbArray[x][y] -0.5;
                // crArray[x][y] = crArray[x][y] -0.5;
            }
        }
        
        yImage = new Image2(image.getW(), image.getH());
        yImage.setArray(yArray);
        yImage.write2PPM("YImage" + yImage.getW() + "x" + yImage.getH()+".ppm");
    }

    // public void subsample(){     
    //     //Get subsampling size
    //     int subArrayRow = cbArray.length/2;
    //     int subArrayColumn = cbArray[0].length/2;
    //     double[][] interimCb = new double[subArrayRow][subArrayColumn];
    //     double[][] interimCr = new double[subArrayRow][subArrayColumn];

    //     //Subsample
    //     int i, j;
    //     for(int y = 0; y < subArrayColumn; y++){
    //         for(int x = 0; x < subArrayRow; x++){
    //             i = 2*x;
    //             j = 2*y;
    //             interimCb[x][y] = (cbArray[i][j] + cbArray[i+1][j] + cbArray[i+1][j+1] + cbArray[i][j+1])/ 4;
    //             interimCr[x][y] = (crArray[i][j] + crArray[i+1][j] + crArray[i+1][j+1] + crArray[i][j+1])/ 4;
    //         }
    //     }

    //     //Pad for 0's
    //     System.out.println("BeforeRow: " + subArrayRow);
    //     System.out.println("BeforeColumn: " + subArrayColumn);
    //     subArrayRow += subArrayRow%8;
    //     subArrayColumn += subArrayColumn%8;
    //     System.out.println("AfterRow: " + subArrayRow);
    //     System.out.println("AfterColumn: " + subArrayColumn);

    //     cbSubArray = new double[subArrayRow][subArrayColumn];
    //     crSubArray = new double[subArrayRow][subArrayColumn];
        
    //     for( i = 0; i < interimCb.length; i++){
    //         for( j = 0; j < interimCb[0].length; j++){
    //             cbSubArray[i][j] = interimCb[i][j];
    //             crSubArray[i][j] = interimCr[i][j];
    //         }
    //     }

    //     cbImage = new Image2(subArrayRow, subArrayColumn);
    //     cbImage.setArray(cbSubArray);
    //     cbImage.write2PPM("CbImage" + cbImage.getW() + "x" + cbImage.getH()+".ppm");

    //     crImage = new Image2(subArrayRow, subArrayColumn);
    //     crImage.setArray(crSubArray);
    //     crImage.write2PPM("CrImage" + crImage.getW() + "x" + crImage.getH()+".ppm");

    // }

    public int addExtra(int x){
        if(x%8 != 0) return 8 - (x%8);
        else return 0;
    }
    public void subsample(){     
        //Get subsampling size
        int subArrayRow = cbArray.length/2;
        int subArrayColumn = cbArray[0].length/2;
        int extraX = addExtra(subArrayRow);
        int extraY = addExtra(subArrayColumn);

        System.out.println("Padded X's: "+extraX);
        System.out.println("Padded Y's: "+extraY);

        cbSubArray = new double[subArrayRow + extraX][subArrayColumn + extraY];
        crSubArray = new double[subArrayRow + extraX][subArrayColumn + extraY];

        //Subsample
        int i, j;
        for(int y = 0; y < subArrayColumn; y++){
            for(int x = 0; x < subArrayRow; x++){
                i = 2*x;
                j = 2*y;
                cbSubArray[x][y] = (cbArray[i][j] + cbArray[i+1][j] + cbArray[i+1][j+1] + cbArray[i][j+1]) / 4;
                crSubArray[x][y] = (crArray[i][j] + crArray[i+1][j] + crArray[i+1][j+1] + crArray[i][j+1]) / 4;
            }
        }

        cbImage = new Image2(subArrayRow + addExtra(subArrayRow), subArrayColumn + addExtra(subArrayColumn));
        cbImage.setArray(cbSubArray);
        cbImage.write2PPM("CbImage" + cbImage.getW() + "x" + cbImage.getH()+".ppm");

        crImage = new Image2(subArrayRow + addExtra(subArrayRow), subArrayColumn + addExtra(subArrayColumn));
        crImage.setArray(crSubArray);
        crImage.write2PPM("CrImage" + crImage.getW() + "x" + crImage.getH()+".ppm");

    }
        
    public void DCTAll(){
        DCTY = new double[yArray.length][yArray[0].length];
        DCTCb = new double[cbSubArray.length][cbSubArray[0].length];
        DCTCr = new double[crSubArray.length][crSubArray[0].length];

        Image2 DCTImage = new Image2(image.getW(), image.getH());
        DCT(yArray, DCTY);
        DCTImage.setArray(DCTY);
        DCTImage.write2PPM("YDCTImage" + DCTImage.getW() + "x" + DCTImage.getH()+".ppm");

        DCTImage = new Image2(cbSubArray.length, cbSubArray[0].length);

        DCT(cbSubArray, DCTCb);
        DCTImage.setArray(DCTCb);
        DCTImage.write2PPM("CbDCTImage" + DCTImage.getW() + "x" + DCTImage.getH()+".ppm");

        DCT(crSubArray, DCTCr);
        DCTImage.setArray(DCTCr);
        DCTImage.write2PPM("CrDCTImage" + DCTImage.getW() + "x" + DCTImage.getH()+".ppm");
    }

    public static void DCT(double[][] original, double[][] DCTarray){
        for(int y = 0; y < original.length; y+=8){
            for(int x = 0; x < original[y].length; x+=8){
                DCTBlock(original, DCTarray, x, y);
            }
        }
    }
    
    public static void DCTBlock(double[][] original, double[][] DCTarray, int startx, int starty){
        double PI = Math.PI;
        double newA, originalFactor, firstFactor, secondFactor;
        
        for(int u = startx; u < startx + 8; u++){
            for(int v = starty; v < starty + 8; v++){
                originalFactor = (C(u%8) * C(v%8))/4;
                double DCTvalue = 0.0;
                for(int i= startx; i < startx + 8; i++){
                    for(int j = starty; j < starty + 8; j++){
                        newA = original[j][i] - 128;
                        firstFactor = Math.cos( ((2* (i%8)+1)*(u%8)*PI)/16);
                        secondFactor = Math.cos( ((2*(j%8)+1)*(v%8)*PI)/16);
                        DCTvalue += firstFactor * secondFactor * newA;
                    }
                }
                DCTvalue *= originalFactor;
                // DCTarray[v][u] = Math.round(DCTvalue * 10.0) / 10.0;
                DCTarray[v][u] = DCTvalue;
            }
        }
    }

    public void QuantizeAll(){
        QuanY = new double[yArray.length][yArray[0].length];
        QuanCb = new double[cbSubArray.length][cbSubArray[0].length];
        QuanCr = new double[crSubArray.length][crSubArray[0].length];

        Image2 QuantizedImage = new Image2(image.getW(), image.getH());
        quantize(DCTY, QuanY, YQ);
        QuantizedImage.setArray(QuanY);
        QuantizedImage.write2PPM("YQuantizedImage" + QuantizedImage.getW() + "x" + QuantizedImage.getH()+".ppm");

        QuantizedImage = new Image2(cbSubArray.length, cbSubArray[0].length);
        quantize(DCTCb, QuanCb, CQ);
        QuantizedImage.setArray(QuanCb);
        QuantizedImage.write2PPM("CbQuantizedImage" + QuantizedImage.getW() + "x" + QuantizedImage.getH()+".ppm");

        quantize(DCTCr, QuanCr, CQ);
        QuantizedImage.setArray(QuanCr);
        QuantizedImage.write2PPM("CrQuantizedImage" + QuantizedImage.getW() + "x" + QuantizedImage.getH()+".ppm");
    }
    public void quantize(double[][] original, double[][] quantizeArray, double[][] values){
        for(int y = 0; y < original.length; y+=8){
            for(int x = 0; x < original[0].length; x+=8){
                quantizeBlock(original, quantizeArray, values, x, y);
            }
        }

    }
    public void quantizeBlock(double[][] original, double[][] quantizeArray, double[][] values, int startx, int starty){
        for(int u = startx; u < startx + 8; u++){
            for(int v = starty; v < starty + 8; v++){
                double quantizeValue = values[u%8][v%8] * Math.pow(2, n);
                quantizeArray[v][u] = Math.round(original[v][u] / quantizeValue);
            }
        }
    }

    public void DeQuantizeAll(){
        DeQuanY = new double[yArray.length][yArray[0].length];
        DeQuanCb = new double[cbSubArray.length][cbSubArray[0].length];
        DeQuanCr = new double[crSubArray.length][crSubArray[0].length];

        Image2 DeQuantizedImage = new Image2(image.getW(), image.getH());
        deQuantize(QuanY, DeQuanY, YQ);
        DeQuantizedImage.setArray(DeQuanY);
        DeQuantizedImage.write2PPM("YDeQuantizedImage" + DeQuantizedImage.getW() + "x" + DeQuantizedImage.getH()+".ppm");

        DeQuantizedImage = new Image2(cbSubArray.length, cbSubArray[0].length);
        deQuantize(QuanCb, DeQuanCb, CQ);
        DeQuantizedImage.setArray(DeQuanCb);
        DeQuantizedImage.write2PPM("CbDeQuantizedImage" + DeQuantizedImage.getW() + "x" + DeQuantizedImage.getH()+".ppm");

        deQuantize(QuanCr, DeQuanCr, CQ);
        DeQuantizedImage.setArray(DeQuanCr);
        DeQuantizedImage.write2PPM("CrDeQuantizedImage" + DeQuantizedImage.getW() + "x" + DeQuantizedImage.getH()+".ppm");
    }

    public void deQuantize(double[][] original, double[][] deQuantizeArray, double[][] values){
        for(int y = 0; y < original.length; y+=8){
            for(int x = 0; x < original[0].length; x+=8){
                deQuantizeBlock(original, deQuantizeArray, values, x, y);
            }
        }

    }
    public void deQuantizeBlock(double[][] original, double[][] deQuantizeArray, double[][] values, int startx, int starty){
        for(int u = startx; u < startx + 8; u++){
            for(int v = starty; v < starty + 8; v++){
                // double deQuantizeValue = (values[u%8][v%8] * Math.pow(2, n));
                deQuantizeArray[v][u] = Math.round(original[v][u] * (values[u%8][v%8] * Math.pow(2, n)));
            }
        }
    }

    public void IDCTAll(){
        IDCTY = new double[yArray.length][yArray[0].length];
        IDCTCb = new double[cbSubArray.length][cbSubArray[0].length];
        IDCTCr = new double[crSubArray.length][crSubArray[0].length];

        Image2 IDCTImage = new Image2(image.getW(), image.getH());
        IDCT(DeQuanY, IDCTY);
        IDCTImage.setArray(IDCTY);
        IDCTImage.write2PPM("YIDCTImage" + IDCTImage.getW() + "x" + IDCTImage.getH()+".ppm");

        IDCTImage = new Image2(cbSubArray.length, cbSubArray[0].length);

        IDCT(DeQuanCb, IDCTCb);
        IDCTImage.setArray(IDCTCb);
        IDCTImage.write2PPM("CbIDCTImage" + IDCTImage.getW() + "x" + IDCTImage.getH()+".ppm");

        IDCT(DeQuanCr, IDCTCr);
        IDCTImage.setArray(IDCTCr);
        IDCTImage.write2PPM("CrIDCTImage" + IDCTImage.getW() + "x" + IDCTImage.getH()+".ppm");
    }

    public static void IDCT(double[][] DCTarray, double[][] IDCTarray){
        for(int y = 0; y < DCTarray.length; y+=8){
            for(int x = 0; x < DCTarray[y].length; x+=8){
                IDCTBlock(DCTarray, IDCTarray, x, y);
            }
        }
    }

    public static void IDCTBlock(double[][] DCTarray, double[][] IDCTarray, int startx, int starty){
        double PI = Math.PI;
        double firstFactor, secondFactor, thirdFactor;
        for(int i = startx; i < startx+8; i++){
            for(int j = starty; j < starty+8; j++){
                double IDCTvalue = 0.0;
                for(int u = startx; u < startx+8; u++){
                    for(int v = starty; v < starty+8; v++){
                        firstFactor = (C(u%8) * C(v%8))/4;
                        secondFactor = Math.cos( ((2*(i%8)+1)*(u%8)*PI)/16);
                        thirdFactor = Math.cos( ((2*(j%8)+1)*(v%8)*PI)/16);
                        IDCTvalue += firstFactor * secondFactor * thirdFactor * (DCTarray[v][u]);
                    }
                }
                IDCTvalue += 128;
                // IDCTarray[j][i] = Math.round(IDCTvalue);
                IDCTarray[j][i] = IDCTvalue;

            }
        }
    }

    public void supersample(){
        //Get supersampling size
        int supArrayRow = cbSubArray.length * 2;
        int supArrayColumn = cbSubArray[0].length * 2;
        double[][] interimCb = new double[supArrayRow][supArrayColumn];
        double[][] interimCr = new double[supArrayRow][supArrayColumn];

        //Supersample
        int i, j;
        for(int y = 0; y < supArrayColumn; y++){
            for(int x = 0; x < supArrayRow; x++){
                i = x/2;
                j = y/2;
                interimCb[x][y] = IDCTCb[i][j];
                interimCr[x][y] = IDCTCr[i][j];
            }
        }

        cbSupArray = new double[yImage.getW()][yImage.getH()];
        crSupArray = new double[yImage.getW()][yImage.getH()];
        
        for( int y = 0; y < cbSupArray[0].length; y++){
            for( int x = 0; x < cbSupArray.length; x++){
                cbSupArray[x][y] = interimCb[x][y];
                crSupArray[x][y] = interimCr[x][y];
            }
        }

        cbInverseImage = new Image2(yImage.getW(), yImage.getH());
        cbInverseImage.setArray(cbSupArray);
        cbInverseImage.write2PPM("CbInverseImage" + cbInverseImage.getW() + "x" + cbInverseImage.getH()+".ppm");

        crInverseImage = new Image2(yImage.getW(), yImage.getH());
        crInverseImage.setArray(crSupArray);
        crInverseImage.write2PPM("CrInverseImage" + cbInverseImage.getW() + "x" + cbInverseImage.getH()+".ppm");

    }
    
    public void inverseColorSpace(){
        int[] rgb = new int[3];
        returnImage = new Image2(yImage.getW(), yImage.getH());
        for(int y = 0; y < yImage.getH(); y++){
            for(int x = 0; x < yImage.getW(); x++){
                IDCTY[x][y] += 128;
                cbSupArray[x][y] += 0.5;
                crSupArray[x][y] += 0.5;

                rgb[0] = (int)Math.round( (R[0]*IDCTY[x][y]) + (R[1] * cbSupArray[x][y]) + (R[2] * crSupArray[x][y]) );
                rgb[1] = (int)Math.round( (G[0]*IDCTY[x][y]) + (G[1] * cbSupArray[x][y]) + (G[2] * crSupArray[x][y]) );
                rgb[2] = (int)Math.round( (B[0]*IDCTY[x][y]) + (B[1] * cbSupArray[x][y]) + (B[2] * crSupArray[x][y]) );

                if(rgb[0] < 0) rgb[0] = 0;
                if(rgb[0] > 255) rgb[0] = 255;
                if(rgb[1] < 0) rgb[1] = 0;
                if(rgb[1] > 255) rgb[1] = 255;
                if(rgb[2] < 0) rgb[2] = 0;
                if(rgb[2] > 255) rgb[2] = 255;

                returnImage.setPixel(x, y, rgb);

            }
        }

        returnImage.write2PPM("InverseColorSpace" + returnImage.getW() + "x" + returnImage.getH()+".ppm");
    }
    
    public void restoreSize(){
        //deletes the additional row and colums
        int[] rgb = new int[3];
        Image2 finalImage = new Image2(originalImage.getW(), originalImage.getH());
        for(int x = 0; x < originalImage.getW(); x++){
            for(int y = 0; y < originalImage.getH(); y++){
                returnImage.getPixel(x, y, rgb);
                finalImage.setPixel(x, y, rgb);
            }
        }

        finalImage.write2PPM("FinalImage_n_" + n + "_" +finalImage.getW() + "x" + finalImage.getH()+".ppm");

    }

    public static  double C(double x){
        if(x == 0) return 1 / Math.sqrt(2);
        else return 1;
    }


    public void driver(){
        colorSpace(); 
        subsample(); 
        DCTAll();
        QuantizeAll();

        DeQuantizeAll();
        IDCTAll();
        supersample(); 
        inverseColorSpace();
        restoreSize(); 
    }
}