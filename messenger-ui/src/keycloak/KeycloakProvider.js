import React, {useEffect, useState} from 'react';
import initKeycloak from './keycloak';

const KeycloakContext = React.createContext();

export const KeycloakProvider = ({children}) => {
    const [keycloak, setKeycloak] = useState(null);

    useEffect(() => {
        const keycloakInstance = initKeycloak((keycloak) => {
            setKeycloak(keycloak);
        });
    }, []);

    if (!keycloak) {
        return null;
    }

    return (
        <KeycloakContext.Provider value={keycloak}>
            {children}
        </KeycloakContext.Provider>
    );
};

export const useKeycloak = () => React.useContext(KeycloakContext);