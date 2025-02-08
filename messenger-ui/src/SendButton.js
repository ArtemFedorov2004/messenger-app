import React from 'react';

const SendButton = ({handleSubmit}) => {
    return (
        <button
            onClick={handleSubmit}
            style={{
                padding: '10px 20px',
                backgroundColor: '#4CAF50',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
            }}
        >
            Отправить сообщение
        </button>
    );
};

export default SendButton;