package com.matchme.srv.service;

import static com.matchme.srv.util.LocationUtils.calculateDistance;

import ch.hsr.geohash.GeoHash;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * Service for handling geohash-related operations, including coordinate conversion and finding
 * geohashes within a specified radius.
 */
@Service
public class GeohashService {

  private static final int GEOHASH_PRECISION = 6; // Precision for user location geohashes

  /**
   * Converts latitude and longitude coordinates to a 6-character geohash string. This method takes
   * coordinates (assumed to be retrieved from the browser's geolocation API) and converts them into
   * a geohash string, which is a compact representation of a location. The 6-character precision
   * provides a resolution of approximately 0.61 km x 0.61 km, suitable for storing user locations
   * accurately in the dating app.
   *
   * @param coordinates List containing exactly 2 elements: [latitude, longitude]
   * @return Geohash string of length GEOHASH_PRECISION
   * @throws IllegalArgumentException if coordinates are null, don’t contain two elements, or
   *     contain invalid latitude/longitude values
   */
  public String coordinatesToGeohash(List<Double> coordinates) {
    if (coordinates == null || coordinates.size() != 2) {
      throw new IllegalArgumentException("Coordinates must contain latitude and longitude");
    }

    double latitude = coordinates.get(0);
    double longitude = coordinates.get(1);

    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90");
    }
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180");
    }
    return GeoHash.withCharacterPrecision(latitude, longitude, GEOHASH_PRECISION).toBase32();
  }

  /**
   * Finds all 3-character geohash prefixes within a specified radius from a center geohash.
   *
   * <p>This method implements the core of the proximity-based recommendation filter. It takes the
   * user's location (as a 6-character geohash) and their preferred radius (in kilometers), then
   * returns a set of 3-character geohash prefixes covering all areas within that radius. These
   * prefixes are used to efficiently filter potential matches from a database.
   *
   * <p>Approach? - Geohashes for Proximity: Geohashes encode locations into strings where nearby
   * locations share common prefixes. Using 3-character prefixes (covering ~156 km x 78 km) allows
   * us to quickly identify broad areas containing potential matches. - Efficiency: Instead of
   * calculating distances for every user, we pre-filter using geohash prefixes, reducing the number
   * of precise distance calculations. - User Preferences: The radius is user-specified, enabling
   * personalized proximity filtering.
   *
   * <p>How It Works: 1. Bounding Box Approximation: - Convert the radius (in km) to degrees of
   * latitude and longitude. - Latitude: ~1 degree = 111 km. - Longitude: Adjusted by latitude using
   * cosine (since longitude lines converge at poles). - This defines a square bounding box around
   * the center point.
   *
   * <p>2. Grid Search: - Iterate over a grid within the bounding box, with a step size of 0.5
   * degrees (~55 km). - Generate a 3-character geohash for each grid point. - Calculate the
   * distance from the center to the geohash cell’s center. - Include the geohash prefix if it’s
   * within the radius.
   *
   * <p>3. Validation: - Ensure the input geohash is valid and the radius is non-negative.
   *
   * <p>This method balances accuracy and performance, ensuring only users within the specified
   * radius are recommended.
   *
   * @param centerGeohash The 6-character geohash string representing the user’s location
   * @param radiusKm The user-specified radius in kilometers
   * @return Set of 3-character geohash strings covering the area within the radius
   * @throws IllegalArgumentException if centerGeohash is null, empty or invalid or if radiusKm is
   *     negative
   */
  public Set<String> findGeohashesWithinRadius(String centerGeohash, int radiusKm) {
    if (centerGeohash == null || centerGeohash.isEmpty()) {
      throw new IllegalArgumentException("centerGeohash cannot be null or empty");
    }
    if (!centerGeohash.matches("^[0-9b-hjkmnp-z]+$")) {
      throw new IllegalArgumentException("centerGeohash contains invalid characters");
    }
    if (radiusKm < 0) {
      throw new IllegalArgumentException("radiusKm cannot be negative");
    }

    // Edge case: if radius is 0, return only the center geohash’s 3-character prefix
    if (radiusKm == 0) {
      return Set.of(centerGeohash.substring(0, 3));
    }

    GeoHash center = GeoHash.fromGeohashString(centerGeohash);
    double lat = center.getBoundingBoxCenter().getLatitude();
    double lon = center.getBoundingBoxCenter().getLongitude();

    // Approximate degrees per km for bounding box calculation
    double latDegreePerKm = 1.0 / 111.0;
    double lonDegreePerKm = 1.0 / (111.0 * Math.cos(Math.toRadians(lat)));
    double latDelta = radiusKm * latDegreePerKm;
    double lonDelta = radiusKm * lonDegreePerKm;

    // Define bounding box
    double minLat = lat - latDelta;
    double maxLat = lat + latDelta;
    double minLon = lon - lonDelta;
    double maxLon = lon + lonDelta;

    Set<String> geohashes = new HashSet<>();
    double stepLat = 0.5; // ~55 km, coarse enough for 3-character precision
    double stepLon = 0.5;

    // Iterate over grid points within the bounding box
    for (double currLat = minLat; currLat <= maxLat; currLat += stepLat) {
      for (double currLon = minLon; currLon <= maxLon; currLon += stepLon) {
        GeoHash gh = GeoHash.withCharacterPrecision(currLat, currLon, 3);
        double ghLat = gh.getBoundingBoxCenter().getLatitude();
        double ghLon = gh.getBoundingBoxCenter().getLongitude();
        double distance = calculateDistance(lat, lon, ghLat, ghLon);
        if (distance <= radiusKm) {
          geohashes.add(gh.toBase32().substring(0, 3));
        }
      }
    }
    return geohashes;
  }
}
