package io.github.artemfedorov2004.messengerserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenService extends JwtService {

    @Value("${access-token.signing-key}")
    private String signingKey;

    @Value("${access-token.ttl}")
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
