import {$authApi} from "../http";

export default class UserService {

    static async fetchUser(username) {
        return $authApi.get(`/users/${username}`)
    }
}