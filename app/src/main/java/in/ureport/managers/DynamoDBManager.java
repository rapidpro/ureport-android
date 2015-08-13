package in.ureport.managers;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Created by johncordeiro on 13/08/15.
 */
public class DynamoDBManager {

    private static AmazonDynamoDBClient client;
    private static DynamoDBMapper mapper;

    public static void initialize(CognitoCachingCredentialsProvider credentialsProvider) {
        client = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(client);
    }

    public static DynamoDBMapper getMapper() {
        return mapper;
    }

}
