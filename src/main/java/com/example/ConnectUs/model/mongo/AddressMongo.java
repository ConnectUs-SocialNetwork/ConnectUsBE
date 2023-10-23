package com.example.ConnectUs.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressMongo {
    private String country;
    private String city;
    private String street;
    private String number;
    private String postalNumber;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private LocationMongo location;
}
