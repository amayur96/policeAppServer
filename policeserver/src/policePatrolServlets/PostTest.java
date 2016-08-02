package policePatrolServlets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class PostTest {
	public static void main(String[] args) {
		HttpURLConnection conn = null;
		try {
         
                // construct URL
                URL url = new URL("http://localhost:8080/policeserver/updateJSON");

                // create request to API call.
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                // get ID

                JSONObject jsonGPS = new JSONObject();
                jsonGPS.put("Datetime", "12 Aug 2014");
                jsonGPS.put("ID", "32A");
                jsonGPS.put("latitude", 32.12);
                jsonGPS.put("longitude", 87.21);
                Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.write(String.valueOf(jsonGPS));
                writer.close();
                
                int response = conn.getResponseCode();
                System.out.println("HTTP Connection: " +"The response is " + response);
			
            } catch (IOException e) {
                System.out.println("HTTP Connection" + " Error");
            } catch(JSONException e) {
                System.out.println("JSON exception" + "Error");
            }
            finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
	}
}