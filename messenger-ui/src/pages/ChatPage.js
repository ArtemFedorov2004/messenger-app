import React, {useEffect, useRef, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import Messages from "../components/chat/Messages";
import MessageInput from "../components/chat/MessageInput";
import SendButton from "../components/chat/SendButton";
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

            fetch('http://localhost:8080/messages', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: keycloak.token ? `Bearer ${keycloak.token}` : '',
                },
                body: JSON.stringify(messageToSend),
            })
                .then(response => response.json())
                .then(data => {
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
                }
            ;
            if (keycloak.token) {
                fetchMessages();
            }
        }, [keycloak.token]
    )
    ;

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
