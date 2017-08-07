package ca.epost.storage.io;

public class StorageManagerFactory 
{

	public static IStorageManager getFileManager()
	{
		IStorageManager storageManager=StorageManagerAwsS3Impl.getInstance();
		return storageManager;
		
	}
	
}
