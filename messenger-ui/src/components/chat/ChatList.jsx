import React, {useContext, useMemo} from 'react';
import './ChatList.css';
import {List} from "antd";
import {Context} from "../../index";
import {observer} from "mobx-react-lite";
import ChatListItem from "./ChatListItem";

const ChatList = observer(() => {
    const {chat} = useContext(Context);

    const selectedChatId = useMemo(() => {
        return chat.selectedChatId;
    }, [chat.selectedChatId]);

    const selectChat = (chatId) => {
        chat.setSelectedChatId(chatId);
    }

    return (
        <List
            className="chat-list"
            dataSource={chat.chats}
            renderItem={chat => (
                <List.Item
                    key={chat.id}
                    onClick={() => selectChat(chat.id)}
                    className={`chat-list-item ${chat.id === selectedChatId ? "chat-list-item__selected" : ""}`}
                >
                    <ChatListItem chat={chat}/>
                </List.Item>
            )}
        />
    );
});

export default ChatList;