import {CHAT_ROUTE, HOME_ROUTE, LOGIN_ROUTE, PROFILE_ROUTE, REGISTRATION_ROUTE} from "./utils/consts";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Registration from "./pages/Registration";
import Chat from "./pages/Chat";
import Profile from "./pages/Profile";

export const publicRoutes = [
    {
        path: HOME_ROUTE,
        Component: <Home/>
    },
    {
        path: LOGIN_ROUTE,
        Component: <Login/>
    },
    {
        path: REGISTRATION_ROUTE,
        Component: <Registration/>
    }
]

export const privateRoutes = [
    {
        path: HOME_ROUTE,
        Component: <Home/>
    },
    {
        path: CHAT_ROUTE,
        Component: <Chat/>
    },
    {
        path: PROFILE_ROUTE,
        Component: <Profile/>
    }
]