Java SDK providing a set of tools for mapping, routing, geocoding and reverse geocoding.



# geokit-java

## Introduction

The Geokit is an SDK providing a set of tools performing requests on the OnYourMap platform. Those requests include geocoding, reverse geocoding and routing. 

For more details about OnYourMap, please contact our customer support here: contact@onyourmap.com


## OnYourMap webservices		

Using OnYourMap webservices for geocoding and routing is done through the class *co.oym.geokitjava.WSClient*. This class is initialised like this:

```smalltalk
WSClient oymClient = new WSClient(OYM_URL, OYM_APP_KEY, OYM_APP_REFERER);
```
	
## Geocoding

You can search places/adresses using the *search* function.

```java
Place.SearchRequest req = new Place.SearchRequest();
req.address = "rivoli paris";
```

All the parameters available for the request are described in the javadoc.

The request can be processed in a sync or async way:

```java
// Sync request
Place.SearchResponse resp = oymClient.PlaceWS.search(req, null);

// Asnyc request
oymClient.PlaceWS.search(req, new WSCallback<Place.SearchResponse>() {
	@Override
	public void onResponse(final Place.SearchResponse resp) {
		// do something with the places found
	}

	@Override
	public void onFailure(final String errorMessage) {
		// handle error
	}
});
```

## Reverse geocoding

Places can be retrieved around a location using the *nearest* function.
All the parameters available for the request are described in the javadoc.

```java
final Place.NearestRequest req = new Place.NearestRequest();
req.location = new LatLng(48.866598, 2.322464);
req.radius = 100;
```

If the radius is 0, only the nearest place will be returned. Otherwise, everything in the radius will be returned.
Like the geocoding, reverse geocoding can be computed in a sync or async way.

```java
// Sync request
Place.NearestResponse resp = oymClient.PlaceWS.nearest(req, null);

// Async request
oymClient.PlaceWS.nearest(req, new WSCallback<Place.NearestResponse>() {
	@Override
	public void onResponse(final Place.NearestResponse resp) {
		// do something with the places found
	}

	@Override
	public void onFailure(final String errorMessage) {
		// handle error
	}
});
```

## Autocomplete

Places suggests can be retrieved using *autocomplete* function. 
Note that a suggest does not contain any location coordinate (so that the result may need to be geocoded afterward).
All the parameters available for the request are described in the javadoc.

```java
final Place.AutocompleteRequest req = new Place.AutocompleteRequest();
req.place = "rivo";
```

Like the geocoding, autocomplete can be computed in a sync or async way.

```java
// Sync request
Place.AutocompleteResponse resp = oymClient.PlaceWS.autocomplete(req, null);

// Async request
oymClient.PlaceWS.autocomplete(req, new WSCallback<Place.AutocompleteResponse>() {
	@Override
	public void onResponse(final Place.AutocompleteResponse resp) {
		// do something with the suggested places
	}

	@Override
	public void onFailure(final String errorMessage) {
		// handle error
	}
});
```

## Routing

The function *directions* provides a route between two coordinates.
All the parameters available for the request are described in the javadoc.

```java
Route.Request req = new Route.Request();
req.start = new LatLng(48.866598, 2.322464);
req.end = new LatLng(48.858620, 2.293961);
req.distanceUnit = Route.Request.UNIT_KM;
req.transportMode = Route.Request.TM_FASTEST_CAR;
```

Like the geocoding, route can be computed in a sync or async way.

```java
// Sync request
Route.Response resp = oymClient.RouteWS.directions(req, null); 

// Async request
oymClient.RouteWS.directions(req, new WSCallback<Route.Response>() {
	@Override
	public void onResponse(final Route.Response resp) {
		// do something with the computed route
	}

	@Override
	public void onFailure(final String errorMessage) {
		// handle error
	}
});
```

## Routing utilities

Some functions are here to simplify the developers life when using the *directions* function.
They can be found in *Route.Utility* class.


### checkDisplayLevel

The route geometry is provided with the maximum accuracy available. In order to speed up the route shape rendering, an array called "levels" is returned in the *Route.Response* object. This array contains, for each point of the route, all the zoom levels this point should be displayed. The static method *Route.Utility.checkDisplayLevel()* will tell you if a point should be displayed or not, based on this value. That way, the number of points to display can be greatly reduced, with a huge speed boost when rendering long routes.

```java
if (Route.Utility.checkDisplayLevel(displayLevelValue, currentZoomLevel)) {
	// keep the point for this zoom level
}
```

### renderInstruction

The instructions returned by the *directions* function must be processed before being displayed on screen. The static method *Route.Utility.renderInstruction()* will transform an encoded instruction into a human readable string.

```java
// load translations for route instructions resources
java.io.InputStream resourceInputStream = ClassLoader.class.getResourceAsStream("/oym-route.xml");
Route.Utility.loadTranslation("en", resourceInputStream);
// ... load additional languages if needed

// retrieve loaded resources for English language
Route.Utility.Resources res = Route.Utility.getResources("en");

// Display all the route instructions 
for (Route.Instruction instruction : resp.instructions) {
	System.out.println(Route.Utility.renderInstruction(instruction, res));
}
```
The file listing all the possible instructions is located in the provided bundle src/main/resources/oym-route.xml. This file can be modified if needed. 
You can create new translations for additional languages by following this template.


	