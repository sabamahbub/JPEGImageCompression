import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.stream.FileImageInputStream;

public class Image2 extends Image
{
    private BufferedImage img;
    private String fileName;			// Input file name
    private int pixelDepth=3;			// pixel depth in byte
    public ArrayList<RGB> LUT = new ArrayList<>();

    //Constructors
    public Image2(int w, int h){ super(w, h);}
    public Image2(String fn){ super(fn);}

    public void setArray(double[][] array){
        int[] irgb = new int[3];
        for (int i = 0; i < getH(); i++){
            for(int j = 0; j < getW(); j++){
                for(int f = 0; f < 3; f++){irgb[f] = (int)array[j][i];}
                setPixel(j, i, irgb);
            }
          }
    }
}