export default class UserService {
    static users = [
        {
            id: '1',
            username: "Artem",
            email: "a@com",
            password: "q"
        },
        {
            id: '2',
            username: "Misha",
            email: "m@com",
            password: "q"
        }
    ]

    static async getUsers() {
        return new Promise((resolve) => {
            setTimeout(async () => {
                resolve(UserService.users);
            }, 1000);
        });
    }
}