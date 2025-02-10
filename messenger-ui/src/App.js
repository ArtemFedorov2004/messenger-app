import React, {useEffect, useRef, useState} from 'react';
import Messages from "./Messages";
import MessageInput from "./MessageInput";
import SendButton from "./SendButton";
import api from "./api/api";

function App() {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const messageEndRef = useRef(null);

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

            api.post('/messages', messageToSend)
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

    useEffect(() => {
        const fetchMessages = async () => {
            api.get('/messages')
                .then(response => {
                    setMessages(response.data);
                })
                .catch(error => {
                    console.error('Error sending message:', error);
                });
        };

        fetchMessages();
    }, []);

    return (
        <div style={{
            display: 'flex',
            flexDirection: 'column',
            maxWidth: '100%',
            height: '100vh',
            width: '70%',
            marginLeft: 'auto'
        }}>
            <div
                style={{
                    flex: 1,
                    overflowY: 'auto',
                    marginBottom: '20px',
                    border: '1px solid #ccc',
                    borderRadius: '4px',
                    padding: '10px',
                    maxHeight: 'calc(100vh - 120px)',
                }}
            >
                <Messages messages={messages}/>
                <div ref={messageEndRef}/>
            </div>

            <div
                style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginBottom: '0px',
                }}
            >
                <MessageInput
                    message={message}
                    handleChange={handleChange}
                    handleKeyDown={handleKeyDown}
                />
                <SendButton handleSubmit={handleSubmit}/>
            </div>
        </div>
    );
}

export default App;
