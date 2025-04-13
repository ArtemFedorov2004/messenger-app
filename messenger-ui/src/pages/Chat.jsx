import React, {useContext, useEffect} from 'react';
import {Layout, Row} from "antd";
import ChatLeftColumn from "../components/chat/ChatLeftColumn";
import ChatRightColumn from "../components/chat/ChatRightColumn";
import {Context} from "../index";
import ChatService from "../service/ChatService";
import {observer} from "mobx-react-lite";

const Chat = observer(() => {
    const {chat} = useContext(Context);

    useEffect(() => {
        const fetchChats = async () => {
            const data = await ChatService.getChats();

            chat.setChats(data);
        };

        fetchChats().then(() => {
            chat.setNewMessages(chat.chats.map((chat) =>
                ({
                    id: null,
                    chatId: chat.id,
                    content: ''
                })))
        });
    }, [chat]);

    return (
        <Layout className="h100">
            <Row>
                <ChatLeftColumn/>
                <ChatRightColumn/>
            </Row>
        </Layout>
    );
});

export default Chat;