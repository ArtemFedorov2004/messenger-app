import React, {useMemo} from 'react';
import {Avatar, List} from "antd";
import userLogo from "../../assets/user.png";
import './ChatListItem.css';

const ChatListItem = ({chat}) => {
    const lastMessage = useMemo(() => {
        if (chat.messages.length === 0) return {};

        return chat.messages.reduce((last, message) => {
            return message.id > last.id ? message : last;
        });
    }, [chat.messages]);

    return (
        <List.Item.Meta
            avatar={<Avatar src={userLogo}/>}
            title={chat.title}
            description={
                <div className="last-message">
                    {lastMessage.content}
                </div>
            }
        />
    );
};

export default ChatListItem;