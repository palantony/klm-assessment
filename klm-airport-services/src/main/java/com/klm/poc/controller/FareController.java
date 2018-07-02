package com.klm.poc.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.klm.poc.service.AuthenticationService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Antony
 *
 */
@Slf4j
@Controller
public class FareController {

	@Autowired
	RestTemplate restTemplate;

	@Value("${server.url.base}")
	private String baseUrl;

	@Value("${server.url.fareservice}")
	private String fareService;

	@Value("${server.url.airportservice}")
	private String airportServiceUrl;

	@Autowired
	private AuthenticationService authenticationService;

	@RequestMapping(method = GET, value = "/fares/{origin}/{destination}", produces = "application/json; charset=UTF-8")
	public ResponseEntity<String> findFare(@RequestParam Map<String, Object> params,
			@PathVariable("origin") final String origin, @PathVariable("destination") final String destination,
			Model model) throws InterruptedException, ExecutionException, JSONException {
		CompletableFuture<ResponseEntity<String>> airFareClient = CompletableFuture.supplyAsync(() -> {
			ResponseEntity<String> result = null;
			try {
				result = getFareDetail(origin, destination);
			} catch (RestClientException | JSONException e) {
				e.printStackTrace();
			}
			return result;
		});
		CompletableFuture<ResponseEntity<String>> originClient = CompletableFuture.supplyAsync(() -> {
			ResponseEntity<String> result = null;
			try {
				result = getAirportDetails(origin);
			} catch (RestClientException | JSONException e) {
				e.printStackTrace();
			}
			return result;
		});
		CompletableFuture<ResponseEntity<String>> destinationClient = CompletableFuture.supplyAsync(() -> {
			ResponseEntity<String> result = null;
			try {
				result = getAirportDetails(destination);
			} catch (RestClientException | JSONException e) {
				e.printStackTrace();
			}
			return result;
		});
		CompletableFuture.allOf(airFareClient, originClient, destinationClient).join();

		log.info("airFareClient.isDone()" + airFareClient.isDone());
		log.info("originClient.isDone()" + originClient.isDone());
		log.info("destinationClient.isDone()" + destinationClient.isDone());

		JSONObject result = new JSONObject();
		result.put("airFare", airFareClient.toString());
		result.put("originData", originClient.toString());
		result.put("destinationData", destinationClient.toString());
		return new ResponseEntity<>(result.toString(), HttpStatus.OK);
	}

	private ResponseEntity<String> getFareDetail(String origin, String destination)
			throws RestClientException, JSONException {
		ResponseEntity<String> response = null;
		response = restTemplate.exchange(getFareServiceUrl(origin, destination), HttpMethod.GET, null, String.class);
		System.out.println(response);
		return response;
	}

	private String getFareServiceUrl(String origin, String destination) throws JSONException {
		StringBuffer url = new StringBuffer("");
		url.append(baseUrl);
		url.append(fareService);
		url.append("/");
		url.append(origin);
		url.append("/");
		url.append(destination);
		url.append("/");
		url.append("?access_token=");
		url.append(authenticationService.getToken());
		return url.toString();
	}

	private ResponseEntity<String> getAirportDetails(String source) throws RestClientException, JSONException {
		ResponseEntity<String> response = null;
		response = restTemplate.exchange(getAirportServiceUrl(source), HttpMethod.GET, null, String.class);
		return response;
	}

	private String getAirportServiceUrl(String source) throws JSONException {
		StringBuffer url = new StringBuffer("");
		url.append(baseUrl);
		url.append(airportServiceUrl);
		url.append("/");
		url.append(source);
		url.append("/");
		url.append("?access_token=");
		url.append(authenticationService.getToken());
		System.out.println("Airport service url" + url.toString());
		return url.toString();
	}

}
