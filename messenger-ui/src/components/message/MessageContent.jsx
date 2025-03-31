import React from 'react';
import './MessageContent.css';

const MessageContent = ({message}) => {
    return (
        <div className={`message-box ${message.senderId === '1' ? 'my-message' : 'friend-message'}`}>
            <p>
                {message.content}
                <br/>
                {message.editedAt === null
                    ?
                    <span>{new Date(message.createdAt).toLocaleTimeString()}</span>
                    :
                    <span>{new Date(message.editedAt).toLocaleTimeString()} (отредактировано)</span>
                }
            </p>
        </div>
    );
};

export default MessageContent;