import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.stream.FileImageInputStream;

public class Image3 extends Image
{
    private BufferedImage img;
    private String fileName;			
    private int pixelDepth=3;			
    public ArrayList<RGB> LUT = new ArrayList<>();

    public Image3(int w, int h){ 
        super(w, h);
    }   

    public void drawEmpty(){
        int[] irgb = {255, 255, 255};
        for(int x = 0; x < getW(); x++){
            for(int y = 0; y < getH(); y++){
                setPixel(x, y, irgb);
            }
        }
    }

    public void drawCircle(int thickness, int radius){
        int[] black = {0, 0, 0};
        int count = 1;
        int cx = getW()/2;
        int cy = getH()/2;
        int r, rcos, rsin, x, y, xPrime, yPrime;
        int newRadius = radius;

        while( (newRadius+thickness) <= (getW()/2) ) {
            for(int layers = 0; layers < thickness; layers++){
                for(double theta = 0; theta <=90; theta += 0.01){
                    r =  newRadius + layers;
                    rcos = (int)(r * Math.cos(theta));
                    rsin = (int)(r * Math.sin(theta));
                    x = rcos + cx;
                    y = rsin + cy;
                    xPrime = cx - rcos;
                    yPrime = cy - rsin;
                    setPixel(x, y, black);
                    setPixel(xPrime, y, black);
                    setPixel(x, yPrime, black);
                    setPixel(xPrime, yPrime, black);
                }
            }
            count++;
            newRadius = radius * count;
        }
    }

}