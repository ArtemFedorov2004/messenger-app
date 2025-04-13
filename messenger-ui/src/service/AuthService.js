import UserService from "./UserService";

export const registration = async (username, email, password) => {
    const response = await UserService.getUsers()
    const mockUser = response.data.find(user => user.username === username && user.password === password && user.email === email)
    return mockUser
}

export const login = async (username, password) => {
    const response = await UserService.getUsers();
    const mockUser = response.find(user => user.username === username && user.password === password);
    return mockUser;
}