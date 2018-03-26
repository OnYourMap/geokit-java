package co.oym.geokitjava;

import org.codehaus.jackson.type.JavaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OnYourMap GIS Web Services Client. <br>
 * Features: <br>
 *  - Geocoding (address lookup) <br>
 *  - Reverse geocoding (get address from coordinate) <br>
 *  - Routing (get directions between 2 locations) <br>
 *  - Mapping utility class for MapBox SDK <br><br>
 *  Before starting using this client, you must have first contacted OnYourMap support in order to get credentials for accessing its Web Services.
 *  Three parameters are mandatory: <br>
 *  - webServiceUrl: The url of OnYourMap Web Services <br>
 *  - appReferer: Your application Identifier when using OnYourMap Web Services <br>
 *  - appKey: Your application key when using OnYourMap Web Services <br>
 *
 */
public class WSClient {

	public static final String TAG = "mapbox_oym";
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private OkHttpClient client = new OkHttpClient();

	private String webServiceUrl;
	private String appKey;
	private String appReferer;

	/**
	 * The client entry point.
	 * @param webServiceUrl
	 */
	public WSClient(String webServiceUrl) {
		this(webServiceUrl, null, null);
	}

	/**
	 * The client entry point.
	 * @param webServiceUrl
	 * @param appKey
	 * @param appReferer
	 */
	public WSClient(String webServiceUrl, String appKey, String appReferer) {
		this.webServiceUrl = webServiceUrl;
		this.appKey = appKey;
		this.appReferer = appReferer;
	}

	/**
	 * Get access to the OkHttpClient object for settings additional parameters like proxy
	 * @return
	 */
	public OkHttpClient getOkHttpClient() {
		return client;
	}

	/**
	 * You can replace the OkHttpClient object used internally by this library if you already use another OkHttpClient in your code and want to reduce memory footprint for httpclients.
	 * @param client
	 */
	public void setOkHttpClient(OkHttpClient client) {
		this.client = client;
	}

	/**
	 * Call this method if you need to kill OkHttpClient thread pool when you need to exit your application
	 */
	public void shutdown() {
		client.dispatcher().executorService().shutdown();
	}

	private String jsonify(Object obj) throws Exception {
		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		JSON.mapper.writeValue(out, obj);

		final byte[] data = out.toByteArray();
		String str = new String(data);
		return str;
	}

	/**
	 * Do not use manually. Only used internally for decoding Web Services content.
	 * @param json
	 * @param outputClass
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public static <T> T decodeContent(String json, Class<T> outputClass) throws Exception {
		
		WSResponse<T> resp = null;
		try {
			JavaType javaType = JSON.mapper.getTypeFactory().constructParametricType(WSResponse.class, outputClass);
			resp = JSON.mapper.readValue(json, javaType);
		} catch (Exception ex) {
			JavaType javaType = JSON.mapper.getTypeFactory().constructParametricType(WSResponse.class, String.class);
			resp = JSON.mapper.readValue(json, javaType);
		}
		if (resp == null || !resp.statusCode.equals("200")) {
			throw new WSException(resp.statusCode, (String) resp.data);
		}
		return resp.data;
	}

	private String execute(okhttp3.Request request) throws Exception {
		// POST
		try {
			okhttp3.Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
        		String content = response.body().string();
				return content;
				
			} else {
				throw new Exception("network error: " + response.code());
			}

		} catch (Exception ex) {
			throw ex;

		} finally {
		}
	}

	public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

	public RouteWS RouteWS = new RouteWS();
	public PlaceWS PlaceWS = new PlaceWS();

	/**
	 * Place Web Service allows to: <br>
	 *  - Search for an address (and get its WGS84 coordinate) <br>
	 *  - Get nearest address from a WGS84 coordinate <br>
	 */
	public class PlaceWS {

