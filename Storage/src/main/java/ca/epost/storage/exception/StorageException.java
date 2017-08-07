package ca.epost.storage.exception;

/**   This class handle all storage exceptions and return appropriate message to caller*/
public class StorageException extends Exception
{
	private static final long serialVersionUID=1L;
	
	public StorageException(String str)
	{
		super(str);
	}
	
	public StorageException()
	{
		//super(str);
	}
	public StorageException(Exception ex)
	{
		super(ex);
	}
	

}
