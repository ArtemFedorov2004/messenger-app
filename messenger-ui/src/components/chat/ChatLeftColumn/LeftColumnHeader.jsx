import React, {useContext} from 'react';
import {Button, Dropdown} from "antd";
import {MenuOutlined, SearchOutlined} from "@ant-design/icons";
import './LeftColumnHeader.css'
import {Context} from "../../../index";
import {observer} from "mobx-react-lite";
import {useNavigate} from "react-router-dom";
import {PROFILE_ROUTE} from "../../../utils/consts";

const LeftColumnHeader = observer(() => {
    const {user, chat} = useContext(Context);
    const navigate = useNavigate();

    const menuItems = [
        {
            label: "Аккаунт",
            key: '0',
            onClick: () => navigate(PROFILE_ROUTE)
        },
        {
            label: 'Выйти',
            key: '1',
            onClick: async () => {
                try {
                    await user.logout()
                    chat.resetStore()
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
            <Button className="search" onClick={() => user.setIsSearchUsers(true)}>
                Поиск <SearchOutlined/>
            </Button>
        </div>
    );
});

export default LeftColumnHeader;