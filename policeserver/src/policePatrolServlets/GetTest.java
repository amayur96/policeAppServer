package policePatrolServlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class GetTest {
	
	public static void main(String[] args) {
		// do work here
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String dataJsonStr = null;

        try {
            // construct URL
            String baseURL = "http://localhost:8080/policeserver/updateJSON";
            URL url = new URL(baseURL);
            // create request to API call.
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // starts query
            conn.connect();
            int response = conn.getResponseCode();
            System.out.println("HTTP Connection: " +"The response is " + response); // 200 means success

            // read input stream back to stream to build json str
            InputStream inputStream = conn.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                System.out.println("input stream was null");
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                System.out.println("stream was empty");
            }

            dataJsonStr = buffer.toString();
            System.out.println(dataJsonStr);

        } catch (IOException e) {
            System.out.println("HTTP Connection" + " Error");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    System.out.println("Error closing stream");
                }
            }
        }
	}
}
