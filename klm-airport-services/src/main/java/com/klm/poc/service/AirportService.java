package com.klm.poc.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @author Antony
 *
 */
@Service
public class AirportService {

	public List<String> parseLocation(String locations) throws JSONException {
		JSONObject jsonObjRoot = new JSONObject(locations);
		List<String> airportList = null;
		if (jsonObjRoot.get("_embedded") != null 
				&& jsonObjRoot.get("_embedded") instanceof JSONObject
				&& (((JSONObject) jsonObjRoot.get("_embedded")).get("locations") != null)
				&& ((JSONObject) jsonObjRoot.get("_embedded")).get("locations") instanceof JSONArray
				) {
			
			airportList= arrayToStream((JSONArray) ((JSONObject) jsonObjRoot.get("_embedded")).get("locations"))
			.stream()
			.map(obj -> {
				try {
					return (String) obj.get("description");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return obj.toString();
			})
			.map(Object::toString)
			.collect(Collectors.toList());

		}
		System.out.println("Airport List in Service >> "+airportList);
		return airportList;
	}

	
	private List<JSONObject> arrayToStream(JSONArray array) throws JSONException {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (int i = 0; i < array.length(); i++) {
			jsonList.add(			array.getJSONObject(i));
			}
	    return jsonList;
	}
}
