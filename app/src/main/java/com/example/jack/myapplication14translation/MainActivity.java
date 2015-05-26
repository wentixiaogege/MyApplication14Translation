package com.example.jack.myapplication14translation;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    EditText translateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void OnTranslateClick(View view) {

        EditText translateEditText = (EditText) findViewById(R.id.editText);

        if (!isEmpty(translateEditText)){

            Toast.makeText(this,"Getting Transaltions",Toast.LENGTH_SHORT).show();

            new SaveTheFeed().execute();
        }else{

            Toast.makeText(this,"Enter words to Translate",Toast.LENGTH_SHORT).show();


        }

    }
    protected boolean isEmpty(EditText editText){

        return editText.getText().toString().trim().length() == 0;
    }

    class SaveTheFeed extends AsyncTask<Void,Void,Void>{

        String jsonString = "";

        String result = "";

        @Override
        protected Void doInBackground(Void... params) {

            EditText translateEditText = (EditText) findViewById(R.id.editText);

            String wordsToTranslate = translateEditText.getText().toString();

            wordsToTranslate = wordsToTranslate.replace(" ","+");

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());

//            HttpPost httpPost = new HttpPost("http://localhost:8079/phpbyjack/WebServices/translateme.php?action=translations&english_words=" + wordsToTranslate);
            HttpGet httpPost = new HttpGet("http://newjustin.com/translateit.php?action=translations&english_words=" + wordsToTranslate);

            httpPost.setHeader("Content-type","application/json");

            InputStream inputStream =null;

            try{

                HttpResponse response = httpClient.execute(httpPost);

                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();

                BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream,"UTF-8"),8);

                StringBuilder sb = new StringBuilder();

                String line = null;

                while((line = reader.readLine()) != null){

                    sb.append(line + "\n");

                }

                jsonString = sb.toString();

                JSONObject jsonObject = new JSONObject(jsonString);

                JSONArray jsonArray = jsonObject.getJSONArray("translations");

                outputTranslations(jsonArray);


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            // Put the translations in the TextView
            TextView translationTextView = (TextView) findViewById(R.id.tranlationTextView);

            translationTextView.setText(result);
        }
        protected void outputTranslations(JSONArray jsonArray){

            // Used to get the translation using a key
            String[] languages = {"arabic", "chinese", "danish", "dutch",
                    "french", "german", "italian", "portuguese", "russian",
                    "spanish"};

            // Save all the translations by getting them with the key
            try{

                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject translationObject =
                            jsonArray.getJSONObject(i);

                    result = result + languages[i] + " : " +
                            translationObject.getString(languages[i]) + "\n";

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
