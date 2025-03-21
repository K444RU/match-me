package com.matchme.srv.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.queries.GeoHashCircleQuery;

@Service
public class GeohashService {

    private static final int GEOHASH_PRECISION = 6;

    /**
     * Converts latitude and longitude coordinates to a geohash string.
     * 
     * @param coordinates List containing exactly 2 elements: [longitude, latitude]
     * @return geohash string of length GEOHASH_PRECISION
     * @throws IllegalArgumentException if coordinates are null or don't contain
     *                                  two elements, or if latitude/longitude are
     *                                  not valid
     */
    public String coordinatesToGeohash(List<Double> coordinates) {
        if (coordinates == null || coordinates.size() != 2) {
            throw new IllegalArgumentException("Coordinates must contain latitude and longitude");
        }

        double longitude = coordinates.get(0);
        double latitude = coordinates.get(1);

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }

        return GeoHash.withCharacterPrecision(latitude, longitude, GEOHASH_PRECISION).toBase32();
    }

    /**
     * Finds all geohashes that fall within a specified radius from a center point.
     * 
     * @param centerGeohash The geohash string representing the center point
     * @param radiusKm      The radius to search within, in kilometers
     * @return Set of geohash strings that cover the circular area
     * @throws IllegalArgumentException if centerGeohash is null, empty, or contains
     *                                  invalid characters or if radiusKm is
     *                                  negative
     */
    public Set<String> findGeohashesWithinRadius(String centerGeohash, int radiusKm) {
        if (centerGeohash == null || centerGeohash.isEmpty()) {
            throw new IllegalArgumentException("centerGeohash cannot be null or empty");
        }
        if (!centerGeohash.matches("^[0-9b-hjkmnp-z]+$")) {
            throw new IllegalArgumentException("centerGeohash contains invalid characters");
        }
        if (radiusKm <= 0) {
            throw new IllegalArgumentException("Proximity must be positive");
        }

        GeoHash centerHash = GeoHash.fromGeohashString(centerGeohash);
        WGS84Point centerPoint = centerHash.getOriginatingPoint();

        GeoHashCircleQuery query = new GeoHashCircleQuery(centerPoint, radiusKm * 1000);

        Set<String> geohashes = new HashSet<>();
        for (GeoHash hash : query.getSearchHashes()) {
            String hashString = hash.toBase32();
            // Ensure we only take the minimum of the actual length or the desired precision
            // This prevents the substring from throwing an exception when the hash is shorter than expected
            int precision = Math.min(hashString.length(), GEOHASH_PRECISION);
            geohashes.add(hashString.substring(0, precision));
        }

        return geohashes;
    }
}