		/**
		 * Search for an address and get its WGS84 coordinate. <br>
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.SearchResponse search(Place.SearchRequest request, final WSCallback<Place.SearchResponse> callback) throws Exception {
			return search(appKey, request, callback);
		}

		/**
 		 * Search for an address and get its WGS84 coordinate.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param appKey
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.SearchResponse search(String appKey, Place.SearchRequest request, final WSCallback<Place.SearchResponse> callback) throws Exception {
			
			String jsonObject = jsonify(request);
			
			okhttp3.Request okReq = new okhttp3.Request.Builder()
			.url(webServiceUrl + "/place/search")
			.post(RequestBody.create(JSON_TYPE, jsonObject))
	        .addHeader("Content-Type", "application/json; charset=utf-8")
			.addHeader("appKey", appKey)
	        .addHeader("Referer", appReferer)
			.build();

			if (callback == null) {
				String content = execute(okReq);
				return decodeContent(content, Place.SearchResponse.class);
				
			} else {
				DownloadTask<Place.SearchResponse> task = new DownloadTask<Place.SearchResponse>(client, DownloadTask.PLACE_SEARCH, okReq, callback);
				executor.submit(task);
				return null;
			}
		}

		/**
		 * Get nearest address from a WGS84 coordinate.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.NearestResponse nearest(Place.NearestRequest request, final WSCallback<Place.NearestResponse> callback) throws Exception {
			return nearest(appKey, request, callback);
		}

		/**
		 * Get nearest address from a WGS84 coordinate.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param appKey
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.NearestResponse nearest(String appKey, Place.NearestRequest request, final WSCallback<Place.NearestResponse> callback) throws Exception {

			String jsonObject = jsonify(request);

			okhttp3.Request okReq = new okhttp3.Request.Builder()
					.url(webServiceUrl + "/place/nearest")
					.post(RequestBody.create(JSON_TYPE, jsonObject))
					.addHeader("Content-Type", "application/json; charset=utf-8")
					.addHeader("appKey", appKey)
					.addHeader("Referer", appReferer)
					.build();

			if (callback == null) {
				String content = execute(okReq);
				return decodeContent(content, Place.NearestResponse.class);

			} else {
				DownloadTask<Place.NearestResponse> task = new DownloadTask<Place.NearestResponse>(client, DownloadTask.PLACE_NEAREST, okReq, callback);
				executor.submit(task);
				return null;
			}
		}

		/**
		 * Get address suggests from an autocomplete string.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.AutocompleteResponse autocomplete(Place.AutocompleteRequest request, final WSCallback<Place.AutocompleteResponse> callback) throws Exception {
			return autocomplete(appKey, request, callback);
		}

		/**
		 * Get address suggests from an autocomplete string.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param appKey
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Place.AutocompleteResponse autocomplete(String appKey, Place.AutocompleteRequest request, final WSCallback<Place.AutocompleteResponse> callback) throws Exception {

			String jsonObject = jsonify(request);

			okhttp3.Request okReq = new okhttp3.Request.Builder()
					.url(webServiceUrl + "/place/autocomplete")
					.post(RequestBody.create(JSON_TYPE, jsonObject))
					.addHeader("Content-Type", "application/json; charset=utf-8")
					.addHeader("appKey", appKey)
					.addHeader("Referer", appReferer)
					.build();

			if (callback == null) {
				String content = execute(okReq);
				return decodeContent(content, Place.AutocompleteResponse.class);

			} else {
				DownloadTask<Place.AutocompleteResponse> task = new DownloadTask<Place.AutocompleteResponse>(client, DownloadTask.PLACE_AUTOCOMPLETE, okReq, callback);
				executor.submit(task);
				return null;
			}
		}

	}

	/**
	 * Route Web Service allows to: <br>
	 *  - Get directions between 2 WGS84 coordinates
	 */
	public class RouteWS {

