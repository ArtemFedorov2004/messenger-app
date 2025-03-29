import React, {useState} from 'react';
import {Button, Form} from "antd";
import {LOGIN_ROUTE} from "../../utils/consts";
import {Link} from "react-router-dom";
import UsernameInput from "./UsernameInput";
import EmailInput from "./EmailInput";
import PasswordInput from "./PasswordInput";

const RegistrationForm = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    return (
        <Form
            name="login"
        >
            <h1 style={{textAlign: 'center'}}>Регистрация</h1>
            <UsernameInput username={username} setUsername={setUsername}/>
            <EmailInput email={email} setEmail={setEmail}/>
            <PasswordInput password={password} setPassword={setPassword}/>
            <Form.Item>
                <Button block type="primary" htmlType="submit">
                    Зарегистрироваться
                </Button>
                Есть аккаунт? <Link to={LOGIN_ROUTE}>Войдите</Link>
            </Form.Item>
        </Form>
    );
};

export default RegistrationForm;