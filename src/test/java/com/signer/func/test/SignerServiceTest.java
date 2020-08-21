package com.signer.func.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;



public class SignerServiceTest {
	private static String AS_SERVER_HOST = "oauth-service-func";
	private static final String AS_SERVER_PORT = "8080";
	
	private static String SERVER_HOST = "signer-service-func";
	private static final String SERVER_PORT = "8080";
	
	private static String accessToken;
	
	@BeforeClass
	public static void setup() throws Exception {
		AS_SERVER_HOST = AS_SERVER_HOST + "-" + System.getenv("GIT_BRANCH").replace("origin/", "");
		SERVER_HOST = SERVER_HOST + "-" + System.getenv("GIT_BRANCH").replace("origin/", "");
		
		HttpPost post = new HttpPost("http://" + AS_SERVER_HOST + ":" + AS_SERVER_PORT + "/authserver/v1/oauth/token");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        post.setHeader("Authorization", "Basic bXljbGllbnQ6cGFzcw==");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
        	accessToken = obj.get("access_token").toString();
        }
	}
	
	@Test 
    public void noAuthenticatedKeyPairGenerationTest() throws Exception {
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/signer/v1/key_pairs");
        
        JSONObject req = new JSONObject();
        req.put("algorithm", "RSA").put("size", 2048);
        post.setEntity(new ByteArrayEntity(req.toString().getBytes()));
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }
	
	@Test 
    public void invalidBearerKeyPairGenerationTest() throws Exception {
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/signer/v1/key_pairs");
    	post.setHeader("Authorization", "Bearer AAAAAAAAAAAAAA=");
    	 
        JSONObject req = new JSONObject();
        req.put("algorithm", "RSA").put("size", 2048);
        post.setEntity(new ByteArrayEntity(req.toString().getBytes()));
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	Assert.assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }
	
	@Test 
    public void invalidRequestKeyPairGenerationTest() throws Exception {
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/signer/v1/key_pairs");
    	post.setHeader("Authorization", "Bearer " + accessToken);
    	 
        JSONObject req = new JSONObject();
        req.put("algorithm", "ECC").put("size", 2048);
        post.setEntity(new ByteArrayEntity(req.toString().getBytes()));
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }
	
    @Test 
    public void validKeyPairGenerationTest() throws Exception {
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/signer/v1/key_pairs");
        post.setHeader("Authorization", "Bearer " + accessToken);
        
        JSONObject req = new JSONObject();
        req.put("algorithm", "RSA").put("size", 2048);
        post.setEntity(new ByteArrayEntity(req.toString().getBytes()));
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
        	Assert.assertNotNull(obj.get("public_key"));
        	Assert.assertNotNull(obj.get("private_key"));
        }
    }
    
    @Test 
    public void invalidAuthenticationSignatureGenerationTest() throws Exception {
    	String privateKey = generateKeyPair();
    	
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/signer/v1/signatures");
        post.setHeader("Authorization", "Bearer AAAAA");
        
        JSONObject req = new JSONObject();
        req.put("private_key", privateKey).put("data", "VGhpcyBpcyB0aGUgZGF0YSB0byBiZSBzaWduZWQK").put("signature_algorithm", "SHA256withRSA");
        post.setEntity(new ByteArrayEntity(req.toString().getBytes()));
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	Assert.assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }
    
    @Test 
    public void invalidRequestGenerationTest() throws Exception {
    	String privateKey = generateKeyPair();
    	
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/signer/v1/signatures");
        post.setHeader("Authorization", "Bearer " + accessToken);
        
        JSONObject req = new JSONObject();
        req.put("private_key", privateKey).put("data", "VGhpcyBpcyB0aGUgZGF0YSB0byBiZSBzaWduZWQK").put("signature_algorithm", "aaaa");
        post.setEntity(new ByteArrayEntity(req.toString().getBytes()));
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }
    
    @Test 
    public void invalidKeySignatureGenerationTest() throws Exception {
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/signer/v1/signatures");
        post.setHeader("Authorization", "Bearer " + accessToken);
        
        JSONObject req = new JSONObject();
        req.put("private_key", "AAAAAAAA").put("data", "VGhpcyBpcyB0aGUgZGF0YSB0byBiZSBzaWduZWQK").put("signature_algorithm", "SHA256withRSA");
        post.setEntity(new ByteArrayEntity(req.toString().getBytes()));
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	Assert.assertEquals(500, response.getStatusLine().getStatusCode());
        }
    }
    
    @Test 
    public void validSignatureGenerationTest() throws Exception {
    	String privateKey = generateKeyPair();
    	
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/signer/v1/signatures");
        post.setHeader("Authorization", "Bearer " + accessToken);
        
        JSONObject req = new JSONObject();
        req.put("private_key", privateKey).put("data", "VGhpcyBpcyB0aGUgZGF0YSB0byBiZSBzaWduZWQK").put("signature_algorithm", "SHA256withRSA");
        post.setEntity(new ByteArrayEntity(req.toString().getBytes()));
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
        	
        	Assert.assertNotNull(obj.get("signature"));
        }
    }
    
    public String generateKeyPair() throws Exception {
    	HttpPost post = new HttpPost("http://" + SERVER_HOST + ":" + SERVER_PORT + "/signer/v1/key_pairs");
        post.setHeader("Authorization", "Bearer " + accessToken);
        
        JSONObject req = new JSONObject();
        req.put("algorithm", "RSA").put("size", 2048);
        post.setEntity(new ByteArrayEntity(req.toString().getBytes()));
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        	CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
        	return obj.get("private_key").toString();
        }
    }
}
