package io.github.artemfedorov2004.messengerserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService extends JwtService {

    @Value("${refresh-token.signing-key}")
    private String signingKey;

    @Value("${refresh-token.ttl}")
    private int ttl;

    @Override
    public int getTtl() {
        return this.ttl;
    }

    @Override
    public String getSigningKey() {
        return this.signingKey;
    }
}
