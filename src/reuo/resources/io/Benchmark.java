package reuo.resources.io;

import java.io.*;
import java.util.Random;

import reuo.resources.format.*;

public class Benchmark{
	public static void main(String[] args) throws IOException{
		File dir = new File(args[0]);
		
		GumpLoader loader = new GumpLoader();
		loader.prepare(
				new StoredIndexPreparation<Preparation.None>(
						new File(dir, "gumpidx.mul"),
						new File(dir, "gumpart.mul"),
						Rgb15To16Masked.getFormatter(),
						null
				)
		);
		
		int[] randoms = new int[loader.getCapacity()];
		int index = 0;
		
		for(int id : loader){
			randoms[index++] = id;
		}
		
		Random rnd = new Random(1337);
		
		for(int i=0; i < 1000; i++){
			int a = rnd.nextInt(randoms.length);
			int b = rnd.nextInt(randoms.length);
			int t = randoms[a];
			
			randoms[a] = randoms[b];
			randoms[b] = t;
		}
		
		long end;
		//int totalWidth = 0, totalHeight = 0;
		long start = System.currentTimeMillis();
		
		for(int id : randoms){
			//if(id > 0x4000) break;
			//BitMap bmp = null;
			
			try{
				loader.get(id);
			}catch(IOException e){
				e.printStackTrace();
				break;
			}
			
			//totalWidth += bmp.getWidth();
			//totalHeight += bmp.getHeight();
		}
		
		end = System.currentTimeMillis();
		System.out.printf("Total of %dms", end - start);
	}
}
