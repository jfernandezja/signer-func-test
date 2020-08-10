package com.signer.func.test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;



public class OAuthServiceTest {
    @Test public void validOAuthCGGTest() throws Exception {
    	String ipAddress = "oauth-service-func.signer-network";
        InetAddress inet = InetAddress.getByName(ipAddress);

        Assert.assertTrue(inet.isReachable(5000));
        
        
    	HttpPost post = new HttpPost("http://oauth-service-func.signer-network:9080/authserver/v1/oauth/token");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }
}
