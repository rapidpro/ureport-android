package in.ureport.models.converters;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMarshaller;

import in.ureport.models.Story;

/**
 * Created by johncordeiro on 13/08/15.
 */
public class StoryTypeConverter implements DynamoDBMarshaller<Story> {

    @Override
    public String marshall(Story getterReturnResult) {
        return getterReturnResult.getStoryId();
    }

    @Override
    public Story unmarshall(Class<Story> clazz, String obj) {
        Story story = new Story();
        story.setStoryId(obj);

        return story;
    }
}
