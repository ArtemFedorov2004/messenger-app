import React, {createContext, useContext, useEffect, useState} from 'react';
import {useKeycloak} from "@react-keycloak/web";

const UserContext = createContext();

export const UserProvider = ({children}) => {
    const [user, setUser] = useState(null);

    const {keycloak, initialized} = useKeycloak();

    useEffect(() => {
        if (initialized) {
            if (keycloak.authenticated) {
                const fetchUserData = async () => {
                    const userData = keycloak.tokenParsed;
                    const user = {
                        id: userData?.sub,
                        email: userData?.email,
                        firstname: userData?.given_name,
                        lastname: userData?.family_name
                    };
                    setUser(user);
                };

                fetchUserData();
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
        <UserContext.Provider value={{user, logout}}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    return useContext(UserContext);
};