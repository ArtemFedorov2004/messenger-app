import React, {useContext, useEffect} from 'react';
import {Layout, Row} from "antd";
import ChatLeftColumn from "../components/chat/ChatLeftColumn/ChatLeftColumn";
import {Context} from "../index";
import PrivateChatService from "../service/PrivateChatService";
import {observer} from "mobx-react-lite";
import StompClient from "../ws";
import UsersSearchModal from "../components/chat/UsersSearchModal/UsersSearchModal";
import NewPrivateChat from "../components/chat/ChatRightColumn/NewPrivateChat";
import ExistingPrivateChat from "../components/chat/ChatRightColumn/ExistingPrivateChat";

const Chat = observer(() => {
    const {chat, user} = useContext(Context);

    useEffect(() => {
        const fetchChatsAndConnect = async () => {
            const accessToken = localStorage.getItem('token');
            const username = user.user.username;
            const client = new StompClient(accessToken, username, chat);

            try {
                if (username) {
                    const response = await PrivateChatService.getPrivateChats();
                    const preparedChats = response.data.map(chat => ({
                        ...chat,
                        newMessage: {content: ''}
                    }));

                    chat.setChats(preparedChats);
                    client.connect();
                }
            } catch (error) {
                console.error(error);
            }

            return () => {
                client.disconnect();
            };
        };

        fetchChatsAndConnect();
    }, [chat, user.user]);

    return (
        <Layout className="h100">
            <Row>
                <ChatLeftColumn/>
                <UsersSearchModal
                    open={user.isSearchUsers}
                    onCancel={() => user.setIsSearchUsers(false)}
                />

                {chat.isChatSelected === true && (
                    chat.selectedChat.id
                        ? <ExistingPrivateChat key={chat.selectedChat.id}/>
                        : <NewPrivateChat key={chat.selectedChat.participantName}/>
                )}
            </Row>
        </Layout>
    );
});

export default Chat;