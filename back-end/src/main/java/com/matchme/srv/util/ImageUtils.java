package com.matchme.srv.util;

import java.util.Base64;

public class ImageUtils {

    /**
     * Converts a byte array representing an image to a Base64-encoded string with a data URI prefix.
     * Returns null if the input is null or empty.
     *
     * @param imageBytes The byte array of the image
     * @return A Base64-encoded string with "data:image/png;base64," prefix, or null if input is invalid
     */
    public static String toBase64Image(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            return null;
        }
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}