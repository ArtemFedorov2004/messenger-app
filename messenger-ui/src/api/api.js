import axios from 'axios';
import keycloak from "../keycloak/keycloak";

const api = () => {
    let token = keycloak.token;
    return axios.create({
        baseURL: 'http://localhost:8080',
        headers: {
            'Content-Type': 'application/json',
            Authorization: token ? `Bearer ${token}` : '',
        }
    });
}

export default api;