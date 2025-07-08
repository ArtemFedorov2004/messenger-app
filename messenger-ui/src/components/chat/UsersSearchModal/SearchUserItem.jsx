import React, {useContext} from 'react';
import {Avatar, List} from "antd";
import userLogo from "../../../assets/user.png";
import {observer} from "mobx-react-lite";
import {Context} from "../../../index";
import './SearchUserItem.css';

const SearchUserItem = observer(({userItem}) => {
    const {user, chat} = useContext(Context);

    const handleUserClick = () => {
        const chatWithUser = chat.findChatWithUser(userItem.username);

        if (chatWithUser) {
            chat.selectChat(chatWithUser);
        } else {
            const newChat = {
                id: null,
                participantName: userItem.username,
                newMessage: { content: '' },
                lastMessage: { content: '' }
            };

            chat.setChats([...chat.chats, newChat]);
            chat.selectChat(newChat);
        }

        user.setIsSearchUsers(false);
    };

    return (
        <List.Item
            onClick={handleUserClick}
            className={"search-user-item"}
        >
            <List.Item.Meta
                avatar={<Avatar src={userLogo}/>}
                title={userItem.username}
                description={userItem.email}
            />
        </List.Item>
    );
});

export default SearchUserItem;