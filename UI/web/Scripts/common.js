
let currentUserSession = null;

function $post(url, data) {
    return $ajax('POST', url, data);
}

function $get(url, data) {
    return $ajax('GET', url, data);
}

function redirectUrl(url) {
    location.href = url;
}

function init() {
    $.ajaxSetup({
        dataType: "json",
        global: false,
        type: "POST"
    });

    currentUserSession = userSession();
}

function $ajax(verb, url, data) {
    return $.ajax(url, {
        type: 'GET',  // http method
        data: data,
        error: function (jqXhr, textStatus, errorMessage) {
            console.log('error!', textStatus, errorMessage);
        }
    });
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