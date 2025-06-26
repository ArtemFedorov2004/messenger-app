export const rules = {
    required: (message = "Обязательное поле") => ({
        required: true,
        message
    }),

    email: (message = "Некорректный email") => ({
        type: 'email',
        message
    }),

    minLength: (min, message) => ({
        min,
        message: message || `Минимальная длина: ${min} символов`
    }),

    password: () => [
        rules.required("Пожалуйста введите пароль"),
        rules.minLength(8, "Пароль должен быть не менее 8 символов")
    ]
}