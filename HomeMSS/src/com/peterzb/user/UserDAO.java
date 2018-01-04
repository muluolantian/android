package com.peterzb.user;

/*
 * 验证用户合法性
 * 
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import com.peterzb.*;

public class UserDAO 
{
    public int CheckUser(String url, String userName, String pwd)
    {
    	int resultCode = 0;
    	try
    	{
    		Map<String, String> data = new HashMap<String, String>();
            data.put("status", "login");
            data.put("LoginID", userName);
            data.put("Password", pwd);
            
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, String> m : data.entrySet()) {
                postData.add(new BasicNameValuePair(m.getKey(), m.getValue()));
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, HTTP.UTF_8);
            httpPost.setEntity(entity);
            
            HttpResponse response = httpClient.execute(httpPost);
            
            if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
            {               
                HttpEntity httpEntity = response.getEntity();
                
                InputStream is = httpEntity.getContent();
                byte [] b = new byte[1];
                is.read(b);
                String str = new String(b);
                resultCode = Integer.parseInt(str);	
            }
            else
            {
            	resultCode = 4;
            }           
    	}
    	catch(Exception e)
    	{
    		resultCode = 4;
    	}
    	
    	return resultCode;
    }
    
    private String convertStreamToString(InputStream is)
    {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();   

        String line = null;
        try 
        {
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line + "\n");
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }   

        return sb.toString();
    }
}
