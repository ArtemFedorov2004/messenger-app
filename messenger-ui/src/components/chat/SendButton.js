import React from 'react';
import './SendButton.css';

const SendButton = ({handleSubmit}) => {
    return (
        <button
            onClick={handleSubmit}
            className="send-button"
        >
            Отправить сообщение
        </button>
    );
};

export default SendButton;