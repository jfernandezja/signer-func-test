package com.signer.func.test;

import org.junit.Assert;
import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class OAuthServiceTest {
    @Test public void testSomeLibraryMethod() {
    	Vertx vertx = Vertx.vertx();
    	WebClient client = WebClient.create(vertx);
    	client
    	  .get(9080, "localhost", "/authserver/v1/oauth/token")
    	  .send(ar -> {
    	    if (ar.succeeded()) {
    	      HttpResponse<Buffer> response = ar.result();
    	      Assert.assertEquals(200, response.statusCode());
    	    } else {
    	      Assert.fail(ar.cause().getMessage());
    	    }
    	  });
    }
}
