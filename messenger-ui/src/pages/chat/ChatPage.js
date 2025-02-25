import React, {useCallback, useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import Messages from "../../components/chat/ChatMessages/Messages";
import './ChatPage.css'
import ChatList from "../../components/chat/ChatLIst/ChatList";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import {useUser} from "../../contexts/UserContext";
import {useKeycloak} from "@react-keycloak/web";
import api from "../../api/api";

const ChatPage = () => {
    const [chats, setChats] = useState(new Map());
    const [selectedChatId, setSelectedChatId] = useState('');
    const [newMessages, setNewMessages] = useState(new Map());
    const [stompClient, setStompClient] = useState(null);

    const navigate = useNavigate();

    const {user} = useUser();

    const {keycloak} = useKeycloak();

    const selectedChat = chats.get(selectedChatId);

    const connectSocket = useCallback(() => {
        const socket = new SockJS(`http://localhost:8080/ws`);
        const client = Stomp.over(socket);

        const headers = {
            Authorization: `Bearer ${keycloak.token}`,
        };

        client.connect(headers, () => {
            setStompClient(client);
        });

        return client;
    }, [user]);

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

    const handleChange = useCallback((e) => {
        setNewMessages((prev) => {
            const updatedMessages = new Map(prev);

            const newMessage = updatedMessages.get(selectedChatId);

            newMessage.content = e.target.value;

            return updatedMessages;
        });
    }, [selectedChatId]);

    const addMessage = (chatId, message) => {
        setChats((prev) => {
            const chatsToUpdate = new Map(prev);
            const messagesToUpdate = chatsToUpdate.get(chatId);
            messagesToUpdate.messages = [...messagesToUpdate.messages, message];

            chatsToUpdate.set(chatId, messagesToUpdate);

            return chatsToUpdate;
        });
    };

    const clearNewMessage = (chatId) => {
        setNewMessages((prev) => {
            const updatedMessages = new Map(prev);

            const clearedNewMessage = {
                chatId: chatId,
                content: ''
            };

            updatedMessages.set(chatId, clearedNewMessage);

            return updatedMessages;
        });
    };


    const handleSendMessage = useCallback(
        (e) => {
            e.preventDefault();
            const content = newMessages.get(selectedChatId).content.trim();
            if (stompClient && content) {
                const messageToSend = {
                    chatId: selectedChatId,
                    senderId: user.id,
                    content: content,
                    createdAt: new Date(),
                    isRead: false
                }
                stompClient.send(`/app/chats/${selectedChatId}/messages`, {}, JSON.stringify(messageToSend));
                addMessage(selectedChatId, messageToSend)
                clearNewMessage(selectedChatId);
            }
        }, [selectedChatId, newMessages, stompClient, user.id]);

    const handleKeyDown = useCallback(
        (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                handleSendMessage(e);
            }
        }, [handleSendMessage]);

    const fetchUserChats = async () => {
        api.get("/chats")
            .then((response) => response.data)
            .then(data => {
                const chats = new Map(
                    data.map((chat) => {
                        const {id, ...chatWithoutId} = chat;

                        return [chat.id, chatWithoutId];
                    })
                );
                setChats(chats);
            })
            .catch((error) => {
                console.error("Failed to fetch connected users", error);
            });
    };

    const initNewMessages = () => {
        const newMessages = new Map();
        chats.forEach((_, chatId) => {
            newMessages.set(chatId, {
                content: ''
            });
        });
        setNewMessages(newMessages);
    }

    useEffect(() => {
        fetchUserChats()
            .catch(error => console.error(error))

    }, [user]);

    useEffect(() => {
        initNewMessages();
    }, [chats]);

    return (
        <div className="main-container">
            <div className="left-container">
                <div className="header">
                    <div className="user-img">
                        <img src="https://www.codewithfaraz.com/InstaPic.png" alt=""/>
                    </div>
                </div>
                <ChatList
                    chats={chats}
                    setSelectedChatId={setSelectedChatId}
                />
            </div>

            <div className="right-container">
                {selectedChat ? (
                    <>
                        <div className="header">
                            <div className="img-text">
                                <div className="user-img">
                                    <img
                                        src="https://images.pexels.com/photos/2474307/pexels-photo-2474307.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
                                        alt=""/>
                                </div>
                                <h4>Leo<br/><span>Online</span></h4>
                            </div>
                        </div>
                        <div className="chat-container">
                            <Messages messages={selectedChat.messages}/>
                        </div>
                        <div className="chatbox-input">
                            <input
                                type="text"
                                placeholder="Type a message"
                                value={newMessages.get(selectedChatId).content}
                                onChange={handleChange}
                                onKeyDown={handleKeyDown}
                            />
                            <button
                                className="send-button"
                                onClick={handleSendMessage}
                            >
                                Отправить
                            </button>
                        </div>
                    </>
                ) : null}
            </div>
        </div>
    );
};

export default ChatPage;