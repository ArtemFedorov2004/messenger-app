import axios from "axios";

export default class UserService {
    static async getUsers() {
        return new Promise((resolve) => {
            setTimeout(async () => {
                const response = await axios.get('./users.json');
                resolve(response);
            }, 1000);
        });
    }
}