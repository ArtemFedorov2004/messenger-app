import './Message.css';

const Message = ({msg}) => {
    return (
        <div className="message-container">
            <div>{msg.content}</div>
           {/* <div className="message-time">
                {new Date(msg.createdAt).toISOString()}
            </div>*/}
        </div>
    );
};

export default Message;