package com.yd.yourdoctorpartnerandroid.managers;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.LinkedList;

public class AzureImageManager {
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
            + "AccountName=yourdoctorstorage;"
            + "AccountKey=eVFKYyqmfIlLApZOGqndbJdBqiR3LTSwymBdCRA5Yg8yTBsP5fG1oUBGXg5N1KgczBQI9LfV4An8o3Y7+kMyqA==";

    private static CloudBlobContainer getContainer() throws Exception {
        // Retrieve storage account from connection-string.

        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(storageConnectionString);

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        // Get a reference to a container.
        // The container name must be lower case
        CloudBlobContainer container = blobClient.getContainerReference("images");

        return container;
    }

    public static String UploadImage(String imageName, InputStream image, int imageLength) throws Exception {
        CloudBlobContainer container = getContainer();

        container.createIfNotExists();

        CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
        imageBlob.upload(image, imageLength);

        return imageName;

    }

    public static String[] ListImages() throws Exception{
        CloudBlobContainer container = getContainer();

        Iterable<ListBlobItem> blobs = container.listBlobs();

        LinkedList<String> blobNames = new LinkedList<>();
        for(ListBlobItem blob: blobs) {
            blobNames.add(((CloudBlockBlob) blob).getName());
        }

        return blobNames.toArray(new String[blobNames.size()]);
    }

    public static void GetImage(String name, OutputStream imageStream, long imageLength) throws Exception {
        CloudBlobContainer container = getContainer();

        CloudBlockBlob blob = container.getBlockBlobReference(name);

        if(blob.exists()){
            blob.downloadAttributes();

            imageLength = blob.getProperties().getLength();

            blob.download(imageStream);
        }
    }

    static final String validChars = "abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( validChars.charAt( rnd.nextInt(validChars.length()) ) );
        return sb.toString();
    }
}
