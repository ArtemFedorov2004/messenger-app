import React from 'react';
import {Layout, Button, Menu} from "antd";

const Navbar = () => {
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
                    <Button>Войти</Button>
                </Menu.Item>
            </Menu>
        </Layout.Header>
    );
};

export default Navbar;