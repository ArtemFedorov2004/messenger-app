import React from 'react';
import Message from './Message';

const Messages = ({messages}) => {
    return (
        <div>
            {messages.map((msg, index) => (
                <Message key={index} msg={msg}/>
            ))}
        </div>
    );
};

export default Messages;