package com.matchme.srv.exception;

// Might not need this, but example use case


// Match match = matchRepository.findById(matchId)
// .orElseThrow(() -> new ResourceNotFoundException(
//     "Match"
// ));

// "error": "Match not found"


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource) {
        super(resource + " not found");
    }
}
