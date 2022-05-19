package edu.practice.utils.shared.com.example.rare_finds.sqlconnection


import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.blob.BlobContainerPermissions
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType
import edu.practice.utils.shared.Keys
import java.io.File
import java.io.FileInputStream

class BlobConnection() {
    private val storageConnectionString = Keys.blob()
    private val imageUrlLocation = "https://rarefindsstorage.blob.core.windows.net"

    @RequiresApi(Build.VERSION_CODES.P)
    fun blobConnection(
        imageUri: Uri,
        cont: ContentResolver,
        containerName: String,
        fileName: String
    ) {

        //Here starts the code for Azure Storage Blob
        try {
            // Retrieve storage account from connection-string
            val storageAccount = CloudStorageAccount.parse(storageConnectionString)

            // Create the blob client
            val blobClient = storageAccount.createCloudBlobClient()

            // Get a reference to a container
            // The container name must be lower case
            val container = blobClient.getContainerReference(containerName)

            // Create the container if it does not exist
            container.createIfNotExists()

            // Create a permissions object
            val containerPermissions = BlobContainerPermissions()

            // Include public access in the permissions object
            containerPermissions.publicAccess = BlobContainerPublicAccessType.CONTAINER

            // Set the permissions on the container
            container.uploadPermissions(containerPermissions)

            // Create or overwrite the "fileName.jpg" blob with contents from a local file
            val blob = container.getBlockBlobReference("$fileName.jpeg")
            val source = getRealPathFromURI(imageUri, cont)?.let { File(it) }
            if (source != null) {
                blob.upload(FileInputStream(source), source.length())
            }

        } catch (e: Exception) {
            println("------------------------------------------")
            println(e.message)
            println("------------------------------------------")
        }
    }

    private fun getRealPathFromURI(contentURI: Uri, cont: ContentResolver): String? {
        val result: String?
        val cursor: Cursor? = cont.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun returnImageUrl(container: String, imageName: String): String{
        return "$imageUrlLocation/$container/$imageName.jpeg"
    }

    fun getImage(imageName: String): Bitmap? {
        try {
            val image = java.net.URL(imageName).openStream()
            return BitmapFactory.decodeStream(image)
        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }

}