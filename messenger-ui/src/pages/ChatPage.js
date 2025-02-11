import React, {useEffect, useRef, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import Messages from "../components/chat/Messages";
import MessageInput from "../components/chat/MessageInput";
import SendButton from "../components/chat/SendButton";
import api from "../api/api";
import './ChatPage.css'
import {useKeycloak} from "@react-keycloak/web";

const ChatPage = () => {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const messageEndRef = useRef(null);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setMessage(e.target.value);
    };

    const handleSubmit = async () => {
        if (message.trim()) {
            const createdAt = new Date().toISOString();
            const messageToSend = {
                content: message,
                createdAt: createdAt,
            };

            api().post('/messages', messageToSend)
                .then(response => {
                    setMessages([...messages, {content: message, createdAt}]);
                    setMessage('');
                })
                .catch(error => {
                    console.error('Error sending message:', error);
                });
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSubmit();
        }
    };


    useEffect(() => {
        messageEndRef.current?.scrollIntoView({behavior: 'smooth'});
    }, [messages]);

    const {keycloak, initialized} = useKeycloak();

    useEffect(() => {
        const fetchMessages = async () => {
            api().get('/messages')
                .then(response => {
                    setMessages(response.data);
                })
                .catch(error => {
                    console.error('Error sending message:', error);
                });
        };
        if (keycloak.token) {
            fetchMessages();
        }
    }, [keycloak.token]);

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
            </div>

            <div className="chat-container">
                <div className="messages-container">
                    <Messages messages={messages}/>
                    <div ref={messageEndRef}/>
                </div>

                <div className="input-container">
                    <MessageInput
                        message={message}
                        handleChange={handleChange}
                        handleKeyDown={handleKeyDown}
                    />
                    <SendButton handleSubmit={handleSubmit}/>
                </div>
            </div>
        </div>
    );
};

export default ChatPage;
