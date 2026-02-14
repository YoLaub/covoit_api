package fr.cda.covoit_api.mapper;

import fr.cda.covoit_api.domain.entity.Location;
import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.domain.entity.Route;
import fr.cda.covoit_api.dto.request.RouteRequest;
import fr.cda.covoit_api.dto.response.LocationResponse;
import fr.cda.covoit_api.dto.response.ProfilResponse;
import fr.cda.covoit_api.dto.response.RouteResponse;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public ProfilResponse toProfilResponse(Profil profil) {
        if (profil == null) return null;
        ProfilResponse res = new ProfilResponse();
        res.setId(profil.getId());
        res.setFirstname(profil.getFirstname());
        res.setLastname(profil.getLastname());
        res.setPhone(profil.getPhone());
        res.setEmail(profil.getUser().getEmail());
        return res;
    }

    public LocationResponse toLocationResponse(Location loc) {
        if (loc == null) return null;
        LocationResponse res = new LocationResponse();
        res.setStreetNumber(loc.getStreetNumber());
        res.setStreetName(loc.getStreetName());
        res.setPostalCode(loc.getPostalCode());
        res.setCityName(loc.getCityName());
        return res;
    }

    public RouteResponse toRouteResponse(Route route, Location start, Location end) {
        if (route == null) return null;
        RouteResponse res = new RouteResponse();
        res.setId(route.getId());
        res.setKms(route.getDistance());
        res.setAvailableSeats(route.getPlace());
        res.setDate(route.getDate());
        res.setHour(route.getHour());
        res.setDriverName(route.getDriver().getFirstname() + " " + route.getDriver().getLastname());
        res.setDeparture(toLocationResponse(start));
        res.setArrival(toLocationResponse(end));
        return res;
    }

    public Location toLocation(RouteRequest.AddressRequest address) {
        if (address == null) return null;
        Location loc = new Location();
        loc.setStreetNumber(address.getStreetNumber());
        loc.setStreetName(address.getStreetName());
        loc.setPostalCode(address.getPostalCode());
        loc.setCityName(address.getCity());
        loc.setLatitude(address.getLatitude());
        loc.setLongitude(address.getLongitude());
        return loc;
    }

    public Route toRoute(RouteRequest dto) {
        if (dto == null) return null;
        Route route = new Route();
        route.setPlace(dto.getAvailableSeats());
        route.setDate(dto.getTripDate());
        route.setHour(dto.getTripHour());
        route.setDistance(dto.getKms());
        return route;
    }
}