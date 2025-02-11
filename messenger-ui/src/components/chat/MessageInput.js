import React from 'react';
import './MessageInput.css'

const MessageInput = ({message, handleChange, handleKeyDown}) => {
    return (
        <input
            type="text"
            value={message}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
            className="message-input"
        />
    );
};

export default MessageInput;
