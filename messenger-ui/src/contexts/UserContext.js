import React, {createContext, useContext, useEffect, useState} from 'react';
import {useKeycloak} from "@react-keycloak/web";
import api from "../api/api";

const UserContext = createContext();

export const UserProvider = ({children}) => {
    const [user, setUser] = useState(null);

    const {keycloak, initialized} = useKeycloak();

    useEffect(() => {
        if (initialized) {
            if (keycloak.authenticated) {
                localStorage.setItem('access_token', keycloak.token);

                api.get("/login")
                    .then(response => response.data)
                    .then(user => setUser(user))
                    .catch(err => {
                        console.error('Error fetching user data:', err);
                    })

            } else {
                keycloak.login();
            }
        }
    }, [initialized, keycloak.authenticated]);

    const logout = () => {
        setUser(null);
        keycloak.logout();
    };

    return (
        <UserContext.Provider value={{user, setUser, logout}}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    return useContext(UserContext);
};