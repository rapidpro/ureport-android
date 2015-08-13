package in.ureport.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;
import java.util.Random;

import in.ureport.R;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class SocialNetworkLoginTask extends AsyncTask<User.Type, Void, User> {

    private Context context;
    private ProgressDialog progressDialog;

    public SocialNetworkLoginTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, null, context.getString(R.string.load_message_wait), true);
    }

    @Override
    protected User doInBackground(User.Type... types) {
        if(types.length == 0) return null;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        User user = new User();
        user.setType(types[0]);
        createRandomUser(user);

        return user;
    }

    @Override
    protected void onPostExecute(User user) {
        progressDialog.dismiss();
        super.onPostExecute(user);
    }

    private void createRandomUser(User user) {
        String typeName = user.getType().toString().toLowerCase();
        user.setNickname("user_" + typeName);
        user.setEmail("user@" + typeName + ".com.br");
        user.setGender(User.Gender.values()[getRandomInt(0, 1)]);
        user.setBirthday(new Date());
    }

    private int getRandomInt(int min, int max){
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

}
