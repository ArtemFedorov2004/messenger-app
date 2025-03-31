import React, {useContext, useEffect, useMemo, useRef} from 'react';
import Message from "./Message";
import {observer} from "mobx-react-lite";
import {Context} from "../../index";
import './MessageList.css';

const MessageList = observer(() => {
    const {chat} = useContext(Context);
    const messageEndRef = useRef(null);

    const messages = useMemo(() => {
        return chat.chats
            .find((c) => c.id === chat.selectedChatId)
            .messages;
    }, [chat.selectedChatId, chat.chats])

    useEffect(() => {
        messageEndRef.current?.scrollIntoView({behavior: 'instant'});
    }, [messages])

    return (
        <div className="message-list">
            {messages.map(message => (
                <Message key={message.id} message={message}/>
            ))}

            <div ref={messageEndRef}/>
        </div>
    );
});

export default MessageList;