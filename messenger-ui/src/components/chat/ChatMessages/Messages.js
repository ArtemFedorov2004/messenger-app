import React, {useEffect, useRef} from 'react';
import Message from './Message';
import './Messages.css';

const Messages = ({messages}) => {
    const messageEndRef = useRef(null);

    useEffect(() => {
        messageEndRef.current?.scrollIntoView({behavior: 'smooth'});
    }, [messages]);

    return (
        <div className="messages-container">
            {messages.map((msg, index) => (
                <Message key={index} msg={msg}/>
            ))}
            <div ref={messageEndRef}/>
        </div>
    );
};


export default Messages;