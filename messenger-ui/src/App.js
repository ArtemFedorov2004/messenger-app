import React, {useEffect, useRef, useState} from 'react';
import Messages from "./Messages";
import MessageInput from "./MessageInput";
import SendButton from "./SendButton";

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
            try {
                const response = await fetch('http://localhost:8080/messages', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({content: message, createdAt: createdAt}),
                });

                if (response.ok) {
                    setMessages([...messages, {content: message, createdAt}]);
                    setMessage('');
                }
            } catch (error) {
                console.error('Error sending message:', error);
            }
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
            try {
                const response = await fetch('http://localhost:8080/messages');
                if (response.ok) {
                    const data = await response.json();
                    setMessages(data);
                }
            } catch (error) {
                console.error('Error sending message:', error);
            }
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
