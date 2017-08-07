package ca.epost.storage.io;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import ca.epost.storage.exception.StorageException;
/**  epost Object Storage Interface methods to do I/O operations on Object Storage*/

public interface IStorageManager 
{
	/*
	 * getFileStream method
	 * returns the content Input stream of storage object.The caller should close the
	 * stream after use.
	 * @param key specifies the reference of storage object
	 * @return Input Stream of object contents
	 * @throws StorageException -"key doesnot exists" if key is not found.
	 * @since version 1.00
	 */
	
	public InputStream getFileStream(String key) throws StorageException;
	/* 
	 * getSignedUrl method
	 * 
	 * returns the signed url of the object
	 * @param key specifies the reference of the storage object
	 * @retun signed public url of the storage object requested.
	 * @throws StorageException-"key doesnot exists" if key  is not found
	 * @since version 1.00
	 * */
	public URL getSignedUrl(String key)throws StorageException;
	/*
	 *getCFSignedUrl method
	 *returns the signed url of file object from Cloud Front CDN based on the input key
	 *@param key specifies the references of the storage object
	 *@return signed public url of the storage object requested as string.
	 *@throws StorageException-:Key does not exists" if key is not found.
	 *@since version 1.00
	 * */
	 public String getCFSignedUrl(String key)throws StorageException;
	 
	  /*
	  * putFile method
	  * uploads a file object in storage
	  * @param key specifies the reference of the storage object to be uploaded.
	  * Note - if the specified key already exists, this method will over ride the 
	  * existing object.
	  * @param inputstream specifies the contents to be uploaded
	  * @return None
	  * @throws StorageException - "Key is empty" if blank key is passed.
	  * @throws StorageException- "InputStream is empty" if size of InputStream is zero.
	  * @since version 1.00
	  * 
	   */
	 
	 public void putFile(String key,InputStream inputStream)throws StorageException;
	 
	  /*
	   * putFile method
	   * uploads a file object in storage.
	   * @param key specifies the reference of the storage object to be uploaded
	   * @param inputStream specifies the contents to be uploaded
	   * @param tags specifies a collection of tags to be added with the object whch are key-value pairs
	   * @return None
	   * @throws StorageException-"Key is empty" if blank key is passed.
	   * @throws StorageException exception if size of tags is more than 10. Only  10 tags are allowed per file
	   * @thows StorageException- "InputStream is empty" if size of InputStream is zero.
	   * @since version 1.00
	   */
	   
	  
	  public void putFile(String key,InputStream inputStream,Map<String,String> tags) throws StorageException;
	  
	   /* 
	   *  deleteFile method
	   *  deletes an object from storage.
	   *  @param key specifies the reference of the storage object to be deleted
	   *  @param softDelete specifiess physical delete required or not- Y/N.
	   *  @return None
	   *  @throws StorageException-"Key is empty" if blank key is passed.
	   *  @throws StorageException-"Key doesnot exists" if key is not found.
	   *  @since version 1.00
	   *  
	   * 
	   * 
	   */
	  public void deleteFile(String key)throws StorageException;
	  /*
	  /** deleteFolder method
	   * deletes a folder and all objects inside it.
	   * @param path specifies the folder  to be deleted(e.g. "folder/folder2/")
	   * @return List of keys that are deleted inside a folder
	   * @throws StorageException-"Path is empty" if blank path is passed.
	   * @throws StorageException-"Path doesnot exists" if path is not found.
	   * @since version 1.00
	   */
	  public List<String> deleteFolde(String path);
	   
	   
	   

}
