package com.klm.poc.controller;

import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.klm.poc.service.AirportService;
import com.klm.poc.service.AuthenticationService;
import com.klm.poc.util.ReturnPath;
import com.mashape.unirest.http.exceptions.UnirestException;

@Controller
public class AirportController {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private AirportService airportService;

	@Value("${server.url.base}")
	private String baseUrl;

	@Value("${server.url.airportservice}")
	private String airportServiceUrl;

	@GetMapping(path = "/airports/origin/{term}")
	public String getOriginAirports(@PathVariable String term,Model model)
			throws RestClientException, UnirestException, JSONException {
		model.addAttribute("originAirportList", getAirportList(term));
		model.addAttribute("originTerm", term);
		return ReturnPath.DEALS_PAGE;
	}

	@GetMapping(path = "/airports/destination/{term}")
	public String getDestionationAirports(@PathVariable String term,Model model)
			throws RestClientException, UnirestException, JSONException {
		model.addAttribute("destinationAirportList", getAirportList(term));
		model.addAttribute("destinationTerm", term);
		return ReturnPath.DEALS_PAGE;
	}
	
	private List<String> getAirportList(String term) throws JSONException, RestClientException, UnirestException {
		List<String> airportList = null;
		String response = null;
		response = restTemplate.getForObject(getAirportUrl(term), String.class);
		airportList = airportService.parseLocation(response);
		return airportList;
	}

	private String getAirportUrl(String term) throws UnirestException, JSONException {
		StringBuffer url = new StringBuffer("");
		url.append(baseUrl);
		url.append(airportServiceUrl);
		url.append("?term=");
		url.append(term);
		url.append("&access_token=");
		url.append(authenticationService.getToken());
		return url.toString();
	}
}
