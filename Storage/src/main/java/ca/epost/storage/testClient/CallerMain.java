package ca.epost.storage.testClient;

import java.net.URL;

import ca.epost.storage.io.IStorageManager;
import ca.epost.storage.io.StorageManagerFactory;
public class CallerMain 
{
	public static void main(String[] args)
	{
		//TODO Auto-generated method stub
		try
		{
			IStorageManager f=StorageManagerFactory.getFileManager();
			String key="LoremIpsum.pdf";
			//f.createBucket(bucket1");
			//String file="E:\\Projects\\new.txt";
			//f.putFile(file);
			//URL url=f.getSignedUrl("projects/StickyNotes.txt");
			//System.out.println("url new =="+url.toString());
			//f.getFileStream("projects/StickyNotes.txt");
			String url=f.getCFSignedUrl(key);
			System.out.println("url new =="+url);
			f.deleteFile("projects/StickyNotes.txt");
			
		}catch(Exception e)
		{
			//TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

}
