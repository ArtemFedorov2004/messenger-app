import React, {useContext} from 'react';
import {Button, Dropdown} from "antd";
import {MenuOutlined, SearchOutlined} from "@ant-design/icons";
import './LeftColumnHeader.css'
import {Context} from "../../index";
import {observer} from "mobx-react-lite";

const LeftColumnHeader = observer(() => {
    const {user} = useContext(Context);

    const menuItems = [
        {
            label: "Аккаунт",
            key: '0',
        },
        {
            label: 'Выйти',
            key: '1',
            onClick: async () => {
                try {
                    await user.logout()
                } catch (e) {
                    console.error(e.response.data)
                }
            },
        }
    ];

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
});

export default LeftColumnHeader;