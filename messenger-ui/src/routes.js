import {HOME_ROUTE, LOGIN_ROUTE, REGISTRATION_ROUTE} from "./utils/consts";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Registration from "./pages/Registration";

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
    }
]