import React, {useContext, useMemo} from 'react';
import {List} from "antd";
import {observer} from "mobx-react-lite";
import {Context} from "../../../../index";
import './ChatList.css';
import ChatListItem from "./ChatListItem";

const ChatList = observer(() => {
    const {chat} = useContext(Context);

    const selectedChatId = useMemo(() => {
        return chat.selectedChat.id;
    }, [chat.selectedChat.id]);

    return (
        <List
            className="chat-list"
            dataSource={chat.chats.filter(c => c.id !== null)}
            renderItem={c => (
                <List.Item
                    key={c.id}
                    onClick={() => chat.selectChat(c)}
                    className={`chat-list-item ${c.id === selectedChatId ? "chat-list-item__selected" : ""}`}
                >
                    <ChatListItem chat={c}/>
                </List.Item>
            )}
        />
    );
});

export default ChatList;