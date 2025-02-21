import React, {useCallback, useEffect, useRef, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import Messages from "../../components/chat/ChatMessages/Messages";
import './ChatPage.css'
import ChatList from "../../components/chat/ChatLIst/ChatList";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import {useUser} from "../../contexts/UserContext";
import {useKeycloak} from "@react-keycloak/web";

const ChatPage = () => {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const [stompClient, setStompClient] = useState(null);
    const [connectedUsers, setConnectedUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);

    const navigate = useNavigate();

    const selectedUserRef = useRef(null);

    const {user} = useUser();

    useEffect(() => {
        selectedUserRef.current = selectedUser;
    }, [selectedUser]);

    const {keycloak} = useKeycloak();

    const headers = {
        Authorization: `Bearer ${keycloak.token}`,
    };


    const connectSocket = useCallback(() => {
        const socket = new SockJS(`http://localhost:8080/ws`);
        const client = Stomp.over(socket);

        client.connect(headers, () => {
            setStompClient(client);
            client.subscribe(`/user/${user.id}/queue/messages`, onMessageReceived);
            client.subscribe(`/user/public`, onUserConnect);

            client.send(`/app/user.addUser`, {}, JSON.stringify(user));

            fetchConnectedUsers();
        });

        return client;
    }, [user]);

    useEffect(() => {
        const client = connectSocket();

        return () => {
            if (client && client.connected) {
                fetch(`http://localhost:8080/users`);
                client.disconnect();
            }
        };
    }, [connectSocket]);

    const onUserConnect = (payload) => {
        const newUser = JSON.parse(payload.body);
        setConnectedUsers((prevUsers) => [
            ...prevUsers,
            {...newUser, lastMessage: 'bablabal'}
        ]);
    }

    const fetchConnectedUsers = async () => {
        fetch(`http://localhost:8080/users`)
            .then((response) => response.json())
            .then((users) => {
                const filteredUsers = users
                    .filter((u) => u.id !== user.id)
                    .map((u) => ({...u, lastMessage: 'bablabal'}));
                setConnectedUsers(filteredUsers);
            })
            .catch((error) => {
                console.error("Failed to fetch connected users", error);
            });
    };

    const onMessageReceived = (payload) => {
        const receivedMessage = JSON.parse(payload.body);
        const currentSelectedUser = selectedUserRef.current;

        if (currentSelectedUser && currentSelectedUser.id === receivedMessage.senderId) {
            setMessages((prevMessages) => [...prevMessages, receivedMessage]);
        }
    };

    const handleAccountPage = useCallback(() => {
        navigate('/me');
    }, [navigate]);

    const handleChange = useCallback((e) => {
        setMessage(e.target.value);
    }, []);

    const handleSendMessage = useCallback(
        (e) => {
            e.preventDefault();
            if (stompClient && message.trim()) {
                const messageToSend = {
                    senderId: user.id,
                    recipientId: selectedUser.id,
                    content: message.trim(),
                    timestamp: new Date(),
                };
                stompClient.send(`/app/chat`, {}, JSON.stringify(messageToSend));
                setMessages((prevMessages) => [
                    ...prevMessages,
                    {senderId: user.id, content: messageToSend.content},
                ]);
                setMessage('');
            }
        }, [stompClient, message, user, selectedUser]);

    const handleKeyDown = useCallback(
        (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                handleSendMessage(e);
            }
        }, [handleSendMessage]);

    return (
        <div className="main">
            <div className="transition">
                <button onClick={handleAccountPage} className="user-button">
                    Учетные данные
                </button>
                <ChatList
                    connectedUsers={connectedUsers}
                    setMessages={setMessages}
                    setSelectedUser={setSelectedUser}
                />
            </div>

            <div className="chat-container">
                <Messages messages={messages}/>

                <div className="input-container">
                    <input
                        type="text"
                        value={message}
                        onChange={handleChange}
                        onKeyDown={handleKeyDown}
                        className="message-input"
                    />
                    <button onClick={handleSendMessage} className="send-button">
                        Отправить сообщение
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ChatPage;