import React, {useContext, useEffect, useState} from 'react';
import './App.css';
import {BrowserRouter as Router} from "react-router-dom";
import AppRouter from "./components/AppRouter";
import {observer} from "mobx-react-lite";
import {Context} from "./index";
import {Spin} from "antd";

const App = observer(() => {
    const {user} = useContext(Context);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (localStorage.getItem('token')) {
            setIsLoading(true);
            user.checkAuth()
                .finally(() => setIsLoading(false));
        }
    }, [user])

    if (isLoading) {
        return <Spin/>
    }

    return (
        <Router>
            <AppRouter/>
        </Router>
    );
});

export default App;