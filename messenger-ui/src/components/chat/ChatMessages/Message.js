import './Message.css';
import React from "react";

const Message = ({msg}) => {
    return (
        <div className="message-box my-message">
            <p>
                {msg.content}
                <br/><span>{msg.createdAt.toLocaleTimeString()}</span>
            </p>
        </div>
    );
};

export default Message;