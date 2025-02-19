import React from 'react';
import './MessageInput.css'

const MessageInput = ({message, setMessage}) => {
    const handleChange = (e) => {
        setMessage(e.target.value);
    };

    return (
        <input
            type="text"
            value={message}
            onChange={handleChange}
            className="message-input"
        />
    );
};

export default MessageInput;
