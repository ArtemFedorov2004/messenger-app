import React from 'react';
import {Col} from "antd";

const ProjectDescription = () => {
    return (
        <Col span={8}>
            <h1>Мессенджер на React и Spring Boot</h1>
            <p>
                Это простое веб-приложение для обмена сообщениями, созданное с использованием React на фронтенде и
                Spring Boot на бэкенде. Платформа предназначена для обмена текстовыми сообщениями в реальном времени.
            </p>

            <h2>Технологии, использованные в проекте:</h2>
            <ul>
                <li><strong>Frontend:</strong> React, JavaScript, HTML, CSS</li>
                <li><strong>Backend:</strong> Java, Spring Boot</li>
                <li><strong>База данных:</strong> MongoDB</li>
                <li><strong>Сервер:</strong> Tomcat, NGINX</li>
                <li><strong>Контейнеризация:</strong> Docker</li>
            </ul>

            <p>
                Исходный код проекта на <a href="https://github.com/ArtemFedorov2004/messenger-app" target="_blank"
                                           rel="noopener noreferrer">GitHub</a>.
            </p>

            <p>
                Для обратной связи пишите на почту: <a href="https://e.mail.ru/" target="_blank"
                                                       rel="noopener noreferrer">artem20.fedorov00@mail.ru</a>
            </p>
        </Col>
    );
};

export default ProjectDescription;