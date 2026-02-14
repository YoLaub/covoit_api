package fr.cda.covoit_api.mapper;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.dto.request.RouteRequest;
import fr.cda.covoit_api.dto.request.VehicleRequest;
import fr.cda.covoit_api.dto.response.*;
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
        if (route.getIcon() != null) {
            res.setIconLabel(route.getIcon().getLabel());
        }
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

    public Vehicle toVehicle(VehicleRequest dto, Model model) {
        if (dto == null) return null;
        Vehicle vehicle = new Vehicle();
        vehicle.setSeats(dto.getSeats());
        vehicle.setCarregistration(dto.getCarregistration());
        vehicle.setAdditionalInfo(dto.getAdditionalInfo());
        vehicle.setModel(model);
        return vehicle;
    }

    public VehicleResponse toVehicleResponse(Vehicle vehicle) {
        if (vehicle == null) return null;
        VehicleResponse res = new VehicleResponse();
        res.setId(vehicle.getId());
        res.setSeats(vehicle.getSeats());
        res.setCarregistration(vehicle.getCarregistration());
        res.setAdditionalInfo(vehicle.getAdditionalInfo());

        if (vehicle.getModel() != null) {
            res.setModelName(vehicle.getModel().getLabel());
            if (vehicle.getModel().getBrand() != null) {
                res.setBrandName(vehicle.getModel().getBrand().getLabel());
            }
        }
        return res;
    }

    public BrandResponse toBrandResponse(Brand brand) {
        if (brand == null) return null;
        return new BrandResponse(brand.getId(), brand.getLabel());
    }

    public ReservationResponse toReservationResponse(UserRoute ur, Location start, Location end) {
        if (ur == null) return null;
        ReservationResponse res = new ReservationResponse();
        res.setRouteId(ur.getRoute().getId());
        res.setStatus(ur.getStatus());
        res.setCreatedAt(ur.getCreatedAt());

        if (ur.getRoute() != null) {
            res.setTripDate(ur.getRoute().getDate().toString() + " " + ur.getRoute().getHour().toString());
            res.setDriverName(ur.getRoute().getDriver().getFirstname() + " " + ur.getRoute().getDriver().getLastname());
        }

        if (start != null) res.setDepartureCity(start.getCityName());
        if (end != null) res.setArrivalCity(end.getCityName());

        return res;
    }
}