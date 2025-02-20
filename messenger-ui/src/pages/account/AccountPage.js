import React from 'react';
import {useNavigate} from 'react-router-dom';
import {useKeycloak} from "@react-keycloak/web";
import './AccountPage.css';

const AccountPage = () => {
    const {keycloak, authenticated} = useKeycloak();
    const navigate = useNavigate();

    const userData = keycloak.tokenParsed;

    const handleBackToChat = () => {
        navigate('/');
    };

    const handleLogout = () => {
        keycloak.logout();
    };

    return (
        <div className="user-container">
            <h1>Информация о пользователе</h1>
            <div className="user-info">
                <p><strong>ID пользователя:</strong> {userData?.sub}</p>
                <p><strong>Email:</strong> {userData?.email}</p>
                <p><strong>Имя:</strong> {userData?.given_name}</p>
                <p><strong>Фамилия:</strong> {userData?.family_name}</p>
            </div>
            <div className="button-container">
                <button onClick={handleBackToChat}>Вернуться в чат</button>
                <button onClick={handleLogout} className="logout-button">Выйти</button>
            </div>
        </div>
    );
};

export default AccountPage;