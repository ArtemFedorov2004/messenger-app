import React, {useContext} from 'react';
import {Button, Layout, Menu} from "antd";
import {useNavigate} from "react-router-dom";
import {CHAT_ROUTE, LOGIN_ROUTE} from "../utils/consts";
import {observer} from "mobx-react-lite";
import {Context} from "../index";

const Navbar = observer(() => {
    const {user, chat} = useContext(Context)
    const navigate = useNavigate();

    const logout = async () => {
        await user.logout()
        chat.resetStore()
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
                            <Button onClick={() => navigate(CHAT_ROUTE)}>Перейти в чат</Button>
                        </Menu.Item>
                        <Menu.Item
                            key={3}
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