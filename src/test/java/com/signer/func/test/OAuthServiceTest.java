package com.signer.func.test;

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
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;



public class OAuthServiceTest {
	private static String SERVER_HOST = "oauth-service-func";
	private static final String SERVER_PORT = "8080";
	
	@BeforeClass
	public static void initHost() {
		SERVER_HOST = System.getenv("GIT_BRANCH").replace("origin/", "");
	}
	
    @Test public void validCCGTest() throws Exception {
    	Assert.assertEquals("origin/develop", );
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/authserver/v1/oauth/token");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        post.setHeader("Authorization", "Basic bXljbGllbnQ6cGFzcw==");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
        	Assert.assertEquals("Bearer", obj.getString("token_type"));
        	Assert.assertNotNull(obj.get("access_token"));
        }
    }
    
    @Test public void invalidCredentialsTest() throws Exception {
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/authserver/v1/oauth/token");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        post.setHeader("Authorization", "Basic bXljbGllbnRhYTpwYXNz");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	Assert.assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }
    
    @Test public void invalidGrantTypeTest() throws Exception {
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/authserver/v1/oauth/token");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", "incorrect_type"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        post.setHeader("Authorization", "Basic bXljbGllbnQ6cGFzcw==");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }
    
    @Test public void invalidPathTest() throws Exception {
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/authserver/oauth/token");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", "incorrect_type"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        post.setHeader("Authorization", "Basic bXljbGllbnQ6cGFzcw==");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }
}
