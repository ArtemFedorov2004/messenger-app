import React, {useContext, useMemo} from 'react';
import {Avatar} from "antd";
import userLogo from "../../assets/user.png";
import './RightColumnHeader.css';
import {Context} from "../../index";
import {observer} from "mobx-react-lite";

const RightColumnHeader = observer(() => {
    const {chat} = useContext(Context);

    const selectedChat = useMemo(() => {
        return chat.chats.find((c) => c.id === chat.selectedChatId);
    }, [chat.selectedChatId, chat.chats])

    return (
        <div className="right-column-header">
            <Avatar src={userLogo}/>
            <div className="info">
                <h3 className="username">{selectedChat.title}</h3>
                <span className="user-status">Онлайн</span>
            </div>
        </div>
    );
});

export default RightColumnHeader;