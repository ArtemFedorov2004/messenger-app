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
    const [usernameErrors, setUsernameErrors] = useState(null);
    const [emailErrors, setEmailErrors] = useState(null);

    const submit = async () => {
        setIsLoading(true)
        try {
            await user.registration(username, email, password)
        } catch (e) {
            const response = e.response
            if (response?.status == 400) {
                const {detail} = response.data
                if (detail.includes('Имя пользователя')) {
                    setUsernameErrors(detail)
                } else if (detail.includes('Адрес электронной почты')) {
                    setEmailErrors(detail)
                }
            }

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
            <UsernameInput username={username} setUsername={setUsername} errors={usernameErrors}
                           setErrors={setUsernameErrors}/>
            <EmailInput email={email} setEmail={setEmail} errors={emailErrors}
                        setErrors={setEmailErrors}/>
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