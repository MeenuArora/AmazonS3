package ca.epost.storage.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import ca.epost.storage.exception.StorageException;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.PEM;


public class StorageManagerAwsS3Impl implements IStorageManager
{

	//private String awsAccessKey="your access key";
	//private String awsSecretKey=your secret key";
	//private AWSCredentials awsCredentials=null;
	private AmazonS3 s3client=null;
	private static StorageManagerAwsS3Impl instance=null;
	private static final String S3bucket="epost-eol-locked";
	/* Cloud Front specific parameters */
	private static final String PemFileBucket="poc-s3valut-ca";
	private static final Regions BucketRegion=Regions.CA_CENTRAL_1;
	private static final String PemFileKey="nonprod-shared-pk-APKAISRHIYAJJJWO2FWA.pem";
	String KEY="AKIAJ4G7B6GW23TNUDVA";
	String KEYVALUE="pObORTYir2Js/I72x9+hamHinFfmBPIPFo7BszSz";
	private static final int ExpirySeconds=3600;
	
	private String KeyPairId=null;
	private PrivateKey PrivateKey=null;
	
	private StorageManagerAwsS3Impl()
	{
		System.out.println("calling construtor");
		/* Getting the credentials from credentials  file in {user home dir}\.aws */
		s3client=AmazonS3ClientBuilder.standard().withRegion(BucketRegion).build();
		/* TODO - can get the access key an secret key from caller from properties and build credentials here */
		//awsCredentials=new BasicAWSCredentials(awsAccessKey,awsSecetKey);
		//s3client=AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		
		/* Load Private key for Cloud Front from remote location and cache locally */
		GetObjectRequest request=new GetObjectRequest(PemFileBucket,PemFileKey);
		S3Object object=s3client.getObject(request);
		S3ObjectInputStream is =object.getObjectContent();
		try
		{
			System.out.println("Creating PrivateKey...");
			PrivateKey=PEM.readPrivateKey(is);
			KeyPairId="APKAISRHIYAJJJWO2FWA";
			System.out.println("KeyPair ID"+KeyPairId + " points to PrivateKey containing"+ PrivateKey.getEncoded().length+"Bytes");
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}System.out.println("calling constructor s3 client=="+s3client);
	}
	public static StorageManagerAwsS3Impl  getInstance()
	{
		if(instance==null)
		{
			instance=new StorageManagerAwsS3Impl();
		}
		return instance;
	}
	public void deleteFile(String key)throws StorageException
	{
		/*TODO Check if key exists. if it does not exists
		 * - throw exception or do nothing ? check with Surkh */
		/* Delete the object for given key in s3 */
		s3client.deleteObject(S3bucket,key);
		 
	}
	public InputStream getFileStream(String key)throws StorageException
	{
		S3Object s3object=s3client.getObject(new GetObjectRequest(S3bucket,key));
		InputStream stream;
		stream=s3object.getObjectContent();
		return stream;
	}
	/* Old implementation
	 * public InputStream getFileStream(String key)
	 * {
	 * S3object s3object=s3client.getObject(new GetObjectRequest(S3bucket,key));
	 * InputStream testStream=s3object.getObjectContent();
	 * InputStream stream;
	 * BufferedInputStream bis;
	 * ByteArrayOutputStream bos=new ByteArrayOutputStream();
	 * byte buff[]=new byte[2048];
	 * int bytesRead;
	 * try
	 * {
	 * 	  displayTextInputStream(testStream);
	 * stream=s3object.getObjectContent();
	 * 
	 * bis=new BufferedInputStream(stream);
	 * while(-1 !=(bytesRead=bis.read(buff,0,buff.length)))
	 * {
	 * 		bos.write(buff,0,bytesRead);
	 * 		System.out.println(File STREAM : : "+buff.toString());
	 * }
	 * }catch(AmazonServiceException ase)
	 * { ase.printStackTrace();}
	 * finally
	 * {
	 * 	try
	 * {
	 * 		stream.close();
	 * 		bis.close();
	 *      bos.close();
	 * }catch(IOException e)
	 * {
	 * 		TODO Auto-generated catch block
	 * 		e.printStackTrace();
	 * }
	 * }
	 * return stream;
	 * }*/
	
