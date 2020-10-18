let currentUserSession = null;

function $post(url, data) {
    return $ajax('POST', url, data);
}

function $get(url, data) {
    return $ajax('GET', url, data);
}

function $ajax(verb, url, data) {
    showLoader(true);

    return $.ajax(url, {
        type: verb,
        data: data,
        cache: false,
        error: function (jqXhr, textStatus, errorMessage) {

            console.log('error', textStatus, errorMessage);
            console.log('error', data.ErrorMessage);
        },
        complete: () => {
            showLoader(false);
        }
    });
}

function redirectUrl(url) {
    location.href = url;
}

function redirectToLogin(url) {
    redirectUrl(`${location.origin}/UI_Web_exploded/Pages/login/login.html`);
}

function init() {
    $.ajaxSetup({
        dataType: "json",
        global: false,
        type: "POST"
    });

    currentUserSession = userSession();
}


function setPermission() {

    if (!currentUserSession) {
        redirectToLogin();
    }

    if (currentUserSession.userType === 'CUSTOMER') {
        $('[role=store-owner-permission]').hide();
    }
}

function showLoader(flag) {
    if (flag) {

        $('.loading').show();
    } else {
        setTimeout(() => {
            $('.loading').hide();
        }, 200);

    }
}

function userSession(data) {
    if (data) {
        localStorage.setItem('userSession', JSON.stringify(data));
    } else {
        const storageData = localStorage.getItem('userSession');
        if (storageData) {
            return JSON.parse(storageData);
        }
    }
    return null;
}

init();


function showToaster(text) {

    $('#myToast').css({'opacity': 0, top: 0}).find('.toast-text').html(text);

    $("#myToast").fadeIn().animate({
        opacity: 0.9,
        top: "+=30",
    });

    setTimeout(() => {
        $("#myToast").animate({
            opacity: 0,
            top: "-=30",
        }).fadeOut()
    }, 3500)

}