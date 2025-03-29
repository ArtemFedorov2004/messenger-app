export const rules = {
    required: (message = "Обязательное поле") => ({
        required: true,
        message
    })
}