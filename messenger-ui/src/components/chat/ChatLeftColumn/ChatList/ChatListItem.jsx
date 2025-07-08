import React from 'react';
import {Avatar, List} from "antd";
import userLogo from "../../../../assets/user.png";
import './ChatListItem.css';
import {observer} from "mobx-react-lite";

const ChatListItem = observer(({chat}) => {
    return (
        <List.Item.Meta
            avatar={<Avatar src={userLogo}/>}
            title={chat.participantName}
            description={
                <div className="last-message">
                    {chat.lastMessage?.content}
                </div>
            }
        />
    );
});

export default ChatListItem;