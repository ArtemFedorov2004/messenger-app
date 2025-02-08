import React from 'react';

const MessageInput = ({message, handleChange, handleKeyDown}) => {
    return (
        <input
            type="text"
            value={message}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
            style={{
                padding: '10px',
                width: '80%',
                marginRight: '10px',
                borderRadius: '4px',
                border: '1px solid #ccc',
            }}
        />
    );
};

export default MessageInput;
