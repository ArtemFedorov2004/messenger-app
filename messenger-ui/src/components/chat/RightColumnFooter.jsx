import React, {useContext, useMemo} from 'react';
import {Button, Input} from "antd";
import {SendOutlined} from "@ant-design/icons";
import './RightColumnFooter.css'
import {Context} from "../../index";
import {observer} from "mobx-react-lite";
import ChatService from "../../service/ChatService";

const RightColumnFooter = observer(() => {
    const {chat} = useContext(Context);

    const selectedChatId = useMemo(() => {
        return chat.chats
            .find((c) => c.id === chat.selectedChatId)
            .id;
    }, [chat.selectedChatId, chat.chats])

    const newMessage = useMemo(() => {
        return chat.newMessages.find((m) => m.chatId === selectedChatId);
    }, [chat.newMessages, selectedChatId])

    const onChange = (e) => {
        chat.updateNewMessage({...newMessage, content: e.target.value})
    }

    const createMessage = async () => {
        const data = await ChatService.createMessage(selectedChatId, newMessage.content);
        chat.addMessage(data);
    }

    const editMessage = async () => {
        const data = await ChatService.editMessage(selectedChatId, newMessage.id, newMessage.content);
        chat.editMessage(data)
    }

    const sendMessage = async () => {
        if (newMessage.id === null) {
            await createMessage()
        } else {
            await editMessage()
        }

        chat.updateNewMessage({
            id: null,
            chatId: selectedChatId,
            content: ''
        })
    }

    return (
        <div className="right-column-footer">
            <Input
                className="input"
                placeholder={"Сообщение..."}
                value={newMessage.content}
                onChange={onChange}
            ></Input>
            <Button className="send-button" onClick={sendMessage}>
                <SendOutlined/>
            </Button>
        </div>
    );
});

export default RightColumnFooter;