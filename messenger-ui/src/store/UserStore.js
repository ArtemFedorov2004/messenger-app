import {makeAutoObservable} from "mobx";
import AuthService from "../service/AuthService";
import UserService from "../service/UserService";
import {jwtDecode} from "jwt-decode";

export default class UserStore {
    constructor() {
        this._isAuth = false;
        this._user = {}
        this._isSearchUsers = false;
        makeAutoObservable(this)
    }

    setIsAuth(bool) {
        this._isAuth = bool
    }

    setUser(user) {
        this._user = user
    }

    setIsSearchUsers(bool) {
        this._isSearchUsers = bool
    }

    get isAuth() {
        return this._isAuth;
    }

    get user() {
        return this._user;
    }

    get isSearchUsers() {
        return this._isSearchUsers;
    }

    async registration(username, email, password) {
        const response = await AuthService.registration(username, email, password);
        localStorage.setItem('token', response.data.accessToken);
        this.setIsAuth(true);
        await this.fetchUser(username);
    }

    async login(username, password) {
        const response = await AuthService.login(username, password);
        localStorage.setItem('token', response.data.accessToken);
        this.setIsAuth(true);
        await this.fetchUser(username);
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
            const accessToken = response.data.accessToken;
            localStorage.setItem('token', accessToken);
            this.setIsAuth(true);
            const username = jwtDecode(accessToken).sub;
            await this.fetchUser(username);
        } catch (e) {
            console.error(e);
        }
    }

    async fetchUser(username) {
        try {
            const response = await UserService.fetchUser(username);
            this.setUser(response.data);
        } catch (e) {
            console.error(e);
        }
    }
}