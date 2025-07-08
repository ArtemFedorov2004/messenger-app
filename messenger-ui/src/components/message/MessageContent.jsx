import {observer} from "mobx-react-lite";
import React, {useContext} from "react";
import {Context} from "../../index";
import './MessageContent.css';

const MessageContent = observer(({message}) => {
    const {user} = useContext(Context);

    return (
        <div className={`message-box ${message.senderName === user.user.username ? 'my-message' : 'friend-message'}`}>
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
});

export default MessageContent;