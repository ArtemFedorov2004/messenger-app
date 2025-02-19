import React, {useState} from 'react';
import Chat from './Chat';

function Main() {
    const [isConnected, setIsConnected] = useState(false);

    const onLogout = () => {
        setIsConnected(false);
    };

    return (
        <div>
            <Chat onLogout={onLogout}/>
        </div>
    );
}

export default Main;