import React from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import ChatPage from "./pages/chat/ChatPage";
import AccountPage from "./pages/account/AccountPage";
import {useUser} from "./contexts/UserContext";


function App() {
    const {user} = useUser();

    /*if (user == null) {
        return null;
    }
*/
    return (
        <BrowserRouter>
            <Routes>
                <Route exact path="/" element={<ChatPage/>}/>
                <Route path="/me" element={<AccountPage/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
