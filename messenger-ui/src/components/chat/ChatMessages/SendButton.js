import React from 'react';
import './SendButton.css';
import {useKeycloak} from "@react-keycloak/web";

const SendButton = ({message, setMessage, messages, setMessages, stompClient, selectedUser}) => {
    const {keycloak, initialized} = useKeycloak();

    const handleSend = (e) => {
        e.preventDefault();
        if (stompClient && message.trim()) {
            const messageToSend = {
                senderId: keycloak.tokenParsed?.given_name,
                recipientId: selectedUser.nickName,
                content: message.trim(),
                timestamp: new Date(),
            };
            stompClient.send(`/app/chat`, {}, JSON.stringify(messageToSend));
            setMessages((prevMessages) => [...prevMessages, {
                senderId: keycloak.tokenParsed?.given_name,
                content: messageToSend.content
            }]);
            setMessage('');
        }
    };

    return (
        <button
            onClick={handleSend}
            className="send-button"
        >
            Отправить сообщение
        </button>
    );
};

export default SendButton;

/*const handleSubmit = async () => {
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
*/