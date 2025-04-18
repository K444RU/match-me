package com.matchme.srv.service;

import ch.hsr.geohash.GeoHash;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for handling geohash-related operations, including coordinate conversion
 * and finding geohashes within a specified radius.
 */
@Service
public class GeohashService {

    private static final int GEOHASH_PRECISION = 6;
    private static final double EARTH_RADIUS = 6371;

    /**
     * Converts latitude and longitude coordinates to a 6-character geohash string.
     * @param coordinates List containing exactly 2 elements: [latitude, longitude]
     * @return Geohash string of length GEOHASH_PRECISION
     * @throws IllegalArgumentException if coordinates are null, don’t contain two elements,
     * or contain invalid latitude/longitude values
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
     * @param centerGeohash The 6-character geohash string representing the center point
     * @param radiusKm      The radius to search within, in kilometers
     * @return Set of 3-character geohash strings covering the area within the radius
     * @throws IllegalArgumentException if centerGeohash is null, empty, or invalid,
     * or if radiusKm is negative
     */
    public Set<String> findGeohashesWithinRadius(String centerGeohash, int radiusKm) {
        if (centerGeohash == null || centerGeohash.isEmpty()) {
            throw new IllegalArgumentException("centerGeohash cannot be null or empty");
        }
        if (!centerGeohash.matches("^[0-9b-hjkmnp-z]+$")) {
            throw new IllegalArgumentException("centerGeohash contains invalid characters");
        }
        if (radiusKm <= 0) {
            return Set.of(centerGeohash);
        }

        GeoHash center = GeoHash.fromGeohashString(centerGeohash);
        double lat = center.getBoundingBoxCenter().getLatitude();
        double lon = center.getBoundingBoxCenter().getLongitude();

        // Approximate degrees per km
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
        // Step size for precision 3 (roughly 156 km x 78 km per cell)
        double stepLat = 0.5; // ~55 km, adjust if needed
        double stepLon = 0.5;

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

    /**
     * Calculates the distance between two points using the Haversine formula.
     *
     * @param lat1 Latitude of the first point
     * @param lon1 Longitude of the first point
     * @param lat2 Latitude of the second point
     * @param lon2 Longitude of the second point
     * @return Distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}