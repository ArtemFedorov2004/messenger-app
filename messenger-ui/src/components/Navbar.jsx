import React from 'react';
import {Button, Layout, Menu} from "antd";
import {useNavigate} from "react-router-dom";
import {LOGIN_ROUTE} from "../utils/consts";

const Navbar = () => {
    const navigate = useNavigate();

    return (
        <Layout.Header>
            <Menu
                theme="dark"
                mode="horizontal"
                selectable={false}
                style={{justifyContent: 'end'}}
            >
                <Menu.Item
                    key={1}
                >
                    <Button onClick={() => navigate(LOGIN_ROUTE)}>Войти</Button>
                </Menu.Item>
            </Menu>
        </Layout.Header>
    );
};

export default Navbar;