import React, {useContext, useEffect, useState} from 'react';
import {Layout, Row} from "antd";
import ChatLeftColumn from "../components/chat/ChatLeftColumn";
import ChatRightColumn from "../components/chat/ChatRightColumn";
import {Context} from "../index";
import ChatService from "../service/ChatService";
import {observer} from "mobx-react-lite";
import StompClient from "../ws";
import UsersSearchModal from "../components/chat/UsersSearchModal/UsersSearchModal";

const Chat = observer(() => {
    const {chat, user} = useContext(Context);

    useEffect(() => {
        const accessToken = localStorage.getItem('token');
        const username = user.user.username;
        const client = new StompClient(accessToken, username, chat);

        try {
            if (user.user.username) {
                ChatService.getChats()
                    .then(response => response.data)
                    .then(data => chat.setChats(data))
                    .then(() => {
                        client.connect()
                    });
            }

        } catch (error) {
            console.error(error);
        }


        return () => {
            client.disconnect();
        };
    }, [chat, user.user]);

    return (
        <Layout className="h100">
            <Row>
                <ChatLeftColumn/>
                <UsersSearchModal open={user.isSearchUsers} onCancel={() => user.setIsSearchUsers(false)}/>
                <ChatRightColumn/>
            </Row>
        </Layout>
    );
});

export default Chat;