package in.ureport.managers;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import in.ureport.R;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class AmazonServicesManager {

    private static TransferUtility transferUtility;
    private static AmazonS3Client s3Client;
    private static CognitoCachingCredentialsProvider credentialsProvider;

    public static String BUCKET_ID;

    public static void init(Context context) {
        transferUtility = new TransferUtility(getS3Client(context), context);
        BUCKET_ID = context.getString(R.string.amazon_s3_bucket_id);
    }

    public static TransferUtility getTransferUtility() {
        return transferUtility;
    }

    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (credentialsProvider == null) {
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    context.getString(R.string.identity_pool_id),
                    Regions.US_EAST_1);
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
