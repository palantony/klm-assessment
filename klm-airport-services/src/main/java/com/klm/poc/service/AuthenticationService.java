package com.klm.poc.service;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.klm.poc.util.ApplicationConstants;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Antony
 *
 */
@Service
@Slf4j
public class AuthenticationService  {

    @Value("${server.auth.username}")
    private String username;

    @Value("${server.auth.password}")
    private String password;

    @Value("${server.url.base}")
    private String baseUrl;
    
    @Value("${server.auth.granttype}")
    private String grantType;
    
    @Value("${server.url.tokenurl}")
    private String tokenUrl;

    private volatile String token;
    private volatile long tokenExpiresAtMillis = Long.MIN_VALUE;
    
    @Autowired
	 RestTemplate restTemplate;
    

    public String getToken() throws  JSONException {
        if (System.currentTimeMillis() > tokenExpiresAtMillis) {
            synchronized (this) {
                if (System.currentTimeMillis() > tokenExpiresAtMillis) {
                    obtainToken();
                }
            }
        }

        return token;
    }

    private void obtainToken() throws  JSONException {
    	
		String credentials = username+":"+password;
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
		ResponseEntity<String> response = null;
		StringBuffer access_token_url = new StringBuffer();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);

		access_token_url.append(baseUrl);
		access_token_url.append(tokenUrl);
		access_token_url.append("?grant_type=");
		access_token_url.append(grantType);
		
		log.info("Access Token URL >>>>  "+access_token_url);
		
		response = restTemplate.exchange(access_token_url.toString(), HttpMethod.POST, request,String.class);

    	
		JSONObject jsonObj = new JSONObject(response.getBody());
		token = jsonObj.get("access_token").toString();
		
    }
}
