import React, {useContext} from 'react';
import {Navigate, Route, Routes} from "react-router-dom";
import {privateRoutes, publicRoutes} from "../routes";
import {HOME_ROUTE, LOGIN_ROUTE} from "../utils/consts";
import {observer} from "mobx-react-lite";
import {Context} from "../index";

const AppRouter = observer(() => {
    const {user} = useContext(Context);

    return (
        user.isAuth
            ?
            <Routes>
                {privateRoutes.map(({path, Component}) =>
                    <Route key={path} path={path} element={Component} exact/>
                )}
                <Route
                    path="*"
                    element={<Navigate to={HOME_ROUTE} replace/>}
                />
            </Routes>
            :
            <Routes>
                {publicRoutes.map(({path, Component}) =>
                    <Route key={path} path={path} element={Component} exact/>
                )}
                <Route
                    path="*"
                    element={<Navigate to={LOGIN_ROUTE} replace/>}
                />
            </Routes>
    );
});

export default AppRouter;