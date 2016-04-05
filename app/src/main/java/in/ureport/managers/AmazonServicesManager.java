package in.ureport.managers;

import android.content.Context;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import in.ureport.R;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class AmazonServicesManager {

    private static TransferUtility transferUtility;
    private static AmazonS3Client s3Client;
    private static BasicAWSCredentials credentialsProvider;

    public static String BUCKET_ID;

    public static void init(Context context) {
        transferUtility = new TransferUtility(getS3Client(context), context);
        BUCKET_ID = context.getString(R.string.amazon_s3_bucket_id);
    }

    public static TransferUtility getTransferUtility() {
        return transferUtility;
    }

    private static BasicAWSCredentials getCredProvider(Context context) {
        if (credentialsProvider == null) {
            credentialsProvider = new BasicAWSCredentials(context.getString(R.string.amazon_s3_access_key)
                    , context.getString(R.string.amazon_s3_access_secret));
        }
        return credentialsProvider;
    }

    private static AmazonS3Client getS3Client(Context context) {
        if (s3Client == null) {
            s3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
        }
        return s3Client;
    }

}
