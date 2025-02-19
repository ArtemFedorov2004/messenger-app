import React from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import ChatPage from "./pages/chat/ChatPage";
import UserPage from "./pages/account/UserPage";
import {useKeycloak} from "@react-keycloak/web";
import Main from "./pages/Main";


function App() {
    const {keycloak, initialized} = useKeycloak();

    if (!keycloak) {
        return null;
    }

    if (keycloak.authenticated === false) {
        keycloak.login();
    }

    return (
        <BrowserRouter>
            <Routes>
                {/*<Route exact path="/" element={<Main/>}/>*/}
                <Route exact path="/" element={<ChatPage/>}/>
                <Route path="/user" element={<UserPage/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
