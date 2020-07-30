package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class HttpJSONParser
{
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    HttpURLConnection urlConnection = null;

    public JSONObject makeHTTPRequest(String url, String method, Map<String, String> params)
    {
        try
        {
            Uri.Builder builder = new Uri.Builder();
            URL urlObj;

            String encodedParams = "";

            if (params != null)
            {//parse parameters
                for (Map.Entry<String, String> entry : params.entrySet())
                {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue());
                }
            }

            if (builder.build().getEncodedQuery() != null)
            {//get parameters, if any
                encodedParams = builder.build().getEncodedQuery();
            }

            if (method.equals("GET"))
            {
                try
                {
                    url = url + "?" + encodedParams;
                    urlObj = new URL(url);
                    urlConnection = (HttpURLConnection) urlObj.openConnection();
                    urlConnection.setRequestMethod(method);
                }
                catch(Exception ex)
                {
                    System.out.println("ERROR PARSEANDO GET, JSON PARSER");
                    ex.printStackTrace();
                }
            }
            else //POST
            {
                try
                {
                    urlObj = new URL(url);
                    urlConnection = (HttpURLConnection) urlObj.openConnection();
                    urlConnection.setRequestMethod(method);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("Content-Length", String.valueOf(encodedParams.getBytes().length));
                    urlConnection.getOutputStream().write(encodedParams.getBytes());
                }
                catch(Exception ex)
                {
                    System.out.println("ERROR PARSEANDO POST, JSON PARSER");
                    ex.printStackTrace();
                }
            }

            urlConnection.connect();
            is = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            is.close();

            json = sb.toString();
            jObj = new JSONObject(json);
        }
        catch (Exception ex)
        {
            System.out.println("ERROR PARSEANDO TODO, JSON PARSER");
            ex.printStackTrace();
        }

        return jObj;
    }
}
