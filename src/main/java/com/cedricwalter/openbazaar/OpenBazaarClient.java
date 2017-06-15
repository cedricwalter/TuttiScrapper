package com.cedricwalter.openbazaar;

import com.cedricwalter.openbazaar.bo.Profile;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Created by cedric on 6/14/2017.
 */
public class OpenBazaarClient implements OpenBazaarRestV1{

    private String BASE_URL = "http://localhost:18469/api/v1/";

    public static void  main(String[] args) {
        OpenBazaarClient client = new OpenBazaarClient();
        Optional<String> guid = Optional.empty();

        client.getProfile(guid);
    }

    public Profile getProfile(Optional<String> guid) {
        Client client = ClientBuilder.newBuilder().newClient();
        WebTarget target = client.target(BASE_URL);
        if (guid.isPresent())
            target = target.path("profile").queryParam("guid", "guid");
        else
            target = target.path("profile");

        Invocation.Builder builder = target.request();
        Response response = builder.get();
        Profile profile = builder.get(Profile.class);

        return profile;
    }

}
