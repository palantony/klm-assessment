package com.klm.poc.controller;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.klm.poc.service.AuthenticationService;
import com.klm.poc.util.ReturnPath;
import com.mashape.unirest.http.exceptions.UnirestException;


@Controller
public class HomeController {

	@Autowired
	 RestTemplate restTemplate;
	 
	@Autowired
	private AuthenticationService authenticationService;

	@Value("${server.url.base}")
	private String baseUrl;

	@Value("${server.url.airportservice}")
	private String airportServiceUrl;
	
	@RequestMapping(value = "/", method = RequestMethod.GET )
    public String home() {
        return ReturnPath.HOME_PAGE;
    }

	@GetMapping(path = "/home")
	public String getHome(Model model) {
		return ReturnPath.HOME_PAGE;
	}
	
	@RequestMapping(value = "/deals", method = RequestMethod.GET )
    public String deals() throws RestClientException,  JSONException {
		
        return ReturnPath.DEALS_PAGE;
    }
	
	
//	@RequestMapping(value = "/fares", method = RequestMethod.GET )
//    public ResponseEntity<String> getFares() throws RestClientException, UnirestException, JSONException {
//		
//		return new ResponseEntity<>("Antony Test String", HttpStatus.OK);
//    }
	
	
	@RequestMapping(value = "/metrics", method = RequestMethod.GET )
    public String metrics() {
        return ReturnPath.METRICS_PAGE;
    }
}
