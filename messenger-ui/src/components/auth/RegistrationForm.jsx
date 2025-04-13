import React, {useContext, useState} from 'react';
import {Button, Form} from "antd";
import {LOGIN_ROUTE} from "../../utils/consts";
import {Link} from "react-router-dom";
import UsernameInput from "./UsernameInput";
import EmailInput from "./EmailInput";
import PasswordInput from "./PasswordInput";
import {observer} from "mobx-react-lite";
import {Context} from "../../index";
import {registration} from "../../service/AuthService";

const RegistrationForm = observer(() => {
    const {user} = useContext(Context);
    const [isLoading, setIsLoading] = useState(false);
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const submit = async () => {
        setIsLoading(true)
        try {
            const data = await registration(username, email, password)

            if (data) {
                user.setUser(data)
                user.setIsAuth(true)
            }
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <Form
            name="login"
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