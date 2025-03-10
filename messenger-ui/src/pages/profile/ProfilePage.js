import React, {useCallback, useState} from "react";
import "./ProfilePage.css";
import {useUser} from "../../contexts/UserContext";
import {useNavigate} from "react-router-dom";
import api from "../../api/api";

const ProfilePage = () => {
    const navigate = useNavigate();
    const {user, setUser, logout} = useUser();
    const [updatedUser, setUpdatedUser] = useState({
        username: user?.username || '',
        email: user?.email || '',
        firstname: user?.firstname || '',
        lastname: user?.lastname || '',
        address: user?.address || '',
        city: user?.city || '',
        country: user?.country || '',
        postalCode: user?.postalCode,
        aboutMe: user?.aboutMe || '',
    });

    const handleInputChange = (e) => {
        const {name, value} = e.target;
        setUpdatedUser((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const hasChanges = () => {
        return updatedUser.username !== user.username ||
            updatedUser.email !== user.email ||
            updatedUser.firstname !== user.firstname ||
            updatedUser.lastname !== user.lastname ||
            updatedUser.address !== user.address ||
            updatedUser.city !== user.city ||
            updatedUser.country !== user.country ||
            updatedUser.postalCode !== user.postalCode ||
            updatedUser.aboutMe !== user.aboutMe;
    };

    const handleSaveChanges = () => {
        if (!hasChanges()) {
            return;
        }

        api.put(`/users/${user.id}`, updatedUser)
            .then(response => {
                if (response.status === 204) {
                    setUser({
                        ...user,
                        ...updatedUser
                    });
                }
            })
            .catch(err => console.error(err))
    };

    const handleReturnToChat = useCallback(() => {
        navigate('/');
    }, [navigate]);

    const handleLogout = (e) => {
        logout();
    }

    return (
        <div className="container">
            <div className="left-container">
                <div className="card-body">
                    <h3>Мой аккаунт</h3>
                    <button
                        className="btn btn-edit"
                        onClick={handleSaveChanges}
                    >
                        Редактировать
                    </button>
                    <form>
                        <h6 className="heading-small text-muted mb-4">Информация о пользователе</h6>
                        <div>
                            <div className="col">
                                <div className="focused">
                                    <label className="form-control-label" htmlFor="input-username">
                                        Отображаемое имя
                                    </label>
                                    <input
                                        type="text"
                                        id="input-username"
                                        className="form-control form-control-alternative"
                                        placeholder="Отображаемое имя"
                                        name="username"
                                        value={updatedUser.username}
                                        onChange={handleInputChange}
                                    />
                                </div>
                            </div>
                            <div className="col">
                                <label className="form-control-label" htmlFor="input-email">Email</label>
                                <input
                                    type="email"
                                    id="input-email"
                                    className="form-control form-control-alternative"
                                    placeholder="jesse@example.com"
                                    name="email"
                                    value={updatedUser.email}
                                    onChange={handleInputChange}
                                />
                            </div>
                            <div>
                                <div className="col">
                                    <div className="focused">
                                        <label className="form-control-label" htmlFor="input-first-name">
                                            Имя
                                        </label>
                                        <input
                                            type="text"
                                            id="input-first-name"
                                            className="form-control form-control-alternative"
                                            placeholder="Имя"
                                            name="firstname"
                                            value={updatedUser.firstname}
                                            onChange={handleInputChange}
                                        />
                                    </div>
                                </div>
                                <div className="col">
                                    <div className="focused">
                                        <label className="form-control-label" htmlFor="input-last-name">
                                            Фамилия
                                        </label>
                                        <input
                                            type="text"
                                            id="input-last-name"
                                            className="form-control form-control-alternative"
                                            placeholder="Фамилия"
                                            name="lastname"
                                            value={updatedUser.lastname}
                                            onChange={handleInputChange}
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                        <hr className="my-4"/>
                        <h6 className="heading-small text-muted mb-4">Контактная информация</h6>
                        <div>
                            <div className="col">
                                <div className="focused">
                                    <label className="form-control-label" htmlFor="input-address">Адрес</label>
                                    <input
                                        id="input-address"
                                        className="form-control form-control-alternative"
                                        placeholder="Адрес"
                                        name="address"
                                        value={updatedUser.address}
                                        onChange={handleInputChange}
                                        type="text"
                                    />
                                </div>
                            </div>
                            <div>
                                <div className="col">
                                    <div className="focused">
                                        <label className="form-control-label" htmlFor="input-city">Город</label>
                                        <input
                                            type="text"
                                            id="input-city"
                                            className="form-control form-control-alternative"
                                            placeholder="Город"
                                            name="city"
                                            value={updatedUser.city}
                                            onChange={handleInputChange}
                                        />
                                    </div>
                                </div>
                                <div className="col">
                                    <div className="focused">
                                        <label className="form-control-label" htmlFor="input-country">Страна</label>
                                        <input
                                            type="text"
                                            id="input-country"
                                            className="form-control form-control-alternative"
                                            placeholder="Страна"
                                            name="country"
                                            value={updatedUser.country}
                                            onChange={handleInputChange}
                                        />
                                    </div>
                                </div>
                                <div className="col">
                                    <label className="form-control-label" htmlFor="input-postal-code">
                                        Почтовый индекс
                                    </label>
                                    <input
                                        type="number"
                                        id="input-postal-code"
                                        className="form-control form-control-alternative"
                                        placeholder="Почтовый индекс"
                                        name="postalCode"
                                        value={updatedUser.postalCode}
                                        onChange={handleInputChange}
                                    />
                                </div>
                            </div>
                        </div>
                        <hr className="my-4"/>
                        <h6 className="heading-small text-muted mb-4">Обо мне</h6>
                        <div className="focused">
                            <label>Обо мне</label>
                            <textarea
                                rows="4"
                                className="form-control form-control-alternative"
                                placeholder="Несколько слов о тебе ..."
                                name="aboutMe"
                                value={updatedUser.aboutMe}
                                onChange={handleInputChange}
                            />
                        </div>
                    </form>
                </div>
            </div>
            <div className="right-container">
                <div className="card-profile shadow">
                    <div className="card-profile-image">
                        <img
                            src="/logo512.png"
                            className="rounded-circle"
                            alt="Profile"
                        />
                    </div>
                    <div style={{display: "flex"}}>
                        <button className="btn btn-info">Изменить аватар</button>
                        <button
                            className="btn btn-info"
                            onClick={handleReturnToChat}
                        >
                            Вернуться в чат
                        </button>
                        <button
                            className="btn btn-default"
                            onClick={handleLogout}
                        >
                            Выйти
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;
