package com.matchme.srv.model.user.profile.user_attributes;

import com.matchme.srv.model.user.profile.ProfileChange;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;
import ch.hsr.geohash.GeoHash;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "user_attributes")
@ToString(exclude = "userProfile")
public class UserAttributes {

  @Id private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserProfile userProfile;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gender_id")
  @NotNull(message = "Gender is required")
  private UserGenderType gender;

  @NotNull(message = "Birth date is required")
  @PastOrPresent(message = "Birth date must be in the past or present")
  private LocalDate birth_date;

  @NotNull(message = "Location is required")
  @Size(min = 2, max = 2, message = "Location must contain exactly 2 coordinates (latitude and longitude)")
  private List<Double> location = new ArrayList<>(); // Geohash of 6-7 length
  
  @Column(name = "location_geohash")
  @Size(min = 5, message = "Geohash must be at least 5 characters long")
  private String locationGeohash; // Stored geohash for efficient querying

  @OneToMany(mappedBy = "userAttributes", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ProfileChange> attributeChangeLog;
  
  /**
   * Custom setter for location that also updates the locationGeohash field
   * @param location List containing latitude and longitude
   */
  public void setLocation(List<Double> location) {
    this.location = location;
    
    // Update the geohash if location is valid
    if (location != null && location.size() >= 2) {
      try {
        double latitude = location.get(0);
        double longitude = location.get(1);
        
        // Validate coordinates
        if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
          // Use precision 7 for geohash (approximately 150m x 150m precision)
          this.locationGeohash = ch.hsr.geohash.GeoHash.geoHashStringWithCharacterPrecision(
              latitude, longitude, 7);
        }
      } catch (Exception e) {
        // Log the error but don't throw it
        System.err.println("Error updating location geohash: " + e.getMessage());
      }
    }
  }
}
