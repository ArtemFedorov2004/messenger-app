import Keycloak from 'keycloak-js'
import api from "../api/api";

const initKeycloak = (onAuthenticatedCallback) => {
    const keycloak = new Keycloak({
        url: "http://localhost:9090/",
        realm: "messenger",
        clientId: "messenger-client",
    });

    keycloak.init({onLoad: "login-required"})
        .then(authenticated => {
            if (authenticated) {
                const token = keycloak.token;

                api.defaults.headers['Authorization'] = `Bearer ${token}`;

                onAuthenticatedCallback(keycloak);
            } else {
                window.location.reload();
            }
        })
        .catch(console.error);

    return keycloak;
};

export default initKeycloak;

