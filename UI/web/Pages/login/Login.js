function showLoginForm() {
    $('#loginForm').show()
    $('#signupForm').hide();
}

function showSignupForm() {
    $('#loginForm').hide();
    $('#signupForm').show()
}

function login(username) {
    return $get('../../login', {username})
        .then(data => {
            if (data.status === 200) {
                $('.error').hide();
                location.href = data.redirectUrl;
            } else {
                $('.error').html(data.errorMessage).show();
            }
        });
}

function signup(username,type) {
    return $get('../../signup', {username,type})
        .then(data => {
            if (data.status === 200) {
                $('.error').hide();
                location.href = data.redirectUrl;
            } else {
                $('.error').html(data.errorMessage).show();
            }
        });

    // location.href = data.redirectUrl;

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

