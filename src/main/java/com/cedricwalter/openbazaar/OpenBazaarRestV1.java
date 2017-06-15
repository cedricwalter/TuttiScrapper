package com.cedricwalter.openbazaar;

import com.cedricwalter.openbazaar.bo.Profile;

import java.util.Optional;

public interface OpenBazaarRestV1 {

    Profile getProfile(Optional<String> guid);
}