		/**
		 * Get directions between 2 WGS84 coordinates.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Route.Response directions(Route.Request request, final WSCallback<Route.Response> callback) throws Exception {
			return directions(appKey, request, callback);
		}

		/**
		 * Get directions between 2 WGS84 coordinates.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param appKey
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Route.Response directions(String appKey, Route.Request request, final WSCallback<Route.Response> callback) throws Exception {

			String jsonObject = jsonify(request);

			okhttp3.Request okReq = new okhttp3.Request.Builder()
					.url(webServiceUrl + "/route/directions")
					.post(RequestBody.create(JSON_TYPE, jsonObject))
					.addHeader("Content-Type", "application/json; charset=utf-8")
					.addHeader("appKey", appKey)
					.addHeader("Referer", appReferer)
					.build();

			if (callback == null) {
				String content = execute(okReq);
				return decodeContent(content, Route.Response.class);

			} else {
				DownloadTask<Route.Response> task = new DownloadTask<Route.Response>(client, DownloadTask.ROUTE_DIRECTIONS, okReq, callback);
				executor.submit(task);
				return null;
			}
		}
		
		/**
		 * Rank points by distances and travel time.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Route.RankingResponse rankPoints(Route.RankingRequest request, final WSCallback<Route.RankingResponse> callback) throws Exception {
			return rankPoints(appKey, request, callback);
		}

		/**
		 * Rank points by distances and travel time.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param appKey
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Route.RankingResponse rankPoints(String appKey, Route.RankingRequest request, final WSCallback<Route.RankingResponse> callback) throws Exception {

			String jsonObject = jsonify(request);

			okhttp3.Request okReq = new okhttp3.Request.Builder()
					.url(webServiceUrl + "/route/rankPoints")
					.post(RequestBody.create(JSON_TYPE, jsonObject))
					.addHeader("Content-Type", "application/json; charset=utf-8")
					.addHeader("appKey", appKey)
					.addHeader("Referer", appReferer)
					.build();

			if (callback == null) {
				String content = execute(okReq);
				return decodeContent(content, Route.RankingResponse.class);

			} else {
				DownloadTask<Route.RankingResponse> task = new DownloadTask<Route.RankingResponse>(client, DownloadTask.ROUTE_RANK_POINTS, okReq, callback);
				executor.submit(task);
				return null;
			}
		}
		
		/**
		 * Compute route isochrone: polygon containing all possible routes within provided max time.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Route.IsochroneResponse isochrone(Route.IsochroneRequest request, final WSCallback<Route.IsochroneResponse> callback) throws Exception {
			return isochrone(appKey, request, callback);
		}

		/**
		 * Compute route isochrone: polygon containing all possible routes within provided max time.
		 * If a callback object is provided, then the method will be executed asynchronously. If callback is null, then the method will be executed synchronously.
		 * @param appKey
		 * @param request
		 * @param callback
		 * @return
		 * @throws Exception
		 */
		public Route.IsochroneResponse isochrone(String appKey, Route.IsochroneRequest request, final WSCallback<Route.IsochroneResponse> callback) throws Exception {

			String jsonObject = jsonify(request);

			okhttp3.Request okReq = new okhttp3.Request.Builder()
					.url(webServiceUrl + "/route/isochrone")
					.post(RequestBody.create(JSON_TYPE, jsonObject))
					.addHeader("Content-Type", "application/json; charset=utf-8")
					.addHeader("appKey", appKey)
					.addHeader("Referer", appReferer)
					.build();

			if (callback == null) {
				String content = execute(okReq);
				return decodeContent(content, Route.IsochroneResponse.class);

			} else {
				DownloadTask<Route.IsochroneResponse> task = new DownloadTask<Route.IsochroneResponse>(client, DownloadTask.ROUTE_ISOCHRONE, okReq, callback);
				executor.submit(task);
				return null;
			}
		}
	}


	/**
	 * Utility class for serializing/deserializing JSON<->Class
	 */
	public static class JSON {
		public static ObjectMapper mapper = new ObjectMapper();
		static {
			mapper.setSerializationInclusion(Inclusion.NON_NULL);
		}

		public static String toString(Object obj) {
			try {
				return mapper.writeValueAsString(obj);
				
			} catch (Exception ex) {
			}
			return "{}";
		}
	}
	
	private static class DownloadTask<T> implements Runnable {

		public static final int PLACE_SEARCH = 1;
		public static final int PLACE_NEAREST = 2;
		public static final int ROUTE_DIRECTIONS = 3;
		public static final int PLACE_AUTOCOMPLETE = 4;
		public static final int ROUTE_RANK_POINTS = 5;
		public static final int ROUTE_ISOCHRONE = 6;

		private final OkHttpClient client; 
		private final int type;
		private final Request req;
		private final WSCallback<T> listener;
		
		public DownloadTask(OkHttpClient client, int type, Request req, WSCallback<T> listener) {
			this.client = client;
			this.type = type;
			this.req = req;
			this.listener = listener;
		}

		public void run() {
			client.newCall(req).enqueue(new Callback() {

				@SuppressWarnings("unchecked")
				public void onResponse(Call call, Response response) throws IOException {
					String content = null;
					try {
						if (!response.isSuccessful()) {
							throw new IOException("Unexpected code " + response);
						}

						content = response.body().string();

						if (type == PLACE_SEARCH) {
							listener.onResponse((T) WSClient.decodeContent(content, Place.SearchResponse.class));

						} else if (type == PLACE_NEAREST) {
							listener.onResponse((T) WSClient.decodeContent(content, Place.NearestResponse.class));

						} else if (type == PLACE_AUTOCOMPLETE) {
							listener.onResponse((T) WSClient.decodeContent(content, Place.AutocompleteResponse.class));

						} else if (type == ROUTE_DIRECTIONS) {
							listener.onResponse((T) WSClient.decodeContent(content, Route.Response.class));

						} else if (type == ROUTE_RANK_POINTS) {
							listener.onResponse((T) WSClient.decodeContent(content, Route.RankingResponse.class));

						} else if (type == ROUTE_ISOCHRONE) {
							listener.onResponse((T) WSClient.decodeContent(content, Route.IsochroneResponse.class));
						}

					} catch (Exception ex) {
						// parsing error or real error
						String errorMessage = "oym response parsing error";
						try {
							errorMessage = WSClient.decodeContent(content, String.class);

						} catch (Exception ex2) {
						}
						listener.onFailure(errorMessage);
					}
				}

				public void onFailure(Call call, IOException e) {
					e.printStackTrace();
					listener.onFailure("network error: " + e.getMessage());
				}

			});		
		}

	}

}
