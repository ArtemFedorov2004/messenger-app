package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.controller.payload.RegistrationPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.TokensPayload;

public interface AuthenticationService {

    TokensPayload registration(RegistrationPayload payload);

    TokensPayload login(String username, String password);

    TokensPayload refresh(String refreshToken);
}
