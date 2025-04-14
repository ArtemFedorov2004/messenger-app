import {makeAutoObservable} from "mobx";
import AuthService from "../service/AuthService";
import UserService from "../service/UserService";

export default class UserStore {
    constructor() {
        this._isAuth = false;
        this._user = {}
        makeAutoObservable(this)
    }

    setIsAuth(bool) {
        this._isAuth = bool
    }

    setUser(user) {
        this._user = user
    }

    get isAuth() {
        return this._isAuth;
    }

    get user() {
        return this._user;
    }

    async registration(username, email, password) {
        const response = await AuthService.registration(username, email, password);
        localStorage.setItem('token', response.data.accessToken);
        this.setIsAuth(true);
        await this.fetchCurrentUser();
    }

    async login(username, password) {
        const response = await AuthService.login(username, password);
        localStorage.setItem('token', response.data.accessToken);
        this.setIsAuth(true);
        await this.fetchCurrentUser();
    }

    async logout() {
        try {
            await AuthService.logout();
            localStorage.removeItem('token');
            this.setIsAuth(false);
            this.setUser({});
        } catch (e) {
            console.error(e);
        }
    }

    async checkAuth() {
        try {
            const response = await AuthService.refresh();
            localStorage.setItem('token', response.data.accessToken);
            this.setIsAuth(true);
            await this.fetchCurrentUser();
        } catch (e) {
            console.error(e);
        }
    }

    async fetchCurrentUser() {
        try {
            const response = await UserService.fetchCurrentUser();
            this.setUser(response.data);
        } catch (e) {
            console.error(e);
        }
    }
}