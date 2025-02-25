import React from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import ChatPage from "./pages/chat/ChatPage";
import {useUser} from "./contexts/UserContext";
import ProfilePage from "./pages/profile/ProfilePage";


function App() {
    const {user} = useUser();

    if (user == null) {
        return null;
    }

    return (
        <BrowserRouter>
            <Routes>
                <Route exact path="/" element={<ChatPage/>}/>
                <Route path="/me" element={<ProfilePage/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
