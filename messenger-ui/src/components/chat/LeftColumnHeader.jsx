import React from 'react';
import {Button, Dropdown} from "antd";
import {MenuOutlined, SearchOutlined} from "@ant-design/icons";
import './LeftColumnHeader.css'

const menuItems = [
    {
        label: "Аккаунт",
        key: '0',
    },
];

const LeftColumnHeader = () => {


    return (
        <div className="left-column-header">
            <Dropdown
                menu={{items: menuItems}}
                trigger={['click']}
            >
                <Button>
                    <MenuOutlined/>
                </Button>
            </Dropdown>
            <Button className="search">
                Поиск <SearchOutlined/>
            </Button>
        </div>
    );
};

export default LeftColumnHeader;