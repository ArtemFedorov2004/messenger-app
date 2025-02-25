import React, {useState} from "react";
import "./ProfilePage.css";

const ProfilePage = () => {
    const [user, setUser] = useState({
        username: "lucky.jesse",
        email: "jesse@example.com",
        firstName: "Lucky",
        lastName: "Jesse",
        address: "Bld Mihail Kogalniceanu, nr. 8 Bl 1, Sc 1, Ap 09",
        city: "New York",
        country: "United States",
        postalCode: "",
        aboutMe: "A beautiful Dashboard for Bootstrap 4. It is Free and Open Source.",
    });

    const handleInputChange = (e) => {
        const {name, value} = e.target;
        setUser((prevUser) => ({
            ...prevUser,
            [name]: value,
        }));
    };

    return (
        <div className="container">
            <div className="left-container">
                <div className="card-body">
                    <h3>Мой аккаунт</h3>
                    <button className="btn btn-edit">Редактировать</button>
                    <form>
                        <h6 className="heading-small text-muted mb-4">Информация о пользователе</h6>
                        <div>
                            <div className="col">
                                <div className="focused">
                                    <label className="form-control-label" htmlFor="input-username">Отображаемое имя</label>
                                    <input
                                        type="text"
                                        id="input-username"
                                        className="form-control form-control-alternative"
                                        placeholder="Отображаемое имя"
                                        name="username"
                                        value={user.username}
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
                                    value={user.email}
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
                                            name="firstName"
                                            value={user.firstName}
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
                                            name="lastName"
                                            value={user.lastName}
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
                                        value={user.address}
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
                                            value={user.city}
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
                                            value={user.country}
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
                                        value={user.postalCode}
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
                                value={user.aboutMe}
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
                            src="https://demos.creative-tim.com/argon-dashboard/assets-old/img/theme/team-4.jpg"
                            className="rounded-circle"
                            alt="Profile"
                        />
                    </div>
                    <div style={{display: "flex"}}>
                        <button className="btn btn-info">Изменить аватар</button>
                        <button className="btn btn-info">Вернуться в чат</button>
                        <button className="btn btn-default">Выйти</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;
