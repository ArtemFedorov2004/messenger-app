import React, {useCallback, useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import './ChatPage.css'
import ChatList from "../../components/chat/ChatLIst/ChatList";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import {useUser} from "../../contexts/UserContext";
import {useKeycloak} from "@react-keycloak/web";
import Search from "../../components/chat/Search/Search";
import FriendChat from "../../components/chat/FriendChat/FriendChat";
import NewFriendChat from "../../components/chat/FriendChat/NewFriendChat";
import useFriendChats from "../../hooks/useFriendChats";


const ChatPage = () => {
    const {user} = useUser();

    const {
        friendChats,
        updateFriendChatInMap,
        updateNewMessageInChat,
        appendMessageToChat,
        clearNewMessage,
        markChatAsExisting
    } = useFriendChats(user);

    const [selectedChatId, setSelectedChatId] = useState('');
    const [stompClient, setStompClient] = useState(null);
    const [showSearchSection, setShowSearchSection] = useState(false);

    const navigate = useNavigate();

    const {keycloak} = useKeycloak();

    const connectSocket = useCallback(() => {
        const socket = new SockJS(`http://localhost:8080/ws`);
        const client = Stomp.over(socket);

        const headers = {
            Authorization: `Bearer ${keycloak.token}`,
        };

        client.connect(headers, () => {
            setStompClient(client);
            client.subscribe(`/user/${user.id}/queue/messages.newFriendMessage`, onMessageReceived);
            client.subscribe(`/user/${user.id}/queue/chats.newFriendChat`, onNewFriendChatNotification);
        });

        return client;
    }, [user]);

    const onMessageReceived = (payload) => {
        const receivedMessage = JSON.parse(payload.body);

        appendMessageToChat(receivedMessage.senderId, receivedMessage);
    }

    const onNewFriendChatNotification = (payload) => {
        const friendChatReceived = JSON.parse(payload.body);
        const friendChat = {
            isNew: false,
            ...friendChatReceived
        };

        updateFriendChatInMap(friendChat);
    }

    useEffect(() => {
        const client = connectSocket();

        return () => {
            if (client && client.connected) {
                client.disconnect();
            }
        };
    }, [connectSocket]);

    const handleAccountPage = useCallback(() => {
        navigate('/me');
    }, [navigate]);

    const handleMessageInputChange = useCallback((e) => {
        const {value} = e.target;
        updateNewMessageInChat(selectedChatId, value);
    }, [selectedChatId]);

    const selectFriendChatById = (friendId) => {
        setSelectedChatId(friendId);
    }

    return (
        <div className="main-container">
            <div className="left-container">
                <div className="header">
                    <div className="user-img"
                         onClick={handleAccountPage}
                    >
                        <img src="/logo512.png" alt="avatar"/>
                    </div>
                </div>
                <Search
                    friendChats={friendChats}
                    updateFriendChatInMap={updateFriendChatInMap}
                    selectFriendChatById={selectFriendChatById}
                    showSearchSection={showSearchSection}
                    setShowSearchSection={setShowSearchSection}
                />
                {!showSearchSection ? (
                    <ChatList
                        friendChats={friendChats}
                        selectPrivateChatById={selectFriendChatById}
                    />
                ) : null}
            </div>

            <div className="right-container">
                {(() => {
                    const selectedFriendChat = friendChats.get(selectedChatId);

                    if (!selectedFriendChat) {
                        return null;
                    }

                    if (selectedFriendChat.isNew) {
                        return (
                            <NewFriendChat
                                friendChat={selectedFriendChat}
                                markChatAsExisting={markChatAsExisting}
                                handleChange={handleMessageInputChange}
                                appendMessageToChat={appendMessageToChat}
                                clearNewMessage={clearNewMessage}
                            />
                        );
                    } else {
                        return (
                            <FriendChat
                                friendChat={selectedFriendChat}
                                appendMessageToChat={appendMessageToChat}
                                clearNewMessage={clearNewMessage}
                                handleChange={handleMessageInputChange}
                            />
                        );
                    }
                })()}
            </div>
        </div>
    );
};

export default ChatPage;