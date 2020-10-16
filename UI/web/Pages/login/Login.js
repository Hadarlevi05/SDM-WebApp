function showLoginForm() {
    $('.error').hide();
    $('#loginForm').show()
    $('#signupForm').hide();
}

function showSignupForm() {
    $('.error').hide();
    $('#loginForm').hide();
    $('#signupForm').show()

}

function login(username) {
    return $get('../../login', {username})
        .then(data => {
            if (data.Status === 200) {
                $('.error').hide();
                redirectUrl(data.RedirectUrl);
            } else {
                $('.error').html(data.ErrorMessage).show();
            }
        });
}

function signup(username,type) {
    return $get('../../signup', {username,type})
        .then(data => {
            if (data.Status === 200) {
                $('.error').hide();
                redirectUrl(data.RedirectUrl);
            } else {
                $('.error').html(data.ErrorMessage).show();
            }
        });

}

$(function () {

    $('#loginForm').submit(() => {

        const val = $('#loginForm').find('input[name=username]').val();

        login(val);
        return false;
    });
    $('#signupForm').submit(() => {

        const username = $('#signupForm').find('input[name=username]').val();
        const type = $('#signupForm').find('input[name=type]:checked').val();

        signup(username,type);
        return false;
    });

})

