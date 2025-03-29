import React, {useContext} from 'react';
import {Button, Layout, Menu} from "antd";
import {useNavigate} from "react-router-dom";
import {LOGIN_ROUTE} from "../utils/consts";
import {observer} from "mobx-react-lite";
import {Context} from "../index";

const Navbar = observer(() => {
    const {user} = useContext(Context)
    const navigate = useNavigate();

    const logout = () => {
        user.setUser({})
        user.setIsAuth(false)
    }

    return (
        <Layout.Header>
            <Menu
                theme="dark"
                mode="horizontal"
                selectable={false}
                style={{justifyContent: 'end'}}
            >
                {user.isAuth ?
                    <>
                        <Menu.Item
                            key={1}
                        >
                            Добро пожаловать, {user.user.username}
                        </Menu.Item>
                        <Menu.Item
                            key={2}
                        >
                            <Button onClick={() => logout()}>Выйти</Button>
                        </Menu.Item>
                    </>
                    :
                    <Menu.Item
                        key={1}
                    >
                        <Button onClick={() => navigate(LOGIN_ROUTE)}>Войти</Button>
                    </Menu.Item>
                }
            </Menu>
        </Layout.Header>
    );
});

export default Navbar;