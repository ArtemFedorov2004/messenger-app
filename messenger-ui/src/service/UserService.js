import {$authApi} from "../http";

export default class UserService {

    static async fetchCurrentUser() {
        return $authApi.get("/user")
    }
}