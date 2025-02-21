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
        <div className="main-container">
            <div className="left-container">
                <div className="header">
                    <div className="user-img">
                        <img src="https://www.codewithfaraz.com/InstaPic.png" alt=""/>
                    </div>
                </div>
                <ChatList
                    connectedUsers={connectedUsers}
                    setMessages={setMessages}
                    setSelectedUser={setSelectedUser}
                />
            </div>

            <div className="right-container">
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
                    <Messages messages={messages}/>
                </div>
                <div className="chatbox-input">
                    <input
                        type="text"
                        placeholder="Type a message"
                        value={message}
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
            </div>
        </div>
    );
};

export default ChatPage;