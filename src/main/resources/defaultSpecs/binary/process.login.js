function processLogIn(username, password, invalidCreds) {
    $.post('/session/login',
        {'username': username, 'password': password},
        (result) => {
            if (result.logged) {
                location.href = '/page/welcome'
            } else {
                M.Modal.getInstance(invalidCreds) .open();
            }
        });
}