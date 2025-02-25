import './Message.css';
import React from "react";
import {useUser} from "../../../contexts/UserContext";

const Message = ({msg}) => {
    const {user} = useUser();

    return (
        <div className={`message-box ${msg.senderId === user.id ? 'my-message' : 'friend-message'}`}>
            <p>
                {msg.content}
                <br/><span>{new Date(msg.createdAt).toLocaleTimeString()}</span>
            </p>
        </div>
    );
};

export default Message;