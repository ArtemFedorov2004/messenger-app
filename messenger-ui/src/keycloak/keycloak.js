import Keycloak from 'keycloak-js'

const keycloak = new Keycloak({
    url: "http://localhost:9090/",
    realm: "messenger",
    clientId: "messenger-client",
});

export default keycloak;

