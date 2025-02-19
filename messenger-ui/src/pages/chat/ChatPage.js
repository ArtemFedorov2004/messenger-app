import React, {useEffect, useRef, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import Messages from "../../components/chat/ChatMessages/Messages";
import MessageInput from "../../components/chat/ChatMessages/MessageInput";
import SendButton from "../../components/chat/ChatMessages/SendButton";
import './ChatPage.css'
import {useKeycloak} from "@react-keycloak/web";
import ChatList from "../../components/chat/ChatLIst/ChatList";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const ChatPage = () => {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const [stompClient, setStompClient] = useState(null);
    const [connectedUsers, setConnectedUsers] = useState([]);

    const navigate = useNavigate();
    const {keycloak, initialized} = useKeycloak();

    const [selectedUser, setSelectedUser] = useState(null);
    const selectedUserRef = useRef(null);

    useEffect(() => {
        selectedUserRef.current = selectedUser;
    }, [selectedUser]);

    useEffect(() => {
        // КОСТЫЛЬ ИЗ ЗА АССИНХРОННОСТИ
        if (keycloak.authenticated) {
            // КОНКРЕНО В МОЕМ СЛУЧАЕ
            let nickname = keycloak.tokenParsed?.given_name;
            let fullname = keycloak.tokenParsed?.given_name;

            const socket = new SockJS(`http://localhost:8080/ws`);
            const client = Stomp.over(socket);

            client.connect({}, () => {
                setStompClient(client);
                client.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
                client.subscribe(`/user/public`, onUserConnect);

                client.send(`/app/user.addUser`, {}, JSON.stringify({
                    nickName: nickname,
                    fullName: fullname,
                    status: 'ONLINE'
                }));
                fetchConnectedUsers();
            });
        }

    }, [keycloak.token]);

    // ----
    const onUserConnect = (payload) => {
        const user = JSON.parse(payload.body);
        const updatedUser = {
            ...user,
            id: user.nickName,  // Добавляем новый ключ и его значение
            name: user.nickName,
            lastMessage: 'bablabal'
        }
        console.log(updatedUser)
        setConnectedUsers((prevUsers) => [...prevUsers, updatedUser]);
    }

    // ----
    const fetchConnectedUsers = async () => {
        const response = await fetch(`http://localhost:8080/users`);
        const users = await response.json();
        // МОЕ НОУ ХАУ
        const updatedUsers = users.map(user => {
            return {
                ...user,            // Распаковываем старые свойства объекта
                id: user.nickName,  // Добавляем новый ключ и его значение
                name: user.nickName,
                lastMessage: 'bablabal'
            };
        });
        console.log(updatedUsers)
        setConnectedUsers(updatedUsers.filter(user => user.nickName !== keycloak.tokenParsed?.given_name));
    };

    // ----
    const onMessageReceived = (payload) => {
        const message = JSON.parse(payload.body);
        const currentSelectedUser = selectedUserRef.current;
        if (currentSelectedUser && currentSelectedUser.nickName === message.senderId) {
            setMessages((prevMessages) => [...prevMessages, message]);
        }
    };

    const handleUserPage = () => {
        navigate('/user');
    };

    return (
        <div className="main">
            <div className="transition">
                <button
                    onClick={handleUserPage}
                    className="user-button"
                >
                    Учетные данные
                </button>
                <ChatList connectedUsers={connectedUsers} setMessages={setMessages} setSelectedUser={setSelectedUser}/>
            </div>

            <div className="chat-container">
                <Messages messages={messages}/>

                <div className="input-container">
                    <MessageInput
                        message={message}
                        setMessage={setMessage}
                    />
                    <SendButton message={message} setMessage={setMessage} messages={messages} setMessages={setMessages}
                                stompClient={stompClient} selectedUser={selectedUser}/>
                </div>
            </div>
        </div>
    );
};

export default ChatPage;

/*const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSubmit();
        }
    };*/

/*useEffect(() => {
    const fetchMessages = async () => {
        fetch('http://localhost:8080/messages', {
            method: 'GET',
            headers: {
                Authorization: keycloak.token ? `Bearer ${keycloak.token}` : '',
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                setMessages(data);
            })
            .catch(error => {
                console.error('Error fetching messages:', error);
            });
    };

    if (keycloak.token) {
        fetchMessages();
    }
}, [keycloak.token]);*/
