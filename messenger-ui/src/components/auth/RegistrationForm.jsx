import React, {useContext, useState} from 'react';
import {Button, Form} from "antd";
import {LOGIN_ROUTE} from "../../utils/consts";
import {Link} from "react-router-dom";
import UsernameInput from "./UsernameInput";
import EmailInput from "./EmailInput";
import PasswordInput from "./PasswordInput";
import {observer} from "mobx-react-lite";
import {Context} from "../../index";

const RegistrationForm = observer(() => {
    const {user} = useContext(Context);
    const [isLoading, setIsLoading] = useState(false);
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const submit = async () => {
        setIsLoading(true)
        try {
            await user.registration(username, email, password)
        } catch (e) {
            console.error(e.response.data)
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <Form
            name="register"
            onFinish={submit}
        >
            <h1 style={{textAlign: 'center'}}>Регистрация</h1>
            <UsernameInput username={username} setUsername={setUsername}/>
            <EmailInput email={email} setEmail={setEmail}/>
            <PasswordInput password={password} setPassword={setPassword}/>
            <Form.Item>
                <Button block type="primary" htmlType="submit" loading={isLoading}>
                    Зарегистрироваться
                </Button>
                Есть аккаунт? <Link to={LOGIN_ROUTE}>Войдите</Link>
            </Form.Item>
        </Form>
    );
});

export default RegistrationForm;