package co.oym.geokitjava.test;

import co.oym.geokitjava.*;

/**
 * A Test class. (for debugging purpose)
 */
public class AppTest {
	
	public static void main(String[] args) {
		
		try {
			final String OYM_URL = "";
			final String OYM_APP_KEY = "";
			final String OYM_APP_REFERER = "";
			
			final WSClient oymClient = new WSClient(OYM_URL, OYM_APP_KEY, OYM_APP_REFERER);
			
			//////////////// search (geocoding)
			Place.SearchRequest req1 = new Place.SearchRequest();
			req1.address = "rivoli paris";
			// Sync request
			Place.SearchResponse resp1 = oymClient.PlaceWS.search(req1, null);
			System.out.println("search sync response: " + resp1);
			
//			// Asnyc request
//			oymClient.PlaceWS.search(req1, new WSCallback<Place.SearchResponse>() {
//			    @Override
//			    public void onResponse(final Place.SearchResponse resp) {
//			        // do something with the places found
//					System.out.println("search async response: " + resp);
//			    }
//
//			    @Override
//			    public void onFailure(final String errorMessage) {
//			        // handle error
//			    }
//			});
			

			//////////////// nearest (reverse geocoding)
			final Place.NearestRequest req2 = new Place.NearestRequest();
			req2.location = new LatLng(48.866598, 2.322464);
			req2.radius = 100;
			
			// Sync request
			Place.NearestResponse resp2 = oymClient.PlaceWS.nearest(req2, null);
			System.out.println("nearest sync response: " + resp2);

//			// Async request
//			oymClient.PlaceWS.nearest(req2, new WSCallback<Place.NearestResponse>() {
//			    @Override
//			    public void onResponse(final Place.NearestResponse resp) {
//			        // do something with the places found
//					System.out.println("nearest async response: " + resp);
//			    }
//
//			    @Override
//			    public void onFailure(final String errorMessage) {
//			        // handle error
//			    }
//			});
			
			
			//////////////// nearest (reverse geocoding)
			final Place.AutocompleteRequest req3 = new Place.AutocompleteRequest();
			req3.place = "rivo";
			
			// Sync request
			Place.AutocompleteResponse resp3 = oymClient.PlaceWS.autocomplete(req3, null);
			System.out.println("autocomplete sync response: " + resp3);
			
//			// Async request
//			oymClient.PlaceWS.autocomplete(req3, new WSCallback<Place.AutocompleteResponse>() {
//			    @Override
//			    public void onResponse(final Place.AutocompleteResponse resp) {
//			        // do something with the suggested places
//			    	System.out.println("autocomplete async response: " + resp);
//			    }
//
//			    @Override
//			    public void onFailure(final String errorMessage) {
//			        // handle error
//			    }
//			});

			
			//////////////// directions (routing)
			Route.Request req4 = new Route.Request();
			req4.start = new LatLng(48.866598, 2.322464);
			req4.end = new LatLng(48.858620, 2.293961);
			req4.distanceUnit = Route.Request.UNIT_KM;
			req4.transportMode = Route.Request.TM_FASTEST_CAR;
			
			// Sync request
			Route.Response resp4 = oymClient.RouteWS.directions(req4, null); 
			System.out.println("directions sync response: " + resp4);
			
//			// Async request
//			oymClient.RouteWS.directions(req4, new WSCallback<Route.Response>() {
//			    @Override
//			    public void onResponse(final Route.Response resp) {
//			        // do something with the computed route
//			    	System.out.println("directions async response: " + resp);
//			    }
//
//			    @Override
//			    public void onFailure(final String errorMessage) {
//			        // handle error
//			    }
//			});
			
			// Display all the route instructions 
			java.io.InputStream resourceInputStream = ClassLoader.class.getResourceAsStream("/oym-route.xml");
			Route.Utility.loadTranslation("en", resourceInputStream);
			// load additional languages if needed
			// ...
			Route.Utility.Resources res = Route.Utility.getResources("en");
			
			for (Route.Instruction instruction : resp4.instructions) {
			    System.out.println(Route.Utility.renderInstruction(instruction, res));
			}
	
//			may close async threads
//			oymClient.shutdown();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
