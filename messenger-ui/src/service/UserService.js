import {$authApi} from "../http";

export default class UserService {

    static async fetchUser(username) {
        return $authApi.get(`/users/${username}`)
    }

    static async searchUsers(query, page = 0, size = 5) {
        return $authApi.get('/users', {
            params: {query, page, size}
        });
    }
}