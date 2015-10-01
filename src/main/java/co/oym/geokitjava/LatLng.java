package co.oym.geokitjava;

/**
 * A WGS84 coordinate.
 *
 */
public class LatLng {

	public double lat;
	public double lng;
	
	public LatLng() {
	}
	
	public LatLng(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "LatLng [lat=" + lat + ", lng=" + lng + "]";
	}
}
