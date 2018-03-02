package co.oym.geokitjava.test;

import java.util.ArrayList;

import co.oym.geokitjava.*;
import co.oym.geokitjava.WSClient.JSON;

/**
 * A Test class. (for debugging purpose)
 */
public class AppTest3 {
	
	public static void main(String[] args) {
		
		try {
/*			final String OYM_URL = "http://stage-kartta.fonecta.fi/oym2";
			final String OYM_APP_KEY = "nokey-fonecta";
			final String OYM_APP_REFERER = "http://stage-kartta.fonecta.fi/demo/poc";*/
			final String OYM_URL = "http://maps.onyourmap.com/oym2";
			final String OYM_APP_KEY = "nokey-maps";
			final String OYM_APP_REFERER = "http://onyourmap.com";
				
			
			final WSClient oymClient = new WSClient(OYM_URL, OYM_APP_KEY, OYM_APP_REFERER);
/*
			Place.SearchRequest req = new Place.SearchRequest();
			//req.address = "Hämeenkatu";
			//req.address = "90100, Oulu, Pohjois-Suomi";
			//req.address = "02770, Espoo, Etelä-Suomi";
			req.address = "helsinki";
			req.country = "FI";
			
			System.out.println("request: " + JSON.toString(req));;
			Place.SearchResponse resp = oymClient.PlaceWS.search(req, null);
			System.out.println("response: " + JSON.toString(resp));*/
	
			//////////////// directions (routing)
			Route.Request req4 = new Route.Request();
			req4.start = new LatLng(61.493637, 23.775131);
			req4.end = new LatLng(60.374497, 21.34619);
			req4.distanceUnit = Route.Request.UNIT_KM;
			req4.transportMode = Route.Request.TM_FASTEST_CAR;
			System.out.println("request: " + JSON.toString(req4));
	/*		
			req4.vias = new ArrayList<>();
			req4.vias.add(new LatLng(63.468927, 23.749955));
		*/	
			// Sync request
			Route.Response resp4 = oymClient.RouteWS.directions(req4, null); 
	//		System.out.println("response: " + JSON.toString(resp4));

			
			
			// Display all the route instructions 
			java.io.InputStream resourceInputStream = ClassLoader.class.getResourceAsStream("/oym-route.xml");
			Route.Utility.loadTranslation("en", resourceInputStream);
			// load additional languages if needed
			// ...
			Route.Utility.Resources res = Route.Utility.getResources("en");
			
			int i = 0;
			for (Route.Instruction instruction : resp4.instructions) {
			    System.out.println(i++ + " : " + Route.Utility.renderInstruction(instruction, res));
			}
	
//			may close async threads
//			oymClient.shutdown();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
