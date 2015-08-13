package in.ureport.util;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMarshaller;

/**
 * Created by johncordeiro on 13/08/15.
 */
public class EnumTypeConverter implements DynamoDBMarshaller<Enum> {

    @Override
    public String marshall(Enum getterReturnResult) {
        return getterReturnResult.name();
    }

    @Override
    public Enum unmarshall(Class<Enum> clazz, String obj) {
        return Enum.valueOf(clazz, obj);
    }
}
