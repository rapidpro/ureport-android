package in.ureport.managers;

import android.content.Context;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import in.ureport.BuildConfig;
import in.ureport.R;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class AmazonServicesManager {

    private static TransferUtility transferUtility;
    private static AmazonS3Client s3Client;

    public static String BUCKET_ID;

    public static void init(Context context) {
        transferUtility = new TransferUtility(getS3Client(context), context);
        BUCKET_ID = context.getString(R.string.amazon_s3_bucket_id);
    }

    public static TransferUtility getTransferUtility() {
        return transferUtility;
    }

    private static AWSCredentials getCredentials(Context context) {
        return new BasicAWSCredentials(context.getString(R.string.amazon_s3_access_key)
                , context.getString(R.string.amazon_s3_access_secret));
    }

    private static AWSCredentialsProvider getCredentitalsProvider(Context context) {
        return new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),
                context.getString(R.string.identity_pool_id),
                Regions.US_EAST_1);
    }

    private static AmazonS3Client getS3Client(Context context) {
        if (s3Client == null) {
            if(BuildConfig.DEBUG) {
                s3Client = new AmazonS3Client(getCredentitalsProvider(context.getApplicationContext()));
            } else {
                s3Client = new AmazonS3Client(getCredentials(context.getApplicationContext()));
            }
        }
        return s3Client;
    }

}
