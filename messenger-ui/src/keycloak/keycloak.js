import Keycloak from 'keycloak-js'

const initKeycloak = (onAuthenticatedCallback) => {
    const keycloak = new Keycloak({
        url: "http://localhost:9090/",
        realm: "messenger",
        clientId: "messenger-client",
    });

    keycloak.init({onLoad: "login-required"})
        .then(authenticated => {
            if (authenticated) {
                onAuthenticatedCallback(keycloak);
            } else {
                window.location.reload();
            }
        })
        .catch(console.error);

    return keycloak;
};

export default initKeycloak;

