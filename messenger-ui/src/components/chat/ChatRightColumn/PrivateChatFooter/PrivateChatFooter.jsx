import React, {useContext} from 'react';
import {Button, Input} from "antd";
import {SendOutlined} from "@ant-design/icons";
import {observer} from "mobx-react-lite";
import {Context} from "../../../../index";
import './PrivateChatFooter.css'

const PrivateChatFooter = observer(({onSend}) => {
    const {chat} = useContext(Context);

    const onChange = (e) => {
        const newMessage = {
            ...chat.selectedChat.newMessage,
            content: e.target.value
        }
        chat.updateNewMessage(chat.selectedChat.participantName, newMessage)
    }

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            onSend();
        }
    };

    return (
        <div className="right-column-footer">
            <Input
                className="input"
                placeholder={"Сообщение..."}
                onKeyDown={handleKeyPress}
                value={chat.selectedChat.newMessage.content}
                onChange={onChange}
            ></Input>
            <Button className="send-button" onClick={onSend}>
                <SendOutlined/>
            </Button>
        </div>
    );
});

export default PrivateChatFooter;