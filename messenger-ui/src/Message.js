const Message = ({msg}) => {
    return (
        <div
            style={{
                marginBottom: '10px',
                padding: '10px',
                border: '1px solid #ccc',
                borderRadius: '4px',
                width: '50%',
                marginLeft: 'auto',
                backgroundColor: '#f1f1f1',
                wordWrap: 'break-word',
                position: 'relative',
            }}
        >
            <div>{msg.content}</div>
            <div
                style={{
                    fontSize: '12px',
                    color: '#888',
                    marginTop: '5px',
                    alignSelf: 'flex-end',
                }}
            >
                {new Date(msg.createdAt).toISOString()}
            </div>
        </div>
    );
};

export default Message;