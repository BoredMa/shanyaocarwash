package com.example.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;

import android.util.Log;
import android.widget.Toast;


public class WebServicePost {

    public static String IP = "192.168.191.1:8080";

    // ͨ�� POST ��ʽ��ȡHTTP���������
    public  String executeHttpPost(String username, String password) {

        try {
        	String path = "http://" + IP + "/WirelessOrder/Login";
     
            // ����ָ�����Ϣ
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            params.put("password", password);

            return sendPOSTRequest(path, params, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    //ͨ��post����ע����Ϣ
    public static String executeHttpRegisterPost(String phone,String name, String age,String sex){
    	try {
        	String path = "http://" + IP + "/WirelessOrder/Register";
            // ����ָ�����Ϣ
            Map<String, String> params = new HashMap<String, String>();
            params.put("phone", phone);
            params.put("name", name);
            params.put("age", age);
            params.put("sex", sex);

            return sendPOSTRequest(path, params, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
		}

    // ���?���������
    
    public static String sendPOSTRequest(String path, Map<String, String> params, String encoding) throws Exception {
    	
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (params != null && !params.isEmpty()) {
        	Iterator it = params.entrySet().iterator();
        	while(it.hasNext()){
        		Map.Entry<String, String> entry = (Map.Entry)it.next();
        		pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        	}
        }
          
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,encoding);
        HttpPost post = new HttpPost(path);
        post.setEntity(entity);
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
        HttpResponse response = client.execute(post);
        System.out.println(response.getParams().getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET));
        String info = getInfo(response);
        return info;
    }

    
    public static String sendPOSTNoParams(String path) throws Exception {    	
        HttpPost post = new HttpPost(path);
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        HttpResponse response = client.execute(post);
        String info = getInfo(response);
        return info;
    }
    
    // ��ȡ���
    private static String getInfo(HttpResponse response) throws Exception {

        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        // ��������ת��Ϊbyte��
        byte[] data = WebService.read(is);
        // ת��Ϊ�ַ�

        return new String(data, "UTF-8");
    }

}