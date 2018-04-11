
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import com.sun.prism.Image;
 
public class myKmeans
 {
    public static void main(String [] args)
	{
		if (args.length < 3)
		{
			System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
			return;
		} 
		
		try
		{
			BufferedImage originalImage = ImageIO.read(new File(args[0]));
			int k=Integer.parseInt(args[1]);
			BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
			
			ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}	
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k)
	{
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		
		BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w,h , null);
		System.out.println("Reading the RGB values of image");
		
		int[] rgb = new int[w*h];
		int red[] = new int[w*h];
		int green[] = new int[w*h];
		int blue[] = new int[w*h];
		int count = 0;
		int min = 0;
		int max = 0;
		
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				rgb[count++]=kmeansImage.getRGB(i,j);
				if(count==1)
				{
					min = kmeansImage.getRGB(i,j);
					max = kmeansImage.getRGB(i,j);
				}
				else
				{
					if(min > kmeansImage.getRGB(i,j))
					{
						min = kmeansImage.getRGB(i,j);
					}
					if(max < kmeansImage.getRGB(i,j))
					{
						max = kmeansImage.getRGB(i,j);
					}
				}				
			}
		}

		System.out.println("Running KMeans Algorithm");
		kmeans(rgb,k,min,max);

		// Write the new rgb values to the image
		count = 0;
		System.out.println("\nWriting back the new values to image");
		
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				kmeansImage.setRGB(i,j,rgb[count++]);
			}
		}
		return kmeansImage;
    }
       
    private static void kmeans(int[] rgb, int k,int min,int max)
	{
    	
    	int red[] = new int[rgb.length];
    	int green[] = new int[rgb.length];
    	int blue[] = new int[rgb.length];
    	int karray[][] = new int[k][3];
    	int random;
    	Random randomGenerator = new Random();
      
		for(int i = 0; i < k; i++)
		{
		   random = randomGenerator.nextInt((max - min) + 1) + min;
		  
		   karray[i][0] = (random >> 16) & 0x000000FF;
		   karray[i][1] = (random >> 8 ) & 0x000000FF;;
		   karray[i][2] = (random) & 0x000000FF;;
		   
		}
	   
		ArrayList<Integer>[] kValues = new ArrayList[k];
	   
		for(int i = 0; i < k; i++)
		{
			kValues[i] = new ArrayList<Integer>();
		}
	   
		for(int i = 0; i <= 25; i++)
		{
			if(i != 0)
			{  
				for(int z = 0; z < k; z++)
				{
					kValues[z].clear();
				}
			}
			assignRGBValues(red,green,blue,rgb,karray,kValues,k,min,max);
			calculateNewMean(red,green,blue,rgb,karray,kValues,k);
		}
       
		assignOneColor(red,green,blue,rgb,karray,kValues,k,min,max);
    }
    
    public static void assignRGBValues(int red[],int green[],int blue[],int rgb[],int karray[][],ArrayList<Integer> kValues[],int k,int min,int max)
    {    	
    	  //calculate to which cluster the value belongs to and label them to that respective cluster
    	for(int j = 0; j < rgb.length; j++)
      	{
            double minimum=0;
            int cluster=0;
              
      		red[j] = (rgb[j] >> 16) & 0x000000FF;
      		green[j] = (rgb[j] >>8 ) & 0x000000FF;
      		blue[j] = (rgb[j]) & 0x000000FF;
			
      		for(int i = 0; i < k; i++)
			{
				double dist = Math.pow((karray[i][0]-red[j]),2)+Math.pow((karray[i][1]-green[j]),2)+Math.pow((karray[i][2]-blue[j]),2);
				if(i == 0)
				{
					minimum = dist;
					cluster = i;
				}
				else
				{
					if(dist <= minimum)
					{
						minimum = dist;
						cluster = i;
					}
				}
      		}
      		
      		kValues[cluster].add(j); 
      	}
    }
      
      
    public static void calculateNewMean(int red[],int green[],int blue[],int rgb[],int karray[][],ArrayList<Integer> kValues[],int k)
    {
    	int index;
      	int redValue = 0;
      	int greenValue = 0;
      	int blueValue = 0;
      	int total;
      	for(int z = 0; z < k; z++)
		{
	      	for(int i = 0; i < kValues[z].size(); i++)
	      	{
				index = kValues[z].get(i);
				redValue = redValue + red[index];
				greenValue = greenValue + green[index];
				blueValue = blueValue + blue[index];
	      	}
			
	      	if(kValues[z].size() == 0)
	      	{
	            total = 1;
	      	}
	      	else
	      	{
	      		total = kValues[z].size();
	      	}
	      	//calculate new mean ---sum of the cluster to the total points in the cluster
	      	karray[z][0] = redValue/total;
	      	karray[z][1] = greenValue/total;
	      	karray[z][2] = blueValue/total;
	      	redValue = 0;
	      	greenValue = 0;
	      	blueValue = 0;	      	
	    }
      	
    }
	
    public static void assignOneColor(int red[],int green[],int blue[],int rgb[],int karray[][],ArrayList<Integer> kValues[],int k,int min,int max)
    {
    	int value;
    	int index;
    	int sum = 0;
    	
    	for(int z = 0; z < k; z++)
		{
    		HashMap<Integer, Integer> hm = new HashMap();
			
			if(kValues[z].size() == 0)
			{
				value = 0;
			}
			
			else
			{   
				sum = 0;
				
				for(int i = 0; i < kValues[z].size(); i++)
				{
					index = kValues[z].get(i);
					try
					{
						hm.put(rgb[index], hm.get(rgb[index])+1);
					}
					catch(Exception e)
					{
						hm.put(rgb[index], 1);
					}
					sum = rgb[index]+sum;					
				}
				
				int maxValue = 0;
				int maxIndex = 0;
				Iterator it = hm.entrySet().iterator();
				Map.Entry pair = (Map.Entry)it.next();
				maxValue = (int)pair.getValue();
				maxIndex = (int)pair.getKey();
				while(it.hasNext())
				{
					Map.Entry pair1 = (Map.Entry)it.next();
					if((int)pair1.getValue() > maxValue)
					{
						maxValue = (int)pair1.getValue();
						maxIndex = (int)pair1.getKey();
					}					
				}
				
				value = maxIndex;
			}
			
			for(int i = 0; i < kValues[z].size(); i++)
			{
					 index = kValues[z].get(i);
					 rgb[index] = value;
			}
    	}    	
    }
}