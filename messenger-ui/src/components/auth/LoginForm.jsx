import React, {useState} from 'react';
import {Button, Form} from "antd";
import {Link} from "react-router-dom";
import {REGISTRATION_ROUTE} from "../../utils/consts";
import UsernameInput from "./UsernameInput";
import PasswordInput from "./PasswordInput";

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    return (
        <Form
            name="login"
        >
            <h1 style={{textAlign: 'center'}}>Вход</h1>
            <UsernameInput username={username} setUsername={setUsername}/>
            <PasswordInput password={password} setPassword={setPassword}/>
            <Form.Item>
                <Button block type="primary" htmlType="submit">
                    Войти
                </Button>
                или <Link to={REGISTRATION_ROUTE}>Зарегистрируйтесь сейчас!</Link>
            </Form.Item>
        </Form>
    );
};

export default LoginForm;