	/* Method for testing file contents */
	
	private static void displaytextInputStream(InputStream input) throws IOException
	{
		//read one text line at a time and display.
		BufferedReader reader=new BufferedReader(new InputStreamReader(input));
		while(true)
		{
			String line=reader.readLine();
			if(line==null) break;
			System.out.println("  "+line);
		}
		System.out.println();
	}
	 
	public URL getSignedUrl(String key)
	{
		URL url=null;
		try
		{
			java.util.Date expiration=new java.util.Date();
			long milliSeconds=expiration.getTime();
			milliSeconds +=1000*60*60; //Add 1 hour.
			expiration.setTime(milliSeconds);
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(S3bucket,key);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);
			generatePresignedUrlRequest.setExpiration(expiration);
			url=s3client.generatePresignedUrl(generatePresignedUrlRequest);
		}catch(AmazonServiceException e)
		{
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}
	/* Buckets should be created from Console by authorized person
	 * public void createBucket(String bucketName)
	 * {
	 * 		try
	 * {
	 * 		System.out.println("bucket name=="+bucketName);
	 * System.out.println("exists =="+s3client.doesBucketExist(bucketName));
	 * }catch(AmazonServiceException e)
	 * {
	 * 	//TODO Auto-generated catch block
	 * e.printStackTrace();
	 * }catch(SDKClientException e)
	 * {
	 * 	//TODO Auto-generated catch block
	 * e.printStackTrace();
	 * }
	 * }
	 * 
	 */
	 public String  getCFSignedUrl(String key)
	 {
		 if(key== null ||key =="") 
		 {
			 /* TODO throw exception and set apprpriate return String */
		 }
		 if(KeyPairId ==null ||KeyPairId=="")
		 {
			 /* TODO throw exception and set apprpriate return String */
		 }
		 if(PrivateKey==null)
		 {
			 /* TODO throw exception and set apprpriate return String */
		 }
		 /* Form input url from key*/
		 String url="https://d2ku5pgplqx5f1.cloudfront.net/"+key;
		 System.out.println("url INSIDE "+url);
		 System.out.println("KeyPairId new =="+KeyPairId);
		 System.out.println("PrivateKey new =="+ PrivateKey.toString());
		 String signedURL =CloudFrontUrlSigner.getSignedURLWithCannedPolicy(url,KeyPairId,PrivateKey,getExpiryDate());
		 return signedURL;
				 
	 }
	 public List<String>deleteFolder(String path)
	 {
		 List<S3ObjectSummary> fileList=s3client.listObjects(S3bucket,path).getObjectSummaries();
		 List<String> keys=new ArrayList<String>();
		 /* Delete all the objects inside the folder */
		 for(S3ObjectSummary file:fileList)
		 {
			 s3client.deleteObject(S3bucket,file.getKey());
			 keys.add(file.getKey());
		 }
		 return keys;
	 }
	 public void putFile(String key,InputStream inputstream)throws StorageException
	 {
		 /* TODO - Check if InputStream is empty */
		 if(inputstream== null)
		 {
			 throw new StorageException("InputStream is empty");
		 }
		 /* TODO  - check if InputStream is empty*/
		 if(key==null || key=="")
		 {
			 throw new StorageException("key is blank");
		 }
		 s3client.putObject(S3bucket,key,inputstream,null);
	 }
	 public void putFile(String key,InputStream inputStream,Map<String,String> tags) throws StorageException
	 {
		 if(tags.size()>10)
		 {
			 /* Throw a business exception more than 10 tags are passed*/
			 throw new StorageException("Invalid Number of tags.Only 10 tags are allowed");
			 
		 }
		 ObjectMetadata omd=new ObjectMetadata();
		 omd.setUserMetadata(tags);
		 s3client.putObject(S3bucket,key,inputStream,omd);
	 }
	 private static Date getExpiryDate()
	 {
		 Calendar calender=Calendar.getInstance();
		 calender.add(Calendar.SECOND,ExpirySeconds);
		 return calender.getTime();
	 }
	public List<String> deleteFolde(String path) {
		// TODO Auto-generated method stub
		return null;
	}
}